package ir.fq.platform.common.serialization.schema

import com.google.common.cache.{Cache, CacheBuilder}

/**
  * Creates a cache for storing objects against a schema version
  */
object VersionCacheFactory {
  def createVersionCache[S <: Object](numVersionsToCache: Int = 5): Cache[String, S] = {
    CacheBuilder.newBuilder()
      .maximumSize(numVersionsToCache)
      .initialCapacity(numVersionsToCache)
      .build[String, S]()
  }
}
