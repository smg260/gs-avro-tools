package com.ir.tools.avro.config

import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.{Instant, LocalDateTime, ZoneOffset}

import com.google.common.base.Preconditions
import com.google.common.primitives.Longs
import org.apache.avro.generic.GenericData
import org.apache.commons.codec.binary.Hex

import scala.util.{Failure, Success, Try}

object PrintConverters {

  /**
    * Callers should call `hasConverter` first otherwise may encounter a match error
    * @param obj
    * @param name
    * @return
    */
  def convert(obj: Any, name: String): String = (obj, name) match {
    case (o: GenericData.Fixed, "sessionId") => SessionIdFixedTypeConverter.convert(o)
    case (o: GenericData.Fixed, _) => FixedTypeConverter.convert(o)
    case (o: ByteBuffer, "ipAddress" | "publicIps" | "localIps") => IpAddressConverter.convert(o)
    case (o: ByteBuffer, _) => ByteBufferConverter.convert(o)
    case (o: Long, "timestampMs" | "createdTimeMs") => TimestampMillisConverter.convert(o)
    case (o: Long, "timestampMicros") => TimestampMicrosConverter.convert(o)
  }

  def hasConverter(obj: Any, name: String): Boolean = (obj, name) match {
    case (_: GenericData.Fixed, _) => true
    case (_: ByteBuffer, _) => true
    case (_: Long, "timestampMs" | "timestampMicros" | "createdTimeMs") => true
    case _ => false
  }
}


trait AvroTypeConverter[T] {
  def convert(obj: T): String
}


/**
  * in tracking-api
  * 1)TimeBased UUID Generator created with hardware address as parameter
  * 2) UUID Generated
  * 3) JS UUID is UrlEncoded then Decoded before setting in Sessionid
  *
  * fun getBytesForUUID(uuid: UUID): ByteArray {
  * val ourSessionId = ByteArray(16)
  *         System.arraycopy(Longs.toByteArray(uuid.timestamp()), 0, ourSessionId, 0, Longs.BYTES)
  *         System.arraycopy(Longs.toByteArray(uuid.leastSignificantBits), 0, ourSessionId, Longs.BYTES, Longs.BYTES)
  * return ourSessionId
  * }
  */


object SessionIdFixedTypeConverter extends AvroTypeConverter[GenericData.Fixed] {
  private val UUID_EPOCH_MS = LocalDateTime.of(1582, 10, 15, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli

  private def extractTimeFromFqUUID(sessionId: Array[Byte]): Instant = {
    Preconditions.checkNotNull(sessionId)
    Preconditions.checkArgument(sessionId.length == 16)
    //this is the number of 100NS blocks from the UUID Epoch
    val uuidTimestamp = Longs.fromByteArray(sessionId)
    val msFromUuidEpoch = uuidTimestamp / 10000
    //offset to unix epoch
    val epochMs = UUID_EPOCH_MS + msFromUuidEpoch
    Instant.ofEpochMilli(epochMs)
  }

  def convert(obj: GenericData.Fixed): String = {
    s"${FixedTypeConverter.convert(obj)} [${extractTimeFromFqUUID(obj.bytes())}]"
  }
}

object FixedTypeConverter extends AvroTypeConverter[GenericData.Fixed] {
  def convert(obj: GenericData.Fixed): String = Hex.encodeHex(obj.bytes()).mkString
}

object ByteBufferConverter extends AvroTypeConverter[ByteBuffer] {
  def convert(obj: ByteBuffer): String = Hex.encodeHex(obj.array()).mkString
}

//override
object IpAddressConverter extends AvroTypeConverter[ByteBuffer] {
  def convert(obj: ByteBuffer): String =
    Try(InetAddress.getByAddress(obj.array()).getHostAddress) match {
      case Success(address) => address
      case Failure(ex) => ex.getMessage
    }
}

object TimestampMillisConverter extends AvroTypeConverter[Long] {
  def convert(obj: Long): String = Instant.ofEpochMilli(obj).toString
}


object TimestampMicrosConverter extends AvroTypeConverter[Long] {
  def convert(obj: Long): String = Instant.ofEpochMilli(obj/1000).toString
}