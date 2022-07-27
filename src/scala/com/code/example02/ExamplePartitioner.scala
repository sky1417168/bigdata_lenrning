package com.code.example02

import org.apache.flink.api.common.functions.Partitioner

class ExamplePartitioner extends Partitioner[Long]{
  override def partition(key: Long, numPartitions: Int): Int = {
    println("分区数" + numPartitions)

    if (key % 2 ==0){
      0
    }else{
      1
    }

  }
}
