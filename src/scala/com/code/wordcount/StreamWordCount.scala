package com.code.wordcount

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._

object StreamWordCount {
  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val parameterTool = ParameterTool.fromArgs(args)

    val host = parameterTool.get("host")

    val port = parameterTool.getInt("port")

    val inputDataStream: DataStream[String] = env.socketTextStream(host, port)

    val resultDataStream: DataStream[(String, Int)] = inputDataStream
      .flatMap(_.split(" "))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    resultDataStream.print().setParallelism(1)

    env.execute("wordcount")


  }
}
