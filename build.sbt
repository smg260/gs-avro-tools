name := "gs-avro-tools"

version := "0.3"

scalaVersion := "2.12.6"

libraryDependencies += "org.apache.avro" % "avro" % "1.8.2"
libraryDependencies += "com.google.cloud" % "google-cloud-storage" % "1.29.0"
libraryDependencies += "org.rogach" %% "scallop" % "3.1.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

mainClass in assembly := Some("com.ir.tools.avro.GsAvroTools")

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "avro", "generic", xs @ _*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


lazy val upload = taskKey[Unit]("Upload to gcs")

import scala.sys.process.stringToProcess

upload := {
  val upload = s"gsutil cp target/scala-2.12/gs-avro-tools-assembly-${version.value}.jar gs://miral/tools/avro/releases"
  val rename = s"gsutil cp target/scala-2.12/gs-avro-tools-assembly-${version.value}.jar gs://miral/tools/avro/releases/gs-avro-tools-assembly-latest.jar"

  stringToProcess(upload).!
  stringToProcess(rename).!
}