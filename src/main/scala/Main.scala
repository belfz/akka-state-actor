import actors.StateActor
import akka.actor.{ActorSystem, Props}
import domain.Cat
import http.HttpServer

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("system")
    val stateActorRef = system.actorOf(Props(new StateActor(List(Cat("test_cat", 6)))), "stateActor")

    new HttpServer(stateActorRef).run()
  }
}
