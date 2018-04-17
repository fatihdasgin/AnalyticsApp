package bq

import java.io.{File, FileInputStream}

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery.{BigQuery, BigQueryOptions}

/**
  * Wrapper of BigQuery client API which is written in Java by Google.
  *
  * @param credentials Instance of [[ServiceAccountCredentials]].
  */
class BigQueryClient private(credentials: ServiceAccountCredentials)
  extends BqClient(credentials)
    with DsOps
    with TableOps
    with QueryOps {

  /**
    * BigQuery client.
    */
  override val self: BigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService

}

/**
  * Companion object of [[BigQueryClient]].
  */
object BigQueryClient {

  /**
    * Create instance of [[BigQueryClient]].
    *
    * {{{
    *   val client = BigQueryClient(pathToYourFile)
    * }}}
    *
    * @param filePath File path to Google Cloud Platform json file.
    * @return BigQuery client.
    */
  def apply(filePath: String): BigQueryClient = {
    val file = new File(filePath)
    val credentials = ServiceAccountCredentials.fromStream(new FileInputStream(file))
    new BigQueryClient(credentials)
  }

}
