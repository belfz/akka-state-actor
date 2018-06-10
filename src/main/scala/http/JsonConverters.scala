package http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.Cat
import spray.json.DefaultJsonProtocol

trait JsonConverters extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val catFormat = jsonFormat2(Cat)
}
