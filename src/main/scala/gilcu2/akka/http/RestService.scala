package gilcu2.akka.http

/**
  * Created by gilcu2 on 2/12/17.
  */

import java.util.concurrent.ConcurrentLinkedDeque

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import Models.{ServiceJsonProtoocol, Customer}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.JsArray
import scala.collection.JavaConverters._
import spray.json.{JsArray, pimpAny, DefaultJsonProtocol}
import spray.json.DefaultJsonProtocol._


/**
  * Created by madhu on 8/11/15.
  */
trait RestService {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  val list = new ConcurrentLinkedDeque[Customer]()

  import ServiceJsonProtoocol._

  val route =
    path("customer") {
      post {
        entity(as[Customer]) {
          customer =>
            complete {
              list.add(customer)
              s"got customer with name ${customer.name}"
            }
        }
      } ~
        get {
          complete {
            list.asScala
          }
        }
    }
}

