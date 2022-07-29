package com.code.example03

import org.apache.flink.api.scala._
import org.apache.flink.table.api.scala.BatchTableEnvironment

object Example03 {

  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val csvInput = env.readCsvFile("D:\\project\\my_project\\study\\bigdata_lenrning\\src\\data\\abc.csv")

    //使用table api创建逻辑表
    val tableEnv = BatchTableEnvironment.create(env)
    val table = tableEnv.fromDataSet(csvInput)
    tableEnv.registerTable("test_flink",table)
    //执行sql查询
    val result = tableEnv.sqlQuery("select * from test_flink")
    //输出结果
    val doubleDataSet = tableEnv.toDataSet(result)
    doubleDataSet.print();


  }

}


