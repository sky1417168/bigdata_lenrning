# log4j日志基础

log4j.properties

```properties
### 设置###
log4j.rootCategory=INFO,console,D,E,info

### 输出到控制台
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n

### 输出DEBUG 级别以上的日志到=logs/debug.log ###
log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = logs/debug.log
log4j.appender.D.Append = false
log4j.appender.D.Threshold = DEBUG
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n

### 输出INFO 级别以上的日志到=logs/info.log ###
log4j.appender.info = org.apache.log4j.DailyRollingFileAppender
log4j.appender.info.File = logs/info.log
log4j.appender.info.Append = false
log4j.appender.info.Threshold = INFO
log4j.appender.info.layout = org.apache.log4j.PatternLayout
log4j.appender.info.layout.ConversionPattern = %d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n

### 输出ERROR 级别以上的日志到=logs/error.log ###
log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
log4j.appender.E.File = logs/error.log
log4j.appender.E.Append = false
log4j.appender.E.Threshold = ERROR
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.layout.ConversionPattern = %d{yy/MM/dd HH:mm:ss} %p %c{1}: %m%n
```

设置日志内容

```java
import org.apache.log4j.Logger;

public class Test {
	private static Logger logger = Logger.getLogger(Test.class);  

    public static void main(String[] args) {  
        // 记录debug级别的信息  
        logger.debug("This is debug message.");  
        // 记录info级别的信息  
        logger.info("This is info message.");  
        // 记录error级别的信息  
        logger.error("This is error message.");  
    }  
}
```

```scala
import org.apache.log4j.Logger;

object Test {
	val logger = Logger.getLogger(Test.class);  

    def main(args:Array[String]):Unit = {  
        // 记录debug级别的信息  
        logger.debug("This is debug message.");  
        // 记录info级别的信息  
        logger.info("This is info message.");  
        // 记录error级别的信息  
        logger.error("This is error message.");  
    }  
}
```

## Log4j基本使用方法

Log4j由三个重要的组件构成：日志信息的优先级，日志信息的输出目的地，日志信息的输出格式。

日志信息的优先级从高到低有ERROR、WARN、 INFO、DEBUG，分别用来指定这条日志信息的重要程度

日志信息的输出目的地指定了日志将打印到控制台还是文件中

而输出格式则控制了日志信息的显 示内容。

1. 配置根Logger，其语法为：

   ```properties
   log4j.rootLogger = [ level ], appenderName1, appenderName2, ...
   ```

   其中，level 是日志记录的优先级，分为OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL或者自定义的级别，优 先级从高到低分别是ERROR、WARN、INFO、DEBUG

    appenderName就是指日志信息输出到哪个地方；可以同时指定多个输出目的地

2. 配置日志信息输出目的地Appender，其语法为

   ```properties
   log4j.appender.appenderName = fully.qualified.name.of.appender.class  
   log4j.appender.appenderName.option1 = value1  
   ...
   log4j.appender.appenderName.option = valueN
   ```

   其中，Log4j提供的appender有以下几种：

   ```properties
   org.apache.log4j.ConsoleAppender（控制台），  
   org.apache.log4j.FileAppender（文件），  
   org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件），  
   org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件），  
   org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）
   ```

3. 配置日志信息的格式（布局），其语法为：

   ```properties
   log4j.appender.appenderName.layout = fully.qualified.name.of.layout.class  
   log4j.appender.appenderName.layout.option1 = value1  
   ...
   log4j.appender.appenderName.layout.option = valueN
   ```

   其中，Log4j提供的layout有以下几种：

   ```properties
   org.apache.log4j.HTMLLayout（以HTML表格形式布局），  
   org.apache.log4j.PatternLayout（可以灵活地指定布局模式），  
   org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串），  
   org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息）
   ```

   打印参数如下:

   ```properties
   %m 输出代码中指定的消息
   %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL  
   %r 输出自应用启动到输出该log信息耗费的毫秒数  
   %c 输出所属的类目，通常就是所在类的全名  
   %t 输出产生该日志事件的线程名  
   %n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n”  
   %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyyy MMM dd HH:mm:ss},输出类似：2022 10 18  22：10：28 
   %l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。
   ```

   控制台选项

   ```properties
   Threshold=DEBUG:指定日志消息的输出最低层次。
   ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
   Target=System.err：默认情况下是：System.out,指定输出控制台
   FileAppender 选项
   Threshold=DEBUF:指定日志消息的输出最低层次。
   ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
   File=mylog.txt:指定消息输出到mylog.txt文件。
   Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。
   RollingFileAppender 选项
   Threshold=DEBUG:指定日志消息的输出最低层次。
   ImmediateFlush=true:默认值是true,意谓着所有的消息都会被立即输出。
   File=mylog.txt:指定消息输出到mylog.txt文件。
   Append=false:默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容。
   MaxFileSize=100KB: 后缀可以是KB, MB 或者是 GB. 在日志文件到达该大小时，将会自动滚动，即将原来的内容移到mylog.log.1文件。
   MaxBackupIndex=2:指定可以产生的滚动文件的最大数。
   log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %d{yyyy-MM-dd HH:mm:ssS} %c %m%n
   ```

## spark 使用log4j

首先在代码里加日志，就是scala语言里边加，见前面

提交任务到集群，一般情况下会使用集群配置的日志文件，想要自定义必须自己配置参数

```shell
spark-submit \
--master yarn \
--num-executors 60 \
--executor-cores 4 \
--driver-memory 1G \
--executor-memory 6G  \
--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:./conf/log4j.properties" \
--class com.job51.app.OnlineLoginAndRegister \
./jar/app_event_final.jar \
/user/hive/warehouse/tmp.db/all_udid_202203/ \
/pointlog/login_51_out \
20220301 \
20220315 \
/tmp/khj/app_event/tmp/out_final &
```

这里使用的是相对位置，不想要控制台输出就加上& 结尾，日志配置只输出到文件

```properties
log4j.rootCategory=INFO,file

### 输出INFO 级别以上的日志到=logs/debug.log ###
log4j.appender.file = org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.File = log/sprk-login-register-info.log
log4j.appender.file.Append = false
log4j.appender.file.Threshold = INFO
log4j.appender.file.layout = org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [%t] - [%p] %c - %l: %m%n
```

这样就能定制化日志输出了，方便查bug
