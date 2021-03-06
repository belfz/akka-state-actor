package http

import actors.StateActor.{AddCatRequest, GetCatsRequest}
import akka.actor.{ActorRef, ActorSystem, Status}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import domain.Cat

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class HttpServer(stateActor: ActorRef)(implicit system: ActorSystem) extends JsonConverters {

  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val actorRequestTimeout = Timeout(1 second)

  val routes = pathPrefix("hello") {
    get {
      pathEnd {
        complete("no elo")
      } ~
      path(IntNumber) { num =>
        complete(HttpResponse(StatusCodes.OK))
      }
    }
  } ~
  path("cats") {
    get {
      val catsFuture = (stateActor ? GetCatsRequest).mapTo[List[Cat]]
      onComplete(catsFuture) {
        case Success(listOfCats) => complete(listOfCats)
        case Failure(e) => complete(HttpResponse(StatusCodes.InternalServerError, entity = "cannot fetch cats"))
      }
    } ~
    post {
      entity(as[Cat]) { cat =>
        val addResultFuture = stateActor ? AddCatRequest(cat)
        onComplete(addResultFuture) {
          case Success(_) => complete(HttpResponse(StatusCodes.OK))
          case Failure(e) => complete(HttpResponse(StatusCodes.Conflict, entity = e.getCause.getMessage))
        }
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
