package actors

import actors.StateActor.{AddCatRequest, GetCatsRequest}
import akka.actor.{Actor, Status}
import domain.Cat

import scala.collection.mutable.ListBuffer

class StateActor(initialCats: List[Cat]) extends Actor {

  val cats = initialCats.to[ListBuffer]

  override def receive: Receive = {
    case GetCatsRequest => sender() ! cats.toList

    case AddCatRequest(cat) => {
      if (cats.contains(cat)) {
        sender() ! Status.Failure(new Error("cat already exists!"))
      } else {
        cats += cat
        sender() ! ()
      }
    }
  }
}

object StateActor {
  case object GetCatsRequest

  case class AddCatRequest(cat: Cat)
}
