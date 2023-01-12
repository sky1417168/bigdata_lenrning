# CDH安装全纪录

## 前提准备

- #### JDK8安装包

  ![image-20230110143304048](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230110143304048.png)

- #### mysql安装包

  ![image-20230110143352304](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230110143352304.png)

- #### CM安装包

  ![image-20230110143437641](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230110143437641.png)

- #### CDH安装包

  ![image-20230110143522100](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230110143522100.png)

- #### 3台Linux服务器

  将安装包全部上传到服务器并分发

## 安装步骤

### 1、配置SSH免密登录

#### 1.1、 关闭防火墙

```bash
$ systemctl stop firewalld
$ systemctl disable firewalld.service
```

#### 1.2 配置免密

首先在sys-test-01上执行

```bash
$ ssh-keygen -t rsa
# 再回车三次
$ ssh-copy-id -i ~/.ssh/id_rsa.pub sys-test-02
$ ssh-copy-id -i ~/.ssh/id_rsa.pub sys-test-03
# 每台机器都要相应的执行一遍
```

### 2、安装JDK

#### 2.1、先检查系统是否有jdk

```bash
$ java -version
$ rpm -aq | grep java
```

#### 2.2、卸载系统自带jdk

```bash
$ rpm -e –nodeps java-1.8.0-openjdk-headless-1.8.0.242.b08-1.el7.x86_64 # 这里换成自己机器上的jdk
```

#### 2.3、安装IDK

上传安装包

![image-20230112094933157](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230112094933157.png)

```bash
$ tar -zxvf jdk-8uxxx-linux-x64.tar.gz -C /opt/module
$ mv /opt/module/jdk1.8.0_161/ /opt/module/jdk
# 然后配置环境变量
$ vim /etc/profile
JAVA_HOME=/opt/module/jdk
PATH=/opt/module/jdk/bin:$PATH
export JAVA_HOME
$ source /etc/profile
```

### 3、安装Mysql并创建CM要用的数据库

#### 3.1、安装Mysql

上传安装包

![image-20230112094955985](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230112094955985.png)

```bash
$ rpm -qa|grep mysql
# 如果有那先卸载
$ rpm -e --nodeps mysql-libs-5.1.73-7.el6.x86_64 #换成对应的包名
```

安装

```bash
# 服务器
$ rpm -ivh MySQL-server-5.6.24-1.el6.x86_64.rpm
$ cat /root/.mysql_secret # 查看生成的密码
$ service mysql status
$ service mysql start
# 客户端
$ rpm -ivh MySQL-client-5.6.24-1.el6.x86_64.rpm
$ mysql -uroot -pOEXaQuS8IWkG19Xs # 随机生成的密码
mysql> SET PASSWORD=PASSWORD('root');
mysql> quit
```

#### 3.2、创建数据库

```bash
mysql> mysql -uroot -proot
mysql> show databases;
mysql>use mysql;
mysql>select User, Host, Password from user;
mysql>
delete from user where Host='sys-test-01';
delete from user where Host='127.0.0.1';
delete from user where Host='::1';
mysql>flush privileges;
mysql> create database scm DEFAULT CHARSET utf8 COLLATE utf8_general_ci; # 集群监控
mysql> create database hive DEFAULT CHARSET utf8 COLLATE utf8_general_ci; # hive
mysql>quit;
```

### 4、安装第三方依赖

```bash
$ yum install -y bind-utils psmisc cyrus-sasl-plain cyrus-sasl-gssapi fuse portmap fuse-libs /lib/lsb/init-functions httpd mod_ssl openssl-devel python-psycopg2 MySQL-python libxslt # 所有节点
```

### 5、关闭SELINUX

```bash
$ vim /etc/selinux/config # 所有节点
# 将SELINUX=enforcing 改为SELINUX=disabled
SELINUX=disabled
```

### 6、配置NTP时间同步

```bash
$ yum -y install ntp # 所有节点
$ vi /etc/ntp.conf # 主节点sys-test-01
# 注释掉之前的server，然后添加以下NTP服务器
server ntp.aliyun.com

$ vi /etc/ntp.conf # 其他节点
# 注释掉之前的server，然后添加以下NTP服务器
server sys-test-01

# 重新启动 ntp 服务和设置开机自启
$ service ntpd restart # 所有节点
$ systemctl enable ntpd.servic # 所有节点

$ ntpdc -c loopinfo #查看与时间同步服务器的时间偏差
$ ntpq -p #查看当前同步的时间服务器
```

### 7、安装CM并启动CM

#### 7.1、安装CM

```bash
$ cd /opt/software # cm上传的路径 
$ yum install –y cloudera-manager-daemons-6.3.1-1466458.el7.x86_64.rpm # 所有节点
$ yum install –y cloudera-manager-daemons-6.3.1-1466458.el7.x86_64.rpm # 所有节点
$ yum install -y cloudera-manager-server-6.3.1-1466458.el7.x86_64.rpm # 主节点

$ vim /etc/cloudera-scm-agent/config.ini #所有agent节点
# 配置agent的server节点
server_host=sys-test-01

$ vim /etc/cloudera-scm-server/db.properties
# 修改mysql配置
com.cloudera.cmf.db.type=mysql
com.cloudera.cmf.db.host=localhost #(安装mysql的主机名)
com.cloudera.cmf.db.name=amon # 存放cm数据
com.cloudera.cmf.db.user=root #（用户名）
com.cloudera.cmf.db.setupType=EXTERNAL
com.cloudera.cmf.db.password=root #（mysql密码）
```

#### 7.2、启动CM

启动前，要将CDH包移到主节点的/opt/cloudera/parcel-repo目录下，否者后续安装检测不到包

```bash
$ systemctl start cloudera-scm-server # 启动server
# 耐心等待,观察server状态
$ systemctl status cloudera-scm-server
$ tail -200f /var/log/cloudera-scm-server/cloudera-scm-server.log
# 查看日期,直到出现 Started Jetty server 表示启动完成

$ systemctl start cloudera-scm-agent # 所有节点
$ systemctl status cloudera-scm- server # 查看状态,等待启动完成
```

打开网址http://sys-test-01:7180/ 验证

### 8、安装CDH以及模块

- 打开http://sys-test-01:7180/ 并登录
- 创建新的集群
- 分发CDH包(如果没有包，则需要将包重新复制到主节点的/opt/cloudera/parcel-repo目录，并重启cm)
- 检测机器状态，没啥大问题可以忽视风险
- 选择自定义，并选择需要的模块，hdfs，yarn，zookeeper，hive必备
- 连接数据库[hive]
- 安装模块

### 9、配置CDH

#### HDFS

##### 关闭验证

![image-20230111091028873](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111091028873.png)

##### 配置lzo压缩

1. 打开parcel

   ![image-20230111091242173](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111091242173.png)

2. 点击配置

   添加https://archive.cloudera.com/gplextras6/6.3.2/parcels/

   ![image-20230111091320581](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111091320581.png)

3. 回到parcel页面点击**下载**，下载完后点击**分配**，分配完后点击**激活**

   **![image-20230111091510809](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111091510809.png)**

4. 修改hdfs的压缩编码

   ![image-20230111111500692](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111111500692.png)

5. 修改yarn类路径

   ![image-20230111111731886](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111111731886.png)

6. 开启yarn压缩

   ![image-20230111111916507](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111111916507.png)

   ![image-20230111112056271](CDH%E5%AE%89%E8%A3%85%E5%85%A8%E7%BA%AA%E5%BD%95.assets/image-20230111112056271.png)



### 附录

#### 分发脚本

```shell
#!/bin/bash
#1. 判断参数个数
if [ $# -lt 1 ]
then
echo Not Enough Arguement!
exit;
fi
#2. 遍历集群所有机器
for host in sys-test-01 sys-test-02 sys-test-03 
do
echo ==================== $host ====================
#3. 遍历所有目录，挨个发送
for file in $@
do
#4. 判断文件是否存在
if [ -e $file ]
then
#5. 获取父目录
pdir=$(cd -P $(dirname $file); pwd)
#6. 获取当前文件的名称
fname=$(basename $file)
ssh $host "mkdir -p $pdir"
rsync -av $pdir/$fname $host:$pdir
else
echo $file does not exists!
fi
done
done
```

#### 批量命令执行脚本

```shell
#! /bin/bash
 
for i in sys-test-01 sys-test-02 sys-test-03 
do
    echo --------- $i ----------
    ssh $i "$*"
done
```

