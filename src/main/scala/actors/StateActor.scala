package actors

import actors.StateActor.{AddCatRequest, CatExistsError, GetCatsRequest}
import akka.actor.{Actor, Status}
import domain.Cat

import scala.collection.mutable.ListBuffer

class StateActor(initialCats: List[Cat]) extends Actor {

  val cats = initialCats.to[ListBuffer]

  override def receive: Receive = {
    case GetCatsRequest => sender() ! cats.toList

    case AddCatRequest(cat) => {
      if (cats.contains(cat)) {
        sender() ! Status.Failure(CatExistsError(cat))
      } else {
        cats += cat
        sender() ! Status.Success
      }
    }
  }
}

object StateActor {
  case object GetCatsRequest

  case class AddCatRequest(cat: Cat)

  case class CatExistsError(cat: Cat) extends Error(s"$cat already exists")
}
