package com.ir.tools.avro

import java.nio.ByteBuffer
import java.nio.channels.{Channels, FileChannel}
import java.nio.file.Paths

import com.google.cloud.ReadChannel
import com.google.cloud.storage.{BlobId, StorageOptions}
import com.ir.tools.avro.config.PrintConfig
import org.apache.avro.file.DataFileStream
import org.apache.avro.generic.GenericData.EnumSymbol
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.{BinaryDecoder, DecoderFactory}
import org.codehaus.jackson.map.ObjectMapper
import org.rogach.scallop.{ScallopConf, Subcommand}

import scala.collection.JavaConverters._

object GsAvroTools extends App {
  val Regex = "gs://([\\w-]*)/(.*)".r
  val conf = new Configuration(args)

  val schemaRegistry = new LocalSchemaRegistry(conf.localrepo())
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

  if(conf.tojson.human()) {
    PrintConfig(true)
  }

  var embeddedDecoder: BinaryDecoder = null

  val mapper = new ObjectMapper()

  try {
    if (conf.subcommand contains conf.tojson) {
      dfs.iterator().asScala.take(conf.tojson.number()).foreach { r =>
        val (extraInfo, record) = if (isEnvelope && !conf.tojson.nounwrap()) {
          val msgType = upperToCamel(r.get("type").asInstanceOf[EnumSymbol].toString)
          val schemaVersion = r.get("schemaVersion").asInstanceOf[String]
          val schema = schemaRegistry.lookup(msgType, schemaVersion)

          //reuse the decoder, and reader
          embeddedDecoder = DecoderFactory.get().binaryDecoder(r.get("body").asInstanceOf[ByteBuffer].array(), embeddedDecoder)

          (Some(s"Envelope|$msgType/v$schemaVersion"), new GenericDatumReader[GenericRecord](schema).read(null, embeddedDecoder))
        } else {
          (None, r)
        }

        if (conf.tojson.pretty()) {
          val obj = mapper.readValue(record.toString, classOf[AnyRef])
          if(conf.tojson.x() && extraInfo.isDefined) println(extraInfo.get)
          println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj))
          println()
        } else {
          if(conf.tojson.x() && extraInfo.isDefined) print(extraInfo.get + " >> ")
          println(s"$record")
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

  private def upperToCamel(s: String): String = s match {
    case "WEB_RTC" => "WebRTC"
    case _ =>
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
  version("GS Avro Tools v0.3")

  val localrepo = opt[String](required = true, name = "localrepo", descr = "Base directory containing commons schemas. Required for Envelope deserialisation")

  val tojson = new Subcommand("tojson") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
    val pretty = opt[Boolean](descr = "Pretty print the output")
    val number = opt[Int](default = Some(5), descr = "Number of records to show. Default: 5")
    val human = opt[Boolean](descr = "(BETA) Attempt to make data such as timestamps and ips human readable.")

    //unlikely that these will be used much
    val nounwrap = opt[Boolean](descr = "[Envelopes only] Will not unwrap body")
    val x = opt[Boolean](descr = "[Envelopes only] If unwrapping, show message type and version")

    mutuallyExclusive(nounwrap, x)
  }

  val getschema = new Subcommand("getschema") {
    val avro = opt[String](required = true, descr = "Location of avro file locally or in google storage (gs://)")
  }

  addSubcommand(tojson)
  addSubcommand(getschema)

  verify()
}