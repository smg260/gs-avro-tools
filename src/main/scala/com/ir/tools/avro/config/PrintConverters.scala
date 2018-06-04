package com.ir.tools.avro.config

import java.net.InetAddress
import java.nio.ByteBuffer
import java.time.Instant

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
    case (o: GenericData.Fixed, _) => FixedTypeConverter.convert(o)
    case (o: ByteBuffer, "ipAddress" | "publicIps" | "localIps") => IpAddressConverter.convert(o)
    case (o: ByteBuffer, _) => ByteBufferConverter.convert(o)
    case (o: Long, "timestampMs") => TimestampConverter.convert(o)
  }

  def hasConverter(obj: Any, name: String): Boolean = (obj, name) match {
    case (_: GenericData.Fixed, _) => true
    case (_: ByteBuffer, _) => true
    case (_: Long, "timestampMs") => true
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

//private val NUM_UUID_EPOCH_TO_EPOCH_100NS_BLOCKS: Long = 0x01b21dd213814000L
//private val NUM_100NS_BLOCKS_IN_A_MS: Int = 10000
//prints human readable timestamp which is encoded in the 8 most significant bits as 100NS blocks from UUID epoch
//            ByteBuffer bb = ByteBuffer.wrap(bytes);
//            buffer.append("timestamp: " + Instant.ofEpochMilli((bb.getLong() - NUM_UUID_EPOCH_TO_EPOCH_100NS_BLOCKS) / NUM_100NS_BLOCKS_IN_A_MS));
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

object TimestampConverter extends AvroTypeConverter[Long] {
  override def convert(obj: Long): String = Instant.ofEpochMilli(obj).toString
}