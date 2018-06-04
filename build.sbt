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
lazy val updateLocal = taskKey[Unit]("Copy script and jar to ~/bin")

import scala.sys.process.stringToProcess

upload := {
  val uploadScript = "gsutil cp gs-avro-tools gs://miral/tools/avro/gs-avro-tools"
  val upload = s"gsutil cp target/scala-2.12/gs-avro-tools-assembly-${version.value}.jar gs://miral/tools/avro/releases"
  val rename = s"gsutil cp target/scala-2.12/gs-avro-tools-assembly-${version.value}.jar gs://miral/tools/avro/releases/gs-avro-tools-assembly-latest.jar"

  stringToProcess(uploadScript).!
  stringToProcess(upload).!
  stringToProcess(rename).!
}

updateLocal := {
  val copyScript = "cp gs-avro-tools /Users/miralgadani/bin/"
  val copyJar = s"cp target/scala-2.12/gs-avro-tools-assembly-${version.value}.jar /Users/miralgadani/bin/gs-avro-tools-lib/gs-avro-tools-assembly-latest.jar"

  stringToProcess(copyScript).!
  stringToProcess(copyJar).!
}