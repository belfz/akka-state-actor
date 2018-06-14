import actors.StateActor
import akka.actor.Props
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import domain.Cat
import http.{HttpServer, JsonConverters}
import org.scalatest.FunSpecLike

class RoutesTests extends JsonConverters with FunSpecLike with ScalatestRouteTest {
  describe("routes") {
    val stateActorRef = system.actorOf(Props(new StateActor(List(Cat("test_cat", 6)))), "stateActor")
    val httpServer = new HttpServer(stateActorRef)

    it("GET '/hello' should return a 'no elo' string") {
      Get("/hello") ~> httpServer.routes ~> check {
        assert(responseAs[String] == "no elo")
      }
    }

    it("GET '/hello/42' should return an 'HTTP 200 OK' response") {
      Get("/hello/42") ~> httpServer.routes ~> check {
        assert(status == StatusCodes.OK)
      }
    }

    it("GET '/cats' should return a list of Cats") {
      Get("/cats") ~> httpServer.routes ~> check {
        assert(responseAs[List[Cat]] == List(Cat("test_cat", 6)))
      }
    }

    it("POST '/cats' should accept the new Cat") {
      Post("/cats", Cat("new_cat", 3)) ~> httpServer.routes ~> check {
        assert(status == StatusCodes.OK)
      }
    }

    it("POST '/cats' should not accept existing Cat") {
      Post("/cats", Cat("test_cat", 6)) ~> httpServer.routes ~> check {
        assert(status == StatusCodes.Conflict)
      }
    }
  }
}
