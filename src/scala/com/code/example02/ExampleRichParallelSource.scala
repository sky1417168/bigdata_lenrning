package com.code.example02

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

class ExampleRichParallelSource extends RichParallelSourceFunction[Long]{

  var count = 1L

  var is_Running = true

  override def run(sourceContext: SourceFunction.SourceContext[Long]): Unit = {
    while (is_Running){
      sourceContext.collect(count)
      count += 1
      Thread.sleep(1000)
    }
  }

  override def cancel(): Unit = {
    is_Running = false
  }

  override def open(parameters: Configuration): Unit = super.open(parameters)

  override def close(): Unit = super.close()
}
