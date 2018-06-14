import actors.StateActor
import actors.StateActor.{AddCatRequest, CatExistsError, GetCatsRequest}
import akka.actor.{ActorSystem, Props, Status}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import domain.Cat
import org.scalatest.FunSpecLike
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.util.Success

class StateActorTests extends TestKit(ActorSystem("stateTests")) with ImplicitSender with FunSpecLike with ScalaFutures {
  implicit val timeout = Timeout(1 seconds)
  implicit val executionContext = system.dispatcher
  val initialCatsList = List(Cat("test_cat", 3))

  it("should return a list of available cats on GetCatsRequest") {
    val stateActor = system.actorOf(Props(new StateActor(initialCatsList)))
    val future = (stateActor ? GetCatsRequest).mapTo[List[Cat]]
    val listOfCats = future.futureValue
    assert(listOfCats == initialCatsList)
  }

  it("should add a Cat to the list") {
    val stateActor = system.actorOf(Props(new StateActor(initialCatsList)))
    val future = (stateActor ? AddCatRequest(Cat("new_cat", 1)))
    val result = future.futureValue
    assert(result == Status.Success)
  }

  it("should fail while trying to add an existing Cat to the list") {
    val cat = Cat("test_cat", 3)
    val stateActor = system.actorOf(Props(new StateActor(initialCatsList)))
    val future = (stateActor ? AddCatRequest(cat))
    val result = future.failed.futureValue.getCause
    assert(result == CatExistsError(cat))
  }
}
