package bq

import com.google.cloud.bigquery._

/**
  * Query operations.
  */
trait QueryOps {

  this: BqClient =>

  /**
    * Configure query job.
    *
    * {{{
    *   // Assuming you have `client`
    *
    *   // If query is going to include your project name,
    *   // it should be seperated by dot and
    *   // it should be wrapped by backtick, such as;
    *   // `yourProjectName.yourDatasetName.yourTableName`.
    *   val query = yourQuery
    *   val queryJobConf = client.configureQueryJob(query, false, false)
    * }}}
    *
    * @param query             SQL query in standard syntax.
    * @param allowLargeResults Allow large results.
    *                          If it is not going to be used with destination table,
    *                          it should be set to `false`.
    * @param useLegacySql      Use legacy sql.
    * @return Instance of [[QueryJobConfiguration]].
    */
  def configureQueryJob(query: String, allowLargeResults: Boolean, useLegacySql: Boolean): QueryJobConfiguration = {
    QueryJobConfiguration.newBuilder(query).
      setAllowLargeResults(allowLargeResults).
      setUseLegacySql(useLegacySql).build()
  }

  /**
    * Create job id with given id.
    *
    * {{{
    *   // Assuming you have `client`
    *   val jobId = client.createJobId(UUID.randomUUID().toString)
    * }}}
    *
    * @param jobId Id of the job.
    * @return Instance of [[JobId]].
    */
  def createJobId(jobId: String): JobId = JobId.of(jobId)

  /**
    * Create job id with given id for given project.
    *
    * {{{
    *   // Assuming you have `client`.
    *   val jobId = client.createJobId(yourProjectName, UUID.randomUUID().toString)
    * }}}
    *
    * @param projectName Name of the project.
    * @param jobId       Id of the job.
    * @return Instance of [[JobId]].
    */
  def createJobId(projectName: String, jobId: String): JobId = JobId.of(projectName, jobId)

  /**
    * Create job for query.
    *
    * {{{
    *   // Assuming you have `client`
    *   val query = yourQuery
    *   val queryJobConf = client.configureQueryJob(query, false, false)
    *   val jobId = client.createJobId(yourProjectNAme, UUID.randomUUID().toString)
    *   val job = client.createJob(jobId, queryJobConf)
    * }}}
    *
    * @param jobId                 Id of the job.
    * @param queryJobConfiguration Configuration of the query job.
    * @return Instance of [[Job]].
    */
  def createJob(jobId: JobId, queryJobConfiguration: QueryJobConfiguration): Job = {
    self.create(JobInfo.newBuilder(queryJobConfiguration).setJobId(jobId).build())
  }

  /**
    * Insert row(s) into query.
    *
    * ===Overview===
    * This method also works with streaming data.
    *
    * {{{
    *   // Assuming you have `client`.
    *   val content = new java.util.HashMap[String, _]()
    *   content.put(yourKeyAsTableColumn, yourValueAsTableRowToThatColumn)
    *   ...
    *   val tableId = client.tableIdOf(yourProjectName, yourDatasetName, yourTableName)
    *   val response = client.insertInto(tableId, content)
    * }}}
    *
    * @param tableId Id of the table.
    * @param content Content as key-value pair.
    * @return Response of insert query.
    */
  def insertInto(tableId: TableId, content: java.util.Map[String, _]): InsertAllResponse = {
    self.insertAll(InsertAllRequest.newBuilder(tableId).addRow(content).build())
  }

}
