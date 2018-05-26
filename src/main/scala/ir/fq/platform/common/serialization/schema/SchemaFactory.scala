package ir.fq.platform.common.serialization.schema

import org.apache.avro.Schema

object SchemaFactory {
  val Prefix = "schema.registry"
  val BasePathParamName = "basepath"
  val ExtensionParamName = "extension"
  val ArchiveParamName = "archive"
  val ArchivePrefixParamName = "archive.prefix"
  val ArchiveSuffixParamName = "archive.suffix"
  val DefaultRegistryName = "default"

  def createInMemoryAvroSchemaRegistry(conf: Config): SchemaRegistry[Schema] = {
    val defaultRegistry = conf.getConfig(Prefix).getConfig(DefaultRegistryName)
    val basePath = defaultRegistry.getString(BasePathParamName)
    val extension = defaultRegistry.getString(ExtensionParamName)

    val schemaRegisty =
      if (defaultRegistry.hasPath(ArchiveParamName)) {
        val prefix = if (defaultRegistry.hasPath(ArchivePrefixParamName)) defaultRegistry.getString(ArchivePrefixParamName) else ""
        val suffix = if (defaultRegistry.hasPath(ArchiveSuffixParamName)) defaultRegistry.getString(ArchiveSuffixParamName) else ""

        new JarSchemaRegistry[Schema](basePath, (version: String) => prefix + "-" + version + suffix, extension, AvroSchemaSerde.Instance)
      } else {
        new ProtocolAwareSchemaRegistry[Schema](basePath, extension, AvroSchemaSerde.Instance)
      }

    new InMemorySchemaRegistry[Schema](schemaRegisty)
  }
}
