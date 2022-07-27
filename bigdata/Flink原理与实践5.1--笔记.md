## Flink原理与实践--笔记

### 基本概念

- 批处理：对一批数据进行处理
- 流处理：数据以流的方式持续不断的产生着，流处理就是对数据流进行处理

### lambda架构

![image-20220718100305738](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718100305738.png)

- 批处理层：首先持久化保存到批处理数据仓库，积累一段时间再用批处理引擎计算
- 流处理层：数据实时流入流处理层，流处理引擎计算得到结果，但是会有乱序等问题

### Kappa架构

![image-20220718100745833](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718100745833.png)

- Kappa架构相对更简单，实时性更好，所需的计算资源远小于Lambda架构

### 流处理概念

#### 延迟与吞吐

- **延迟**：表示一个事件系统处理的总时间，一般以毫秒为单位
  - 平均延迟
  - 分位延迟
- **吞吐**：表示一个系统最多能处理多少事件，一般以单位时间处理的事件数量为标准
  - 与引擎自身设计有关
  - 与数据源发送过来的事件数据量有关
  - 峰值吞吐量
  - 缓存
  - 反压
- 延迟与吞吐
  - 延迟与吞吐相互影响
  - 优化单节点内的计算速度
  - 使用并行策略，分而治之的处理数据

#### 窗口与时间

- **窗口**

  - 对于批处理任务，处理一个数据集，就是对该数据集对应的时间窗口内的数据进行处理

  - 滚动窗口：定义一个固定的窗口长度，长度是一个时间间隔，滚动向前，任意两个窗口之间不会包含同样的数据

    ![image-20220718102230290](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718102230290.png)

  - 滑动窗口：也设有一个固定是窗口长度，滚动窗口模式中滑动的时间间隔正好等于窗口的大小

    ![image-20220718102252487](C:\Users\haijun.kuang\AppData\Roaming\Typora\typora-user-images\image-20220718102252487.png)

  - 会话窗口：长度不固定，而是通过一个时间间隔来确定窗口，这个间隔称为会话间隔，当两个事件之间的间隔大于会话间隔，则会划分到不同的窗口中去

    ![image-20220718102316138](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718102316138.png)

#### 时间语义

- Event Time 和 Processing Time
  - Event Time：事件实际发生的时间
  - Processing Time：时间被流处理引擎处理的时间
  - Event Time 自发生起，就不再改变
  - 同一数据不同处理会产生不同的Processing Time
  - 根据Event Time复现一个事件的实际顺序、
  - Processing Time 针对准确性不高，实时性要求高的场景
- Watermark
  - Watermark是针对数据的实时性和准确性的一个折中方案，它假设在某个时间点上，不会有比这个时间点更晚的上报数据，当流处理引擎接收到一个Watermark后，就会粗发对当前时间窗口的计算

#### 状态与检查点

- 状态：流处理区别于批处理特有的概念

  ![image-20220718104148246](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718104148246.png)

- 检查点：主要作用是将中间数据保存下来，在流处理中，主要保存状态数据

#### 数据一致性保障

- At-Most-Once：每个时间最多被处理一次，也就是说，有可能有的事件直接被丢弃，不进行任何处理
- At-Least-Once：无论遇到何种状况，流处理引擎能够保证接收到的事件至少被处理一次，有些可能会被处理多次
- Exactly-Once：无论是否有故障重启，每个事件只能被处理一次

### Kafka概念

- Topic：通过topic区分不同的数据
- Producer：多个Producer发布数据到某个Topic下
- Consumer：多个Consumer分为一组，名为Consumer Group，一组Consumer Group订阅一个Topic下的数据

### Flink数据流图

![image-20220718140210346](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718140210346.png)

![image-20220718140233331](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718140233331.png)

![image-20220718140246236](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718140246236.png)

#### 数据交换策略

- 前向传播
- 按key分组
- 广播
- 随机

![image-20220718140342788](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718140342788.png)

### Flink分布式架构与核心组件

![image-20220718140608853](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718140608853.png)

- **Client**

- **Dispatcher**： Dispatcher可以接收多个作业，每接收一个作业，Dispatcher都会为这个作业分配一个JobManager
- **JobManager**： JobManager是单个Flink作业的协调者，一个作业会有一个JobManager来负责，jobManager会将Client提交的 JobGraph 转化为 ExecutionGraph，JobManager会向ResourceManager申请必要的资源，当获取足够的资源后，JobManager将ExecutionGraph以及具体的计算任务分发部署到多个TaskManager上，同时，JobManager还负责管理多个TaskManager，包括收集作业的状态信息、生成检查点、必要时进行故障恢复等
- **ResourceManager**
- **TaskManager**： TaskManager是实际负责执行计算的节点，一般地，一个Flink作业是分布在多个TaskManager上执行的，单个TaskManager上提供一定量的Slot，一个TaskManager启动后，相关Slot信息会被注册到ResourceManager中，当某个Flink作业提交后，ResourceManager会将空闲的Slot提供给JobManager。JobManager获取到空闲的Slot后会将具体的计算任务部署到空闲Slot之上，任务开始在这些Slot上执行

#### 组件栈

![0](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718141155448.png)

- 部署层

  - Local模式：SingleNode模式 或者SingleJVM模式

    Local-SingleJVM模式大多是开发和测试时使用的部署方式，该模式下JobManager和TaskManager都在同一个JVM里

  - Cluster模式：Standalone独立集群模式，YARN或Kubernetes集群

  - Cloud模式

- 运行时层

- API层：API层主要实现了流处理DataStream API和批处理DataSet API，包括转换（Transformation）、连接（Join）、聚合（Aggregation）、窗口（Window）以及状态（State）的计算

- 上层工具

  - 面向流处理的：复杂事件处理（Complex Event Process，CEP）
  - 面向批处理的：图（Graph Processing）Gelly计算库
  - 面向SQL用户的Table API和SQL
  - 针对Python用户推出的PyFlink

#### 任务执行与资源划分

![image-20220718141855033](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718141855033-16588889569281.png)

- StreamGraph：根据用户编写的代码生成的最初的图，用来表示一个Flink流处理作业的拓扑结构，节点StreamNode就是算子
- JobGraph： JobGraph是被提交给JobManager的数据结构，主要的优化为，将多个符合条件的节点链接在一起作为一个JobVertex节点，这样可以减少数据交换所需要的传输开销，这个链接的过程叫算子链（Operator Chain），JobVertex经过算子链后，会包含一到多个算子，它的输出是IntermediateDataSet，这是经过算子处理产生的数据集
- ExecutionGraph： JobManager将JobGraph转化为ExecutionGraph，ExecutionGraph是JobGraph的并行化版本：假如某个JobVertex的并行度是2，那么它将被划分为2个ExecutionVertex，ExecutionVertex表示一个算子子任务，它监控着单个子任务的执行情况，每个ExecutionVertex会输出一个IntermediateResultPartition，这是单个子任务的输出，再经过ExecutionEdge输出到下游节点，ExecutionJobVertex是这些并行子任务的合集，它监控着整个算子的执行情况
- 物理执行图：JobManager根据ExecutionGraph对作业进行调度后，在各个TaskManager上部署具体的任务，物理执行图并不是一个具体的数据结构

#### 任务，算子任务与算子链

![image-20220718142615351](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718142615351.png)

#### Slot与计算资源

- slot

TaskManager是一个JVM进程，在TaskManager中可以并行执行一到多个任务。每个任务是一个线程，需要TaskManager为其分配相应的资源，TaskManager使用Slot给任务分配资源

![image-20220718144421380](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718144421380.png)

- 槽位共享

![image-20220718144659556](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718144659556.png)

开启槽位共享后，Flink允许多个任务共享一个Slot

综上，Flink的一个Slot中可以执行一个算子子任务、也可以是被链接的多个子任务组成的任务，或者是共享Slot的多个任务，具体这个Slot上执行哪些计算由算子链和槽位共享两个优化措施决定

### DataStream API

#### Flink程序的骨架结构

- 设置执行环境

一个Flink作业必须依赖于一个执行环境：当调用getExecutionEnvironment()方法时，假如我们是在一个集群上提交作业，则返回集群的上下文；假如我们是在本地执行，则返回本地的上下文

Scala需要调用org.apache.flink.streaming.api.scala.StreamExecutionEnvironment和org.apache.flink.api.scala.ExecutionEnvironment，分别对应Scala的流处理和批处理执行环境。

```scala
import org.apache.flink.configuration.{Configuration, RestOptions}
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object FlinkTest {
  def main(args: Array[String]): Unit = {

    import org.apache.flink.api.scala._
      
    val conf = new Configuration()
    conf.setInteger(RestOptions.PORT,8081)

    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf)

    val text: DataStream[String] = env.socketTextStream("sys-test-04",6666)

    val wordCount: DataStream[(String, Int)] = text.flatMap(_.split("[ ,:]")).map((_,1)).keyBy(_._1).sum(1)
    wordCount.print()

    env.execute("SocketWordCount")
  }
}
```

- 读取数据源

```scala
val text: DataStream[String] = env.socketTextStream("sys-test-04",6666)
val consumer: DataStream[String] = env.addSource(consumer)
```

- 进行转换操作

在Transformation转换过程中，DataStream可能被转换为KeyedStream、WindowedStream、JoinedStream等不同的数据流结构

- 结果输出

我们需要将前面的计算结果输出到外部系统，目的地可能是一个消息队列、文件系统或数据库，或其他自定义的输出方式。输出结果的部分统称为Sink

- 执行

当定义好程序的Source、Transformation和Sink的业务逻辑后，程序并不会立即执行这些计算，我们还需要调用执行环境execute()方法来明确通知Flink去执行，Flink是延迟执行（Lazy Evaluation）的，即当程序明确调用execute()方法时，Flink才会将数据流图转化为一个JobGraph，提交给JobManager，JobManager根据当前的执行环境来执行这个作业。如果没有execute()方法，我们无法得到输出结果

#### 常见Transformation

- 常见的transformation

  - 单数据流基本转换
  - 基于key的分组转换
  - 多数据流转换
  - 数据重分布转换

  ![image-20220718152422141](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718152422141.png)

- 单数据流基本转换

  - map： MapFunction和RichMapFunction
  - filter：经过Filter函数返回为True的，将被保留
  - flatMap： flatMap()的输出可以是零个、一个或多个元素。当输出元素是一个列表时，flatMap()会将列表展平

- 基于key的分组转换

  - keyBy： keyBy()将DataStream转换成一个KeyedStream
  - Aggregation：常见的聚合（Aggregation）函数有sum()、max()、min()等
  - 对于一个KeyedStream，一次只能使用一个聚合函数，无法链式使用多个
  - reduce： reduce()在KeyedStream上生效，它接受两个输入，生成一个输出，即两两合一地进行汇总操作，生成一个同类型的新元素

- 多数据流转换

  - union：数据将按照先进先出（First In First Out）的模式合并，且不去重，多个数据流的数据类型必须相同
  - connect：
    - connect()只能连接两个数据流，union()可以连接多个数据流
    - connect()所连接的两个数据流的数据类型可以不一致，union()所连接的两个或多个数据流的数据类型必须一致
    - 两个DataStream经过connect()之后被转化为ConnectedStreams，ConnectedStreams会对两个流的数据应用不同的处理方法，且两个流之间可以共享状态
    - Flink并不能保证map1()/flatMap1()和map2()/flatMap2()两个方法调用的顺序，两个方法的调用顺序依赖于两个数据流中数据流入的先后顺序

![image-20220718154730482](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718154730482.png)

- 并行度与数据重分布

  - 并行度：默认情况下，一个作业所有算子的并行度会依赖于这个作业的执行环境

  - 数据重分布

    - shuffle：基于正态分布，将数据随机分布到下游个算子任务上

    ```scala
    dataStream.shuffle()
    ```

    - rebalance与rescale： 

      rebalance()使用Round-Ribon方法将数据均匀分布到各子任务上

    ```scala
    dataStream.rebalance()
    ```

    ​	   rescale()与rebalance()很像，也是将数据均匀分布到下游各子任务上，但它的传输开销更小，因为rescale()并不是将每个数据轮询地发送给下游每个子任务，而是就近发送给下游子任务

    ```scala
    dataStream.rescale()
    ```

    - broadcast：广播，数据可被复制并广播发送给下游的所有子任务

    ```scala
    dataStream.broadcast()
    ```

    - global： global()会将所有数据发送给下游算子的第一个子任务上，使用global()时要小心，以免造成严重的性能问题
    - partitionCustom：自定义数据重分布逻辑，它有两个参数：第一个参数是自定义的Partitioner，我们需要重写里面的partition()方法；第二个参数表示对数据流哪个字段使用partiton()方法



#### 数据类与序列化

- flink支持的数据类型

  ![image-20220718161641619](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718161641619.png)

  

- TypeInformation

![image-20220718162058461](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718162058461.png)

- 注册类

如果传递给Flink算子的数据类型是父类，实际执行过程中使用的是子类，子类中有一些父类没有的数据结构和特性，将子类注册可以提高性能。在执行环境上调用env.registerType(clazz)来注册类

- Avro

  ```xml
  <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-avro</artifactId>
      <version>${flink.version}</version>
  </dependency>
  ```

  - Avro Specific： Avro使用JSON定义Schema，再经过工具转换，将Schema生成Java类，所生成的Java类和POJO很像，类里面带有getter和setter方法，这种将Avro Schema转换成Java类的方式被称为Avro Specific模式。因为Avro为我们生成了具体的类
  - Avro Generic： Avro另一种更为通用的模式是Avro Generic模式。Avro Generic模式不生成具体的Java类，而是用一个通用的GenericRecord来封装所有用户定义的数据结构，在Avro Generic模式下，无论具体什么数据结构，数据流使用的是GenericRecord这个通用的结构，Flink并不能自动支持Avro Generic模式，需要提供具体的Schema信息。具体有两种方式：①在Source实现ResultTypeQueryable\<GenericRecord>接口，告知数据的TypeInformation；②在Source之后，使用returns()方法告知数据的TypeInformation。

- Kryo

  - 如果一个数据类型被识别为GenericTypeInfo，Flink会使用Kryo作为后备选项进行序列化
  - 使用Kryo时，最好对数据类型和序列化器进行注册，注册之后在一定程度上能提升性能
  - Kryo在有些流处理场景效率不高，有可能造成流数据的积压
  - 我们可以使用env.getConfig.disableGenericTypes()；来禁用Kryo。禁用后，Flink遇到无法处理的数据类型将抛出异常

- Thrift和Protobuf

  - 与Avro Specific模式相似，Thrift和Protobuf都有一套使用声明式语言来定义Schema的方式，可以使用工具将声明式语言转化为Java类

#### 用户自定义函数

需要注意的是，使用这些函数时，一定要保证函数内的所有内容都可以被序列化。如果有一些不能被序列化的内容，就使用后文介绍的RichFunction函数类，或者重写Java的序列化和反序列化方法

#### Rich函数类

比起不带Rich前缀的函数类，Rich函数类增加了如下方法：

- open()方法：Flink在算子调用前会执行这个方法，可以用来进行一些初始化工作。
- close()方法：Flink在算子最后一次调用结束后执行这个方法，可以用来释放一些资源。
- getRuntimeContext()方法：获取运行时上下文。

### 时间和窗口

### flink的时间语义

![image-20220718165628569](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718165628569.png)

- Event Time： Event Time指的是数据流中每个元素或者每个事件自带的时间属性，一般是事件发生的时间

在实际应用中，当涉及对事件按照时间窗口进行统计时，Flink会将窗口内的事件缓存下来，直到接收到一个Watermark，Watermark假设不会有更晚到达的事件。Watermark意味着在一个时间窗口下，Flink会等待一个有限的时间，这在一定程度上降低了计算结果的绝对准确性，而且增加了系统的延迟

使用Event Time的好处是某个事件的时间是确定的，这样能够保证计算结果在一定程度上的可预测性。

一个基于Event Time的Flink程序中必须定义：每条数据的Event Time时间戳；如何生成Watermark

我们可以使用数据自带的时间作为Event Time，也可以在数据到达Flink后人为给Event Time赋值

使用Event Time的优势是结果的可预测性，缺点是缓存较大，增加了延迟，且调试和定位问题更复杂

- Processing Time： 对于某个算子来说，Processing Time指使用算子的当前节点的操作系统时间

Processing Time只依赖当前节点的操作系统时间，不需要依赖Watermark，无须缓存。相比其他时间语义，基于Processing Time的作业实现起来更简单，延迟更小。

- Ingestion Time： Ingestion Time是事件到达Flink Source的时间

从Source到下游各个算子中间可能有很多计算环节，任何一个算子处理速度的快慢可能影响下游算子的Processing Time。而Ingestion Time定义的是数据流最早进入Flink的时间，因此不会被算子处理速度影响

- 设置时间语义

```scala
env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
```

如果想使用另外两种时间语义，需要用TimeCharacteristic.ProcessingTime或TimeCharacteristic.IngestionTime替换

- Event Time和Watermark
  - 果我们要使用Event Time语义，以下两项配置缺一不可：第一，使用一个时间戳为数据流中每个事件的Event Time赋值；第二，生成Watermark。
  - Watermark是Flink插入到数据流中的一种特殊的数据结构，它包含一个时间戳，并假设后续不会有小于该时间戳的数据
  - Watermark与事件的时间戳紧密相关。一个时间戳为t的Watermark会假设后续到达事件的时间戳都大于t
  - 假如Flink算子接收到一个违背上述规则的事件，该事件将被认定为迟到数据
  - Watermark时间戳必须单调递增，以保证时间不会倒流。
  - Watermark机制允许用户来控制准确度和延迟。

![image-20220718172747427](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220718172747427.png)

- 分布式环境下Watermark的传播
  - Flink某算子子任务根据上游流入的各Watermark来更新Partition Watermark列表。
  - 选取Partition Watermark列表中最小的时间戳作为该算子子任务的Event Time，并将Event Time发送给下游算子。
  - 根据系统当前的时间，周期性地生成Watermark解决上游分区Watermark不更新
  - 一旦发现某个数据流不再生成新的Watermark，我们要在SourceFunction中的SourceContext里调用markAsTemporarilyIdle()来设置该数据流为空闲状态，避免空转

![image-20220727103023488](Flink%E5%8E%9F%E7%90%86%E4%B8%8E%E5%AE%9E%E8%B7%B55.1--%E7%AC%94%E8%AE%B0.assets/image-20220727103023488.png)



- 设置时间戳及生成Watermark

  - 因为时间戳在后续处理中都会用到，所以时间戳的设置要在任何时间窗口操作之前
  - 时间戳和Watermark的设置只对Event Time时间语义起作用，如果一个作业基于Processing Time或Ingestion Time，那设置时间戳没有什么意义
  - 通过assignTimestampsAndWatermarks()方法来设置
  - WatermarkStrategy.forGenerator(...).withTimestampAssigner(...)链式调用了两个方法，forGenerator()方法用来生成Watermark，withTimestampAssigner()方法用来为数据流的每个元素设置时间戳




......待更新





































