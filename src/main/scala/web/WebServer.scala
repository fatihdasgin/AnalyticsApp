package web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.pattern.ask
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import constant.ConfigStrings
import entities._

import scala.concurrent.duration._
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  * Web server.
  *
  * @param host             Host.
  * @param port             Port.
  * @param system           Actor system.
  * @param materializer     Materializer.
  * @param executionContext Execution context executor.
  * @param askTimeout       Timeout of ask pattern.
  */
class WebServer(host: String, port: Int)
               (implicit val system: ActorSystem,
                implicit val materializer: Materializer,
                implicit val executionContext: ExecutionContextExecutor,
                implicit val askTimeout: Timeout) {

  /**
    * Exception handler.
    *
    * @return Instance of [[ExceptionHandler]].
    */
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case i: Throwable =>
      complete(HttpResponse(StatusCodes.InternalServerError, entity = i.getMessage))
  }

  val route = handleExceptions(exceptionHandler) {
    path("status") {
      get {
        complete(StatusCodes.OK)
      }
    } ~
      path("analytics") {
        get {
          parameters('timestamp.as[Long]) { timestamp =>
            val actor = system.actorOf(RequestHandler.props())
            onSuccess(actor ? GetRequestEntity(timestamp)) {
              case responseEntity: ResponseEntity =>
                responseEntity.status match {
                  case OK => complete(HttpResponse(StatusCodes.OK, entity = responseEntity.msg))
                  case ERROR => complete(StatusCodes.InternalServerError)
                }
            }
          }
        } ~
          post {
            parameters('timestamp.as[Long], "user", 'click.?, 'impression.?) { (timestamp, user, click, impression) =>
              val actor = system.actorOf(RequestHandler.props())
              val requestEntity = if (click.isEmpty) PostRequestEntity(timestamp, user, "impression")
              else PostRequestEntity(timestamp, user, "click")
              onSuccess(actor ? requestEntity) {
                case responseEntity: ResponseEntity =>
                  responseEntity.status match {
                    case OK =>
                      val varnishInternalIpVector = WebServer.config.getStringList(ConfigStrings.varnishInternalIp).asScala.toVector

                      def aux(vec: Vector[String]): Unit = {
                        if (vec.nonEmpty) {
                          import scala.sys.process._
                          val cmd = s"""bash ${WebServer.config.getString(ConfigStrings.purger)} ${vec.head} ${responseEntity.msg}"""
                          val output = cmd.!!
                          aux(vec.tail)
                        }
                      }

                      aux(varnishInternalIpVector)
                      complete(StatusCodes.OK)
                    case ERROR => complete(StatusCodes.InternalServerError)
                  }
              }
            }
          }
      }
  }

  /**
    * Start web server.
    *
    * {{{
    *   // Assuming you have implicit values
    *   val webServer = WebServer(yourHost, yourPort)
    *   val futureBinding = webServer.start()
    * }}}
    *
    * @return Future of [[Http.ServerBinding]].
    */
  def start(): Future[Http.ServerBinding] = {
    Http().bindAndHandle(route, host, port)
  }

}

/**
  * Web server companion object.
  */
object WebServer {

  /**
    * Configuration.
    */
  private val config = ConfigFactory.load()

  /**
    * Create instance of [[WebServer]].
    *
    * {{{
    *   // Assuming you have implicit values
    *   val webServer = WebServer(yourHost, yourPort)
    * }}}
    *
    * @param host Host.
    * @param port Port.
    * @return Instance of [[WebServer]].
    */
  def apply(host: String, port: Int): WebServer = {
    implicit val system: ActorSystem = ActorSystem(config.getString(ConfigStrings.webServerActorSystem))
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val askTimeout: Timeout = config.getInt(ConfigStrings.webServerAskTimeout).minutes
    new WebServer(host, port)
  }

}