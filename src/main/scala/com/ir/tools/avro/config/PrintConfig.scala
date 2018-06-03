package com.ir.tools.avro.config

class PrintConfig private(val useConverters: Boolean = true) {

}


object PrintConfig {
  var instance: PrintConfig = apply()

  def apply(useConverters: Boolean = true): PrintConfig = {
    instance = new PrintConfig(useConverters)
    instance
  }
}