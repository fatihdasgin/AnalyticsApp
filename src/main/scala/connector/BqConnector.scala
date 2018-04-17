package connector

import bq.{BigQueryClient, SqlType}
import com.google.cloud.bigquery.{Field, InsertAllResponse, Job, TableId}

/**
  * BigQuery connector.
  */
trait BqConnector {

  /**
    * Get path of the file.
    *
    * @return Path of the file.
    */
  def getPath: String

  /**
    * Get name of the dataset.
    *
    * @return Name of the dataset.
    */
  def getDatasetName: String

  /**
    * Get name of the project.
    *
    * @return Name of the project.
    */
  def getProjectName: String

  /**
    * Get prefix of the table name.
    *
    * @return Prefix of the table name.
    */
  def getTableNamePrefix: String

  /**
    * Run `INSERT` operation.
    *
    * @param content         Content of the row.
    * @param tableNameSuffix Suffix of the table name.
    * @return Either an insertion response or
    *         tuple of id of the table and BigQuery client.
    */
  def insertIntoTable(content: java.util.Map[String, _], tableNameSuffix: String): Either[InsertAllResponse, (TableId, BigQueryClient)] = {
    val client = BigQueryClient(getPath)
    client.getTable(getProjectName, getDatasetName, getTableNamePrefix + tableNameSuffix) match {
      case Left(i) => Left(client.insertInto(i.getTableId, content))
      case _ => Right((client.tableIdOf(getProjectName, getDatasetName, getTableNamePrefix + tableNameSuffix), client))
    }
  }

  /**
    * Create table and run `INSERT` operation.
    *
    * @param tableId Id of the table.
    * @param fields  Fields.
    * @param content Content of the row.
    * @return Instance of [[InsertAllResponse]].
    */
  def createTableAndInsertInto(tableId: TableId, fields: Vector[Field], content: java.util.Map[String, _]): InsertAllResponse = {
    val client = BigQueryClient(getPath)
    val table = client.createTable(tableId, fields)
    client.insertInto(table.getTableId, content)
  }

  /**
    * Run `SELECT` operation.
    *
    * @param query           Select query.
    * @param tableNameSuffix Suffix of the table name.
    * @param jobId           Id the job.
    * @return Either a job or a string.
    */
  def readFromTable(query: String, tableNameSuffix: String, jobId: String): Either[Job, String] = {
    val client = BigQueryClient(getPath)
    client.getTable(getProjectName, getDatasetName, getTableNamePrefix + tableNameSuffix) match {
      case Left(i) =>
        val queryJobConfiguration = client.configureQueryJob(query, allowLargeResults = false, useLegacySql = false)
        val job = client.createJob(client.createJobId(getProjectName, jobId), queryJobConfiguration)
        Left(job.waitFor())
      case Right(i) => Right(i)
    }
  }

}
