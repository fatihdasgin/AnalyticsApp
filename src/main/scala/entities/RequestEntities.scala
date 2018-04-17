package entities

import java.text.SimpleDateFormat
import java.util

import scala.collection.JavaConversions.mapAsJavaMap

/**
  * Base request entity.
  *
  * @param timestamp Timestamp.
  */
abstract class RequestEntity(val timestamp: Long)

/**
  * Trait to convert timestamp into date.
  */
trait ConvertTimestampToDate {

  this: RequestEntity =>

  /**
    * Get timestamp in `yyyyMMdd` format or
    * any other format provided.
    *
    * @param format Format.
    *               Default is `yyyyMMdd`.
    * @return Date formatted timestamp.
    */
  def getTimestampInDateFormat(format: String = "yyyyMMdd"): String = {
    val dateFormat = new SimpleDateFormat(format)
    dateFormat.format(timestamp)
  }

}

/**
  * Get request entity.
  *
  * @param timestamp Timestamp.
  */
case class GetRequestEntity(override val timestamp: Long)
  extends RequestEntity(timestamp)
    with ConvertTimestampToDate

/**
  * Post request entity.
  *
  * @param timestamp Timestamp.
  * @param user      User name.
  * @param typeOf    `Click` or `impression`.
  */
case class PostRequestEntity(override val timestamp: Long,
                             user: String,
                             typeOf: String)
  extends RequestEntity(timestamp)
    with ConvertTimestampToDate {

  /**
    * Get properties as java hash map.
    *
    * @return Instance of [[java.util.HashMap]].
    */
  def getAsJavaMap: util.Map[String, _] = {
    val map = Map[String, Any](
      "TIMESTAMP" -> timestamp.toString,
      "USERNAME" -> user,
      "TYPEOF" -> typeOf
    )
    mapAsJavaMap(map)
  }

}
