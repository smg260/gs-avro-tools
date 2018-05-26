package ir.fq.platform.common.serialization.schema

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConversions._

/**
  * Rudimentary schema registry interface.  E.g., see <code>ProtocolAwareSchemaRegistry</code>.
  *
  * @tparam S type of schema
  */
abstract class SchemaRegistry[S](private var serde: SchemaSerde[S]) {
  import SchemaRegistry._

  // prefetch registered schemas, if any
  RegisteredSchemaByVersion.foreach {
    case (id, version) => prefetch(id, version)
  }
  /**
    *
    * @param id -- schema identifier, e.g., name of record
    * @param version  (optional) -- if unspecified, use Version.getVersion, i.e., compile-time dependency
    * @throws SchemaNotFoundException -- iff given schema id and version cannot be resolved
    * @return
    */
  @throws(classOf[SchemaNotFoundException])
  def getSchemaFor(id: String, version: String): S

  def deserialize(s: String): S = serde.deserialize(s)

  def serialize(schema: S): String = serde.serialize(schema)

  def getSchemaSerde: SchemaSerde[S] = serde

  def setSchemaSerde(schemaSerde: SchemaSerde[S]): Unit = {
    this.serde = schemaSerde
  }

  def prefetch(id: String, version: String): Unit = {
    // noop
  }
}

object SchemaRegistry {
  private[schema] val RegisteredSchemaByVersion = new ConcurrentHashMap[String, String]
  private[schema] val AnyVersion = ""

  /**
    *
    * Pre-register schema id and version for pre-fetching, which will occur when the class is instantiated.
    *
    * @param id
    * @param version  -- can be omitted by passing null, in which case an attempt to prefetch any version may be made
    */
  def registerSchemaForPrefetch(id: String, version: String) {
    RegisteredSchemaByVersion.put(id, if (version == null) AnyVersion else version)
  }
}