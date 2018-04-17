package bq

import com.google.cloud.bigquery.{Dataset, DatasetId, DatasetInfo}

/**
  * Dataset operations.
  */
trait DsOps {

  this: BqClient =>

  /**
    * Create dataset.
    *
    * {{{
    *   val client = BigQueryClient(pathToYourFile)
    *   val dataset = client.createDataset(yourDatasetName)
    * }}}
    *
    * @param datasetName Name of the dataset.
    * @return Instance of [[Dataset]].
    */
  def createDataset(datasetName: String): Dataset = {
    val datasetInfo = DatasetInfo.newBuilder(datasetName).build()
    self.create(datasetInfo)
  }

  /**
    * Create dataset.
    *
    * {{{
    *   val client = BigQueryClient(pathToYourFile)
    *   val dataset = client.createDataset(yourProjectName, yourDatasetName)
    * }}}
    *
    * @param projectName Name of the project.
    * @param datasetName Name of the dataset.
    * @return Instance of [[Dataset]].
    */
  def createDataset(projectName: String, datasetName: String): Dataset = {
    val datasetInfo = DatasetInfo.newBuilder(projectName, datasetName).build()
    self.create(datasetInfo)
  }

  /**
    * Get id of the dataset with given name.
    *
    * {{{
    *   // Assuming you have `client`
    *   val dsId = client.datasetIdOf(yourDatasetName)
    * }}}
    *
    * @param datasetName Name of the dataset.
    * @return Instance of [[DatasetId]]
    */
  def datasetIdOf(datasetName: String): DatasetId = {
    DatasetId.of(datasetName)
  }

  /**
    * Get id of the dataset with given name in given project.
    *
    * {{{
    *   // Assuming you have `client`
    *   val dsId = client.datasetIdOf(yourDatasetName).
    * }}}
    *
    * @param projectName Name of the project.
    * @param datasetName Name of the dataset.
    * @return Instance of [[DatasetId]].
    */
  def datasetIdOf(projectName: String, datasetName: String): DatasetId = {
    DatasetId.of(projectName, datasetName)
  }

  /**
    * Get dataset if exists.
    *
    * ===Overview===
    * If dataset with given name in given project exists,
    * it returns instance of dataset.
    *
    * If not, it returns string message to specify error.
    * `Option` can not be used directly with `Dataset`.
    * because `Dataset` is not reachable by option.
    * To overcome this issue, `Either` is used.
    *
    * {{{
    *   // Assuming you have `client`
    *   client.getDataset(yourProjectName, yourDatasetName) match {
    *     case Left(i) => // your logic
    *     case Right(msg) => println(msg)
    *   }
    * }}}
    *
    * @param projectName Name of the project.
    * @param datasetName Name of the dataset.
    * @return Either a dataset or a string.
    */
  def getDataset(projectName: String, datasetName: String): Either[Dataset, String] = {
    val dataset = self.getDataset(datasetIdOf(projectName, datasetName))
    if (dataset != null) Left(dataset)
    else Right(s"Dataset with name $datasetName does not exist")
  }

  /**
    * Get dataset if exists.
    *
    * ===Overview===
    * If dataset with given name exists,
    * it returns instance of dataset.
    *
    * If not, it returns string message to specify error.
    * `Option` can not be used directly with `Dataset`.
    * because `Dataset` is not reachable by option.
    * To overcome this issue, `Either` is used.
    *
    * {{{
    *   // Assuming you have `client`
    *   client.getDataset(yourDatasetName) match {
    *     case Left(i) => // your logic
    *     case Right(msg) => println(msg)
    *   }
    * }}}
    *
    * @param datasetName Name of the dataset.
    * @return Either a dataset or a string.
    */
  def getDataset(datasetName: String): Either[Dataset, String] = {
    val dataset = self.getDataset(datasetIdOf(datasetName))
    if (dataset != null) Left(dataset)
    else Right(s"Dataset with name $datasetName does not exist")
  }
}
