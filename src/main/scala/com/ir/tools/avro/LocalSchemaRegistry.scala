package com.ir.tools.avro

import java.net.URL

import javax.annotation.concurrent.NotThreadSafe
import org.apache.avro.Schema

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

@NotThreadSafe
class LocalSchemaRegistry(basePath: String) {

  private val mapping = mutable.Map[String, Schema]()

  def lookup(msgType: String, version: String): Schema = {
    mapping.getOrElseUpdate(s"$msgType-$version", extract(msgType, version))
  }

  private def extract(msgType: String, version: String): Schema = {
    val schema = s"jar:file:$basePath/$version/data-common-$version.jar!/$msgType.avsc"
    val url = new URL(schema)
    Try(new Schema.Parser().parse(url.openStream())) match {
      case Success(parsed) => parsed
      case Failure(e) => throw new RuntimeException(s"$schema not found! Have you run gs-avro-tools update?")
    }
  }
}
