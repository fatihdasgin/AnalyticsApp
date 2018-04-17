package bq

import com.google.cloud.bigquery._

import scala.collection.immutable.Vector

/**
  * Table operations.
  */
trait TableOps {

  this: BqClient =>

  /**
    * Create table.
    *
    * {{{
    *   // Assuming you have `client`
    *   val tableId = client.tableIdOf(yourProjectName, yourDatasetName, yourTableName)
    *   import scala.collection.immutable.Vector
    *   import bq.SQLTypes
    *   val arr: Array[(String, SQLType)] = Array[(String, SQLType)](
    *     ("TIMESTAMP", STRING),
    *     ("USERNAME", STRING),
    *     ("TYPEOF", STRING)
    *   )
    *   val vec = Vector(arr: _*)
    *   val fields = client.createFields(vec)
    *   val table = client.createTable(tableId, fields)
    * }}}
    *
    * @param tableId Id of the table.
    * @param fields  Fields of the table.
    * @return Instance of [[Table]].
    */
  def createTable(tableId: TableId, fields: Vector[Field]): Table = {
    val tableInfo: TableInfo = TableInfo.newBuilder(tableId, tableDefinitionOf(schemaOf(fields))).build()
    self.create(tableInfo)
  }

  /**
    * Get id of the table with given name in given dataset.
    *
    * {{{
    *   // Assuming you have `client`
    *   val tableId = client.tableIdOf(yourDatasetName, yourTableName)
    * }}}
    *
    * @param datasetName Name of the dataset.
    * @param tableName   Name of the table.
    * @return Instance of [[TableId]].
    */
  def tableIdOf(datasetName: String, tableName: String): TableId = {
    TableId.of(datasetName, tableName)
  }

  /**
    * Get id of the table with given name in given dataset in given project.
    *
    * {{{
    *   // Assuming you have `client`
    *   val tableId = client.tableIdOf(yourProjectName, yourDatasetName, yourTableName)
    * }}}
    *
    * @param projectName Name of the project.
    * @param datasetName Name of the dataset.
    * @param tableName   Name of the table.
    * @return Instance of [[TableId]].
    */
  def tableIdOf(projectName: String, datasetName: String, tableName: String): TableId = {
    TableId.of(projectName, datasetName, tableName)
  }

  /**
    * Create table fields.
    *
    * {{{
    *   // Assuming you have `client`
    *   import scala.collection.immutable.Vector
    *   import bq.SQLTypes
    *   val arr: Array[(String, SQLType)] = Array[(String, SQLType)](
    *     ("TIMESTAMP", STRING),
    *     ("USERNAME", STRING),
    *     ("TYPEOF", STRING)
    *   )
    *   val vec = Vector(arr: _*)
    *   val fields = client.createFields(vec)
    * }}}
    *
    * @param nameSqlTypePair Table column and type pair.
    * @return Vector of instance of [[Field]].
    */
  def createFields(nameSqlTypePair: Vector[(String, SqlType)]): Vector[Field] = {
    def aux(fields: Vector[Field], nameSqlType: Vector[(String, SqlType)]): Vector[Field] = {
      if (nameSqlType.isEmpty) return fields
      val (name, sqlType) = nameSqlType.head
      val field = Field.of(name, sqlType.getType)
      aux(fields :+ field, nameSqlType.tail)
    }

    aux(Vector.empty[Field], nameSqlTypePair)
  }

  /**
    * Create schema of table with given fields.
    *
    * {{{
    *   // Assuming you have `client` and `fields`
    *   val schema = client.schemaOf(fields)
    * }}}
    *
    * @param fields Fields of the table
    * @return Instance of [[Schema]].
    */
  def schemaOf(fields: Vector[Field]): Schema = {
    Schema.of(fields: _*)
  }

  /**
    * Get table definition from schema.
    *
    * {{{
    *   // Assuming you have `client` and `fields`
    *   val schema = client.schemaOf(fields)
    *   val tableDef = client.tableDefinitionOf(schema)
    * }}}
    *
    * @param schema Schema of the table.
    * @return Instance of [[TableDefinition]].
    */
  def tableDefinitionOf(schema: Schema): TableDefinition = {
    StandardTableDefinition.of(schema)
  }

  /**
    * Get table if exists.
    *
    * ===Overview===
    * If table with given name in given dataset exists,
    * it returns instance of table.
    *
    * If not, it returns string message to specify error.
    * `Option` can not be used directly with `Table`.
    * because `Table` is not reachable by option.
    * To overcome this issue, `Either` is used.
    *
    * {{{
    *   // Assuming you have `client`
    *   client.getTable(yourDatasetName, yourTableName) match {
    *     case Left(i) => // your logic
    *     case Right(msg) => println(msg)
    *   }
    * }}}
    *
    * @param datasetName Name of the dataset.
    * @param tableName   Name of the table.
    * @return Either a table or a string.
    */
  def getTable(datasetName: String, tableName: String): Either[Table, String] = {
    val table = self.getTable(tableIdOf(datasetName, tableName))
    if (table != null) Left(table)
    else Right(s"Table with name $tableName does not exist!")
  }

  /**
    * Get table if exists.
    *
    * ===Overview===
    * If table with given name in given dataset in given project exists,
    * it returns instance of table.
    *
    * If not, it returns string message to specify error.
    * `Option` can not be used directly with `Table`.
    * because `Table` is not reachable by option.
    * To overcome this issue, `Either` is used.
    *
    * {{{
    *   // Assuming you have `client`
    *   client.getTable(yourProjectName, yourDatasetName, yourTableName) match {
    *     case Left(i) => // your logic
    *     case Right(msg) => println(msg)
    *   }
    * }}}
    *
    * @param projectName Name of the project.
    * @param datasetName Name of the dataset.
    * @param tableName   Name of the table.
    * @return Either a table or a string.
    */
  def getTable(projectName: String, datasetName: String, tableName: String): Either[Table, String] = {
    val table = self.getTable(tableIdOf(projectName, datasetName, tableName))
    if (table != null) Left(table)
    else Right(s"Table with name $tableName does not exist!")
  }
}
