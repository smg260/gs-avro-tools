package ir.fq.platform.common.serialization.schema

import scala.io.Source

class ProtocolAwareSchemaRegistry[S](val basePath: String, val schemaFileExtension: String = "avsc", val serde: SchemaSerde[S]) extends SchemaRegistry[S](serde) {

  override def getSchemaFor(id: String, version: String): S = {
    this.deserialize(Source.fromFile(s"$basePath/$version/$id.$schemaFileExtension").mkString)
  }
}