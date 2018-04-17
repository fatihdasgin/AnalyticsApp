package web

import java.util.UUID

import akka.actor.{Actor, Props}
import bq.{STRING, SqlType}
import com.typesafe.config.ConfigFactory
import connector.BqConnector
import constant.ConfigStrings
import entities.{ERROR, GetRequestEntity, OK, PostRequestEntity, ResponseEntity}

/**
  * Request handler actor.
  */
class RequestHandler extends Actor with BqConnector {

  /**
    * Configuration.
    */
  private val config = ConfigFactory.load()

  override def receive: Receive = {
    case get: GetRequestEntity =>
      val tableNameSuffix = get.getTimestampInDateFormat()
      val jobId = UUID.randomUUID().toString
      val query = "SELECT " +
        "COUNT(DISTINCT USERNAME) AS unique_users," +
        "COUNT(CASE WHEN TYPEOF = 'click' THEN 1 ELSE NULL END) AS clicks," +
        "COUNT(CASE WHEN TYPEOF = 'impression' THEN 1 ELSE NULL END) AS impressions " +
        "FROM `" + getProjectName + "." + getDatasetName + "." + getTableNamePrefix + tableNameSuffix + "` " +
        "WHERE TIMESTAMP = '" + get.timestamp.toString + "';"
      readFromTable(query, tableNameSuffix, jobId) match {
        case Left(job) =>
          val result = job.getQueryResults()
          val row = result.iterateAll().iterator().next()
          val msg =
            s"""
               |unique_users,${row.get("unique_users").getLongValue}
               |clicks,${row.get("clicks").getLongValue}
               |impressions,${row.get("impressions").getLongValue}
         """.stripMargin
          sender() ! ResponseEntity(msg, OK)
        case Right(i) => sender() ! ResponseEntity(i, ERROR)
      }
      context.stop(self)
    case post: PostRequestEntity =>
      val tableNameSuffix = post.getTimestampInDateFormat()
      insertIntoTable(post.getAsJavaMap, tableNameSuffix) match {
        case Left(insertAllResponse) =>
          if (insertAllResponse.hasErrors) sender() ! ResponseEntity("An error occured during insertion!", ERROR)
          else sender() ! ResponseEntity(post.timestamp.toString, OK)
        case Right((tableId, client)) =>
          val fields = client.createFields(Vector[(String, SqlType)](("TIMESTAMP", STRING),
            ("USERNAME", STRING),
            ("TYPEOF", STRING)))
          val response = createTableAndInsertInto(tableId, fields, post.getAsJavaMap)
          if (response.hasErrors) sender() ! ResponseEntity("An error occcured during insertion! See ", ERROR)
          else sender() ! ResponseEntity(post.timestamp.toString, OK)
      }
      context.stop(self)
  }

  /**
    * Get path of the file.
    *
    * @return Path of the file.
    */
  override def getPath: String = config.getString(ConfigStrings.gcpPath)

  /**
    * Get name of the dataset.
    *
    * @return Name of the dataset.
    */
  override def getDatasetName: String = config.getString(ConfigStrings.gcpDataset)

  /**
    * Get name of the project.
    *
    * @return Name of the project.
    */
  override def getProjectName: String = config.getString(ConfigStrings.gcpProject)

  /**
    * Get prefix of the table name.
    *
    * @return Prefix of the table name.
    */
  override def getTableNamePrefix: String = config.getString(ConfigStrings.gcpTableNamePrefix)
}

/**
  * Request handler companion object.
  */
object RequestHandler {

  /**
    * Get instance of [[Props]] of [[RequestHandler]].
    *
    * @return Instance of [[Props]] of [[RequestHandler]].
    */
  def props(): Props = Props(new RequestHandler())

}