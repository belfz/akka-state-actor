import actors.StateActor
import akka.actor.ActorSystem
import http.HttpServer

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("system")
    val stateActor = new StateActor

    new HttpServer(stateActor).run()
  }
}
