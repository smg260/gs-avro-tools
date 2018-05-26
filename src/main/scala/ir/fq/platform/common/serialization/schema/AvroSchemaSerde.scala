package ir.fq.platform.common.serialization.schema

import org.apache.avro.Schema

object AvroSchemaSerde {
  val Instance = new AvroSchemaSerde
}

class AvroSchemaSerde private() extends SchemaSerde[Schema] {

  override def deserialize(s: String): Schema = new Schema.Parser().parse(s)

  override def serialize(schema: Schema): String = schema.toString
}