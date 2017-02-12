package gilcu2.session

/**
  * Created by gilcu2 on 2/12/17.
  */

import java.util.concurrent.ConcurrentLinkedDeque

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import gilcu2.session.Models.ServiceJsonProtoocol
import gilcu2.session.Session._

import scala.collection.JavaConverters._
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.json._
import DefaultJsonProtocol._


trait RestService {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  val session = system.actorOf(Session.props, "Session")
  implicit val timeout = Timeout(5 seconds)

  import ServiceJsonProtoocol._

  val route =
    path("register") {
      post {
        entity(as[Register]) {
          register =>
            complete {
              val registerF = session.ask(register)
              val registerR = Await.result(registerF, timeout.duration).asInstanceOf[RegisterOk]
              registerR.toJson.prettyPrint
            }
        }
      }
    } ~
      path("login") {
        post {
          entity(as[Login]) {
            login =>
              complete {
                val loginF = session.ask(login)
                val loginR = Await.result(loginF, timeout.duration).asInstanceOf[SessionId]
                loginR.toJson.prettyPrint
              }
          }
        }
      } ~
      path("tocar") {
        post {
          entity(as[ToCar]) {
            tocar =>
              complete {
                val tocarF = session.ask(tocar)
                val tocarR = Await.result(tocarF, timeout.duration).asInstanceOf[CarVolumen]
                tocarR.toJson.prettyPrint
              }
          }
        }
      }
}

