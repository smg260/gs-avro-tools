name := "gs-avro-tools"

version := "0.2"

scalaVersion := "2.12.6"

libraryDependencies += "org.apache.avro" % "avro" % "1.8.2"
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "1.29.0"
libraryDependencies += "org.rogach" %% "scallop" % "3.1.2"

mainClass in assembly := Some("com.ir.tools.avro.GsAvroTools")