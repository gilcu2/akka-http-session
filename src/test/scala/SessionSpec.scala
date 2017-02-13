/**
  * Created by gilcu2 on 2/12/17.
  */

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import gilcu2.session.{RestService, Session}
import gilcu2.session.Session.{CarVolumen, SessionId, ToCar}
import org.scalatest.{Matchers, WordSpec}
import spray.json._
import DefaultJsonProtocol._
import akka.actor.{ActorRef, ActorSystem}
import gilcu2.session.Models.ServiceJsonProtoocol._
import com.github.nscala_time.time.Imports._



class SessionSpec extends WordSpec with Matchers with ScalatestRouteTest with RestService {

  //  implicit val system: ActorSystem
  val sessionTimeout = 1.hour
  implicit val session: ActorRef = system.actorOf(Session.props(sessionTimeout), "Session")

  val jsonRegister = ByteString(
    s"""
       |{
       |    "name":"test",
       |    "email":"test@gmail.com",
       |    "addr":"malaga"
       |}
        """.stripMargin)

  val jsonLogin = ByteString(
    s"""
       |{
       |    "name":"test"
       |}
        """.stripMargin)


  "Session API" should {
    "Posting to /login should get the session Id" in {

      val postRequest0 = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRegister))

      postRequest0 ~> route

      val postRequest1 = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonLogin))

      postRequest1 ~> route ~> check {
        println(responseAs[String])
        status.isSuccess() shouldEqual true
      }
    }

  }

  "Session API" should {
    "Posting to /tocar should add products to car" in {

      val postRequest0 = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRegister))

      postRequest0 ~> route

      val postRequest1 = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonLogin))

      var sessionId = "test"
      postRequest1 ~> route ~> check {
        val r = responseAs[String].parseJson.convertTo[SessionId]
        sessionId = r.sessionId
      }

      val toCar = ToCar(sessionId, "guitar", 2)
      val toCarJson = toCar.toJson.prettyPrint

      val postRequest2 = HttpRequest(
        HttpMethods.POST,
        uri = "/tocar",
        entity = HttpEntity(MediaTypes.`application/json`, toCarJson))

      postRequest2 ~> route ~> check {
        println(responseAs[String])
        val n = responseAs[String].parseJson.convertTo[CarVolumen].quantity
        n shouldEqual 2
      }
    }

  }

}

class SessionSpecTimeout extends WordSpec with Matchers with ScalatestRouteTest with RestService {

  //  implicit val system: ActorSystem
  val sessionTimeout = 5.seconds
  implicit val session: ActorRef = system.actorOf(Session.props(sessionTimeout), "Session")

  val jsonRegister = ByteString(
    s"""
       |{
       |    "name":"test",
       |    "email":"test@gmail.com",
       |    "addr":"malaga"
       |}
        """.stripMargin)

  val jsonLogin = ByteString(
    s"""
       |{
       |    "name":"test"
       |}
        """.stripMargin)

  "Session API" should {
    "Fail when Posting to /tocar after session timeout" in {

      val postRequest0 = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRegister))

      postRequest0 ~> route

      val postRequest1 = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonLogin))

      var sessionId = "test"
      postRequest1 ~> route ~> check {
        val r = responseAs[String].parseJson.convertTo[SessionId]
        sessionId = r.sessionId
      }

      Thread.sleep(6.seconds.millis)

      val toCar = ToCar(sessionId, "guitar", 2)
      val toCarJson = toCar.toJson.prettyPrint

      val postRequest2 = HttpRequest(
        HttpMethods.POST,
        uri = "/tocar",
        entity = HttpEntity(MediaTypes.`application/json`, toCarJson))

      postRequest2 ~> route ~> check {
        status.isSuccess() shouldEqual false
      }
    }

  }

}