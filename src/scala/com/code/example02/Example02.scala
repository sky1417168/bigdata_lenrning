package com.code.example02

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object Example02 {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(2)

    import org.apache.flink.api.scala._

    val text = env.addSource(new ExampleSource)

    val tupleData = text.map { line =>
      Tuple1(line)
    }

    val partitionerData = tupleData.partitionCustom(new ExamplePartitioner, 0)

    val result = partitionerData.map { x =>
      println("当前线程id:" + Thread.currentThread().getId + ",value:" + x)
      x._1
    }

    result.print().setParallelism(1)

    env.execute("StreamingDemo")


  }
}
