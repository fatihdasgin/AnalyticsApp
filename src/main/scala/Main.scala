import com.typesafe.config.ConfigFactory
import constant.ConfigStrings
import web.WebServer

/**
  * Application bootstrapper.
  */
object Main {

  /**
    * Entry point of the application.
    *
    * @param args Arguments.
    */
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val webServer = WebServer(config.getString(ConfigStrings.webServerHost), config.getInt(ConfigStrings.webServerPort))
    val bindingFuture = webServer.start()
  }

}
