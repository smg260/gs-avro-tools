package com.ir.tools.avro

object GsAvroToolsDriver extends App {
   GsAvroTools.main(
      Array(
         "--localrepo", "/Users/miralgadani/bin/jars/commons"
         ,"tojson"
         ,"--avro","gs://fq-logs/merged/firewall_prebid/2018/05/23/15/part-r-7832b62c-03ad-4532-a2e3-9b82335f5f84-00072.avro"
      )
   )
}
