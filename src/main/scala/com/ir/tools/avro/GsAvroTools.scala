package com.ir.tools.avro

import java.nio.channels.{Channels, FileChannel}
import java.nio.file.Paths

import com.google.cloud.ReadChannel
import com.google.cloud.storage.{BlobId, StorageOptions}
import org.apache.avro.file.DataFileStream
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.DecoderFactory
import org.codehaus.jackson.map.ObjectMapper
import org.rogach.scallop.{ScallopConf, Subcommand}

import scala.collection.JavaConverters._

object GsAvroTools extends App {
  val Regex = "gs://([\\w-]*)/(.*)".r
  val conf = new Configuration(args)

  val avroPath = if(conf.subcommand contains conf.tojson) conf.tojson.avro() else conf.getschema.avro()

  val channel = if (avroPath.startsWith("gs://")) {
    val storage = StorageOptions.getDefaultInstance.getService
    val reader: ReadChannel = storage.reader(toBlobId(avroPath))
    reader.capture().restore()
  } else {
    FileChannel.open(Paths.get(avroPath))
  }

  val inputStream = Channels.newInputStream(channel)
  val dfs = new DataFileStream[GenericRecord](inputStream, new GenericDatumReader[GenericRecord]())

  val mapper = new ObjectMapper()

  try {
    if (conf.subcommand contains conf.tojson) {
      dfs.iterator().asScala.take(conf.tojson.number()).foreach { r =>
        if(r.getSchema.getName == "Envelope") {

        } else {
          if (conf.tojson.pretty()) {
            val obj = mapper.readValue(r.toString, classOf[AnyRef])
            println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
          } else {
            println(r)
          }
        }
      }
    } else if (conf.subcommand contains conf.getschema) {
      println(dfs.getSchema.toString(true))
    }
  } finally {
    dfs.close()
    inputStream.close()
    channel.close()
  }

  private def toBlobId(path: String): BlobId = {
    val (bucket, rel) = Regex.findFirstMatchIn(path) match {
      case Some(m) =>
        (m.group(1), m.group(2))
      case _ => throw new RuntimeException(s"Unable to parse google storage path $path")
    }

    BlobId.of(bucket, rel)
  }
}


class Configuration(args: Seq[String]) extends ScallopConf(args) {
  version("GS Avro Tools v0.1")

  val tojson = new Subcommand("tojson") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
    val pretty = opt[Boolean](descr = "Pretty print the output")
    val number = opt[Int](default = Some(5), descr = "Number of records to show. Default: 5")
  }

  val getschema = new Subcommand("getschema") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
  }

  addSubcommand(tojson)
  addSubcommand(getschema)

  verify()
}