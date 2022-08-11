package com.code.window

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object StreamWindowWaterMark {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    env.setParallelism(1)

    val inputStream = env.socketTextStream("sys-test.sql-02", 7777)

    val dataStream = inputStream
      .map{x =>
        val arr = x.split(",")
        (arr(0),arr(1).toLong)

      }
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[(String,Long)](Time.milliseconds(3)) {
        override def extractTimestamp(t: (String,Long)): Long = t._2
      })


    val resultStream = dataStream
      .keyBy(_._1)
      .timeWindow(Time.seconds(3))
      .allowedLateness(Time.minutes(1))
      .sideOutputLateData(new OutputTag[(String, Long)]("late"))
      .reduce((x,y) => (y._1,x._2.max(y._2)))

    resultStream.print("result")

    env.execute()


  }

}
