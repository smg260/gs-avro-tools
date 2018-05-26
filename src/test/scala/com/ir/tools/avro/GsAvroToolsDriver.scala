package com.ir.tools.avro

object GsAvroToolsDriver extends App {
   GsAvroTools.main(
      Array(
         "--localrepo", "/Users/miralgadani/bin/gs-avro-tools-schemas"
         ,"tojson"
         ,"--avro","gs://fq-logs/raw/2018/03/02/23/tracking-api-pixel-us-east4-a-999-0-2018-03-02T23-00Z.avro"
         , "-x"
      )
   )
}
