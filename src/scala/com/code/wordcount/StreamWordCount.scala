package com.code.wordcount

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._

object StreamWordCount {
  def main(args: Array[String]): Unit = {

    // 创建环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 从命令行读取参数, 格式为 --host localhost --port 7777
    val parameterTool = ParameterTool.fromArgs(args)

    // host
    val host = parameterTool.get("host")

    // port
    val port = parameterTool.getInt("port")

    // 首先 nc -lk 7777 开启socket
    // 从socket读取数据流
    val inputDataStream: DataStream[String] = env.socketTextStream(host, port)

    // 数据转换
    val resultDataStream: DataStream[(String, Int)] = inputDataStream
      .flatMap(_.split(" "))
      .filter(_.nonEmpty)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    // 打印流
    resultDataStream.print().setParallelism(1)

    // 启动
    env.execute("wordcount")


  }
}
