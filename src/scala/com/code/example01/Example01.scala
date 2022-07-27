package com.code.example01

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

object Example01 {
  def main(args: Array[String]): Unit = {

    val host = try {
      ParameterTool.fromArgs(args).get("host")
    } catch {
      case _: Exception => {
        println("No host set. use default localhost.")
      }
        "localhost"
    }

    val port = try {
      ParameterTool.fromArgs(args).getInt("port")
    } catch {
      case _: Exception => {
        println("No port set. use default port 9000.")
      }
        9000
    }

    // 运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 输入数据
    val text = env.socketTextStream(host, port)

    // 导入隐式转换

    import org.apache.flink.api.scala._

    val windowCounts = text.flatMap(_.split("\\s"))
      .map((_, 1)) // (word,1)
      .keyBy(0) // 以第一个字段,也就是word分组
      .timeWindow(Time.seconds(2), Time.seconds(1)) //指定窗口大小，窗口间隔
      .sum(1) // 对第二个字段聚合

    // 打印到控制台
    windowCounts.print().setParallelism(1)

    // 执行任务
    env.execute("Socket window count")


  }

}
