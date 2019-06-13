package com.ir.tools.avro

object GsAvroToolsDriver extends App {
   GsAvroTools.main(
      Array(
         "--localrepo", "/Users/miralgadani/bin/gs-avro-tools-schemas"
         ,"tojson"
         ,"--avro","gs://fq-logs-merged/pixel/2019/06/07/15/part-r-132669_0dccc86b-517b-4ae2-a615-1c93407788ac-00213.avro"
         , "--human"
         , "-x"
         , "--pretty"
      )
   )
}
