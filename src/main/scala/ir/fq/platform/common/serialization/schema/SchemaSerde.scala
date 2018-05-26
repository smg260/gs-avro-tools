package ir.fq.platform.common.serialization.schema

abstract class SchemaSerde[S] {
  private[schema] def serialize(s: S): String
  private[schema] def deserialize(s: String): S
}