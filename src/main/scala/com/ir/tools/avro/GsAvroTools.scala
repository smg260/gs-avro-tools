package com.ir.tools.avro

import java.nio.ByteBuffer
import java.nio.channels.{Channels, FileChannel}
import java.nio.file.Paths

import com.google.cloud.ReadChannel
import com.google.cloud.storage.{BlobId, StorageOptions}
import org.apache.avro.file.DataFileStream
import org.apache.avro.generic.GenericData.EnumSymbol
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.{BinaryDecoder, DecoderFactory}
import org.codehaus.jackson.map.ObjectMapper
import org.rogach.scallop.{ScallopConf, Subcommand}

import scala.collection.JavaConverters._

import sys.process._

object GsAvroTools extends App {
  val Regex = "gs://([\\w-]*)/(.*)".r
  val conf = new Configuration(args)

  if (conf.subcommand contains conf.updateschemas) {
    val baseCmd = "gsutil -m rsync -n -x \".*sources.*|.*pom|.*md5|.*sha1|.*xml\" -r"
    val fullCmd = s"$baseCmd ${conf.updateschemas.remoterepo()} ${conf.localrepo()}"
    println(fullCmd)
    fullCmd!
  } else {
    val schemaRegistry = new SchemaRegistry(conf.localrepo())
    val avroPath = if (conf.subcommand contains conf.tojson) conf.tojson.avro() else conf.getschema.avro()

    val channel = if (avroPath.startsWith("gs://")) {
      val storage = StorageOptions.getDefaultInstance.getService
      val reader: ReadChannel = storage.reader(toBlobId(avroPath))
      reader.capture().restore()
    } else {
      FileChannel.open(Paths.get(avroPath))
    }

    val inputStream = Channels.newInputStream(channel)
    val dfs = new DataFileStream[GenericRecord](inputStream, new GenericDatumReader[GenericRecord]())

    val isEnvelope = dfs.getSchema.getName == "Envelope"

    if (isEnvelope) {
      println("** Displaying unwrapped envelopes **")
    }

    var embeddedDecoder: BinaryDecoder = null
    val embeddedReader = new GenericDatumReader[GenericRecord]()

    val mapper = new ObjectMapper()

    try {
      if (conf.subcommand contains conf.tojson) {
        dfs.iterator().asScala.take(conf.tojson.number()).foreach { r =>
          val record = if (isEnvelope) {
            val msgType = upperToCamel(r.get("type").asInstanceOf[EnumSymbol].toString)
            val schemaVersion = r.get("schemaVersion").asInstanceOf[String]
            val schema = schemaRegistry.lookup(msgType, schemaVersion)

            //reuse the decoder, and reader
            embeddedDecoder = DecoderFactory.get().binaryDecoder(r.get("body").asInstanceOf[ByteBuffer].array(), embeddedDecoder)
            embeddedReader.setSchema(schema)
            embeddedReader.read(null, embeddedDecoder)
          } else {
            r
          }

          if (conf.tojson.pretty()) {
            val obj = mapper.readValue(record.toString, classOf[AnyRef])
            println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
          } else {
            println(record)
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
  }

  private def toBlobId(path: String): BlobId = {
    val (bucket, rel) = Regex.findFirstMatchIn(path) match {
      case Some(m) =>
        (m.group(1), m.group(2))
      case _ => throw new RuntimeException(s"Unable to parse google storage path $path")
    }

    BlobId.of(bucket, rel)
  }

  private def upperToCamel(s: String): String = {
    var lastCharWasUnderscore = true
    s.flatMap { c =>
      if (c == '_') {
        lastCharWasUnderscore = true
        Iterator.empty
      } else {
        if (!lastCharWasUnderscore) {
          Iterator(c.toLower)
        } else {
          lastCharWasUnderscore = false
          Iterator(c)
        }
      }
    }
  }
}


class Configuration(args: Seq[String]) extends ScallopConf(args) {
  version("GS Avro Tools v0.1")

  val localrepo = opt[String](required = true, name = "localrepo", descr = "Base directory containing commons schemas. Required for Envelope deserialisation")

  val tojson = new Subcommand("tojson") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
    val pretty = opt[Boolean](descr = "Pretty print the output")
    val number = opt[Int](default = Some(5), descr = "Number of records to show. Default: 5")
    val x = opt[Boolean](descr = "Show extended information")
  }

  val getschema = new Subcommand("getschema") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
  }

  val updateschemas = new Subcommand("updateschemas") {
    val remoterepo = opt[String](required = true, name = "remoterepo", descr = "remoterepo directory containing schemas",
      default = Some("gs://fq-platform/artifacts/releases/fq/platform/data-common/"))
  }

  addSubcommand(tojson)
  addSubcommand(getschema)
  addSubcommand(updateschemas)

  verify()
}