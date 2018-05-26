package ir.fq.platform.common.serialization.schema

import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import java.util.zip.ZipInputStream

import com.google.common.base.Preconditions
import com.google.common.cache.{Cache, CacheBuilder}
import com.google.common.io.Closeables

import scala.io.Source

class JarSchemaRegistry[S](basePath: String, val archiveNameByVersion: String => String,
                           schemaFileExtension: String = "avsc", serde: SchemaSerde[S]) extends ProtocolAwareSchemaRegistry[S](basePath, schemaFileExtension, serde) {
  import SchemaRegistry._
  Preconditions.checkNotNull(archiveNameByVersion)

  private val ArchiveEntryCache: Cache[(String, String), Array[Byte]] = CacheBuilder.newBuilder()
    .maximumSize(60)
    .initialCapacity(1)
    .build[(String, String), Array[Byte]]()

  private def getArchiveEntryContent(id: String, version: String): Array[Byte] = {
    var prefetchedEntries = new java.util.HashMap[(String, String), Array[Byte]]()

    def loadArchiveContent: Callable[Array[Byte]] = new Callable[Array[Byte]] {
      override def call(): Array[Byte] = {
        if (SchemaRegistry.RegisteredSchemaByVersion.isEmpty) {
          Source.fromFile(s"$basePath/$version/${archiveNameByVersion(version)}.jar#$id.$schemaFileExtension")
        } else {
          val fullPath = "jar://" + Prefix + '/' + version + '/' + archiveNameByVersion(version) + ".jar"
          // read requested entry _plus_ additional ones from RegisteredSchemas
          var entryBytes: Array[Byte] = null
          val zipInputStream = ProtocolAwareResourceReader.read(fullPath).asInputStream().asInstanceOf[ZipInputStream]

          try {
            for (entry <- Iterator.continually(zipInputStream.getNextEntry).takeWhile(_ != null)) {
              if (entry.getName.endsWith(Suffix)) {
                val basename = FilenameUtils.getBaseName(entry.getName)

                if (basename == id) {
                  entryBytes = IOUtils.toByteArray(zipInputStream)
                } else if (RegisteredSchemaByVersion.containsKey(basename)) {
                  prefetchedEntries.put((basename, version), IOUtils.toByteArray(zipInputStream))
                }
              }
            }
          } finally {
            Closeables.close(zipInputStream, false)
          }
          Preconditions.checkNotNull(entryBytes)
          entryBytes
        }
      }
    }

    try {
      val result = ArchiveEntryCache.get((id, version), loadArchiveContent)

      if (!prefetchedEntries.isEmpty) {
        // store prefetched entries
        ArchiveEntryCache.putAll(prefetchedEntries)
      }
      result
    } catch {
      case ex: Exception => throw new SchemaNotFoundException(s"Unable to resolve schema for ${id}, version: ${version}", ex)
    }
  }

  override def getSchemaFor(id: String, version: String): S = {
    this.deserialize(IOUtils.toString(getArchiveEntryContent(id, version), StandardCharsets.UTF_8.name()))
  }
}
