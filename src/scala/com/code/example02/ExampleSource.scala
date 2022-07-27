package com.code.example02

import org.apache.flink.streaming.api.functions.source.SourceFunction

class ExampleSource extends SourceFunction[Long]{

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
}
