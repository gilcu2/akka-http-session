package gilcu2.session

/**
  * Created by gilcu2 on 2/10/17.
  */

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import com.github.nscala_time.time.Imports._

// To return error msgs

trait InMsg

trait OutMsg

object Session {

  def props(timeout: Duration = 1.hour): Props = Props(classOf[Session], timeout)

  case class Register(name: String, email: String, addr: String) extends InMsg

  case class Login(name: String) extends InMsg

  case class Logout(sessionId: String) extends InMsg

  case class ToCar(sessionId: String, product: String, quant: Int) extends InMsg

  case class Shop(sessionId: String) extends InMsg

  case class RegisterOk(name: String) extends OutMsg

  case class SessionId(sessionId: String) extends OutMsg

  case class Error(desc: String) extends OutMsg

  case class CarVolumen(quantity: Int) extends OutMsg

  case class CarShopped(quantity: Int) extends OutMsg

}

class Session(val timeout: Duration = 1.hour) extends Actor {

  import Session._
  import scala.collection.mutable.Map

  val usersData = Map[String, Register]()
  val cars = Map[String, Map[String, Int]]() //digest->car
  val carsVolumen = Map[String, Int]()
  val lastConn = Map[String, DateTime]()

  context.setReceiveTimeout(scala.concurrent.duration.Duration(timeout.millis, scala.concurrent.duration.MILLISECONDS))

  // Suppose md5 s unique relation

  def md5(s: String): String = {
    import java.security.MessageDigest
    MessageDigest.getInstance("MD5").digest((s + "secret").getBytes).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }

  def cleanSession(id: String): Unit = {
    cars.remove(id)
    carsVolumen.remove(id)
    lastConn.remove(id)
  }

  def receive = {

    case x: Register =>
      println(s"Register: ${x.name}")

      usersData(x.name) = x
      sender() ! RegisterOk(x.name)

    case x: Login =>
      println(s"Login: ${x.name}")

      if (usersData.contains(x.name)) {
        val sessionId = md5(x.name)

        if (!cars.contains(sessionId)) cars(sessionId) = Map[String, Int]()

        lastConn(sessionId) = DateTime.now
        sender() ! SessionId(sessionId)
      }
      else sender() ! Error(s"Not registered user: ${x.name}")

    case x: Logout =>
      println(s"Logout: ${x.sessionId}")

      cleanSession(x.sessionId)

    case x: ToCar =>
      if (cars.contains(x.sessionId)) {
        val car = cars(x.sessionId)
        car(x.product) = car.getOrElse(x.product, 0) + x.quant
        carsVolumen(x.sessionId) = carsVolumen.getOrElse(x.sessionId, 0) + x.quant
        lastConn(x.sessionId) = DateTime.now
        sender() ! CarVolumen(carsVolumen(x.sessionId))
      }
      else sender() ! Error(s"Not valid session: ${x.sessionId}")

    case x: Shop =>
      if (cars.contains(x.sessionId)) {
        val n = carsVolumen(x.sessionId)
        carsVolumen(x.sessionId) = 0
        cars(x.sessionId) = Map[String, Int]()
        lastConn(x.sessionId) = DateTime.now
        sender() ! CarShopped(n)
      }
      else sender() ! Error(s"Not valid session: ${x.sessionId}")

    case ReceiveTimeout =>
      val time4out = DateTime.now - timeout
      val sessionsOut = lastConn.filter(_._2 < time4out).map(_._1)
      sessionsOut.foreach(cleanSession(_))

    case _ => sender() ! Error("Dont supported msg in Session")
  }
}