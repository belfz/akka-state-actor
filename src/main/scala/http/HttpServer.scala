package http

import java.util.concurrent.Executors

import actors.StateActor
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Cat

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class HttpServer(stateActor: StateActor)(implicit system: ActorSystem) extends JsonConverters {

  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  implicit val materializer = ActorMaterializer()
  implicit val actorRequestTimeout = Timeout(1 second)

  val routes = path("hi") {
    get {
      complete {
        "no elo"
      }
    }
  } ~
  path("cat") {
    get {
      complete {
        Cat("belfiak", 5)
      }
    } ~
    post {
      entity(as[Cat]) { cat =>
        println(cat)
        complete("ok")
      }
    }
  }

  def run(): Unit = {
    val configFactory = ConfigFactory.load()
    val host = configFactory.getString("server.host")
    val port = configFactory.getInt("server.port")
    Http().bindAndHandle(routes, host, port).onComplete({
      case Success(s) => println(s"server running at ${s.localAddress}")
      case Failure(e) => println("Error while starting server", e)
    })
  }
}
