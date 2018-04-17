package bq

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.bigquery.BigQuery

/**
  * Base class for BigQuery client.
  *
  * @param credentials Instance of [[ServiceAccountCredentials]]
  */
abstract class BqClient(credentials: ServiceAccountCredentials) {

  val self: BigQuery

}
