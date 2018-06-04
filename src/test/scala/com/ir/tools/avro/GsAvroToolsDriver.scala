package com.ir.tools.avro

object GsAvroToolsDriver extends App {
   GsAvroTools.main(
      Array(
         "--localrepo", "/Users/miralgadani/bin/gs-avro-tools-schemas"
         ,"tojson"
         ,"--avro","gs://fq-logs/merged/pixel/2018/05/30/13/part-r-7bbf93b7-4ac4-479c-8853-85402b62eb24-00071.avro"
         , "--human"
      )
   )
}
