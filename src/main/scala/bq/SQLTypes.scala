package bq

import com.google.cloud.bigquery.LegacySQLTypeName

/**
  * SQL Type.
  */
trait SqlType {

  /**
    * Get SQL Type of the case object.
    *
    * @return SQL Type name.
    */
  def getType: LegacySQLTypeName

}

/**
  * Case object to represent STRING type in SQL.
  */
case object STRING extends SqlType {

  /**
    * Get SQL Type of the case object.
    *
    * @return SQL Type name.
    */
  override def getType: LegacySQLTypeName = LegacySQLTypeName.STRING
}