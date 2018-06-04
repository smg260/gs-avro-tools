package com.ir.tools.avro.config

class PrintConfig private(val useConverters: Boolean = true) {

}


object PrintConfig {
  var instance: PrintConfig = apply()

  def apply(useConverters: Boolean = false): PrintConfig = {
    instance = new PrintConfig(useConverters)
    instance
  }
}