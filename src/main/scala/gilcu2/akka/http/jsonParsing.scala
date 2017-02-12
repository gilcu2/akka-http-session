package gilcu2.akka.http

/**
  * Created by gilcu2 on 2/12/17.
  */

import java.util.concurrent.ConcurrentLinkedDeque

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import Models.{Customer, ServiceJsonProtoocol}
import spray.json.DefaultJsonProtocol._
import scala.collection.JavaConverters._


object AkkaJsonParsing {


  def main(args: Array[String]) {

    implicit val actorSystem = ActorSystem("rest-api")

    implicit val actorMaterializer = ActorMaterializer()

    val list = new ConcurrentLinkedDeque[Customer]()

    import ServiceJsonProtoocol.customerProtocol
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

    Http().bindAndHandle(route, "localhost", 8080)
  }


}