package gilcu2.session

/**
  * Created by gilcu2 on 2/10/17.
  */

import akka.actor.Actor


object Session {

  case class Login(user: String, sender: Actor)

  case class Logout(sessionID: String)

}

class Session extends Actor {

  import Session._
  import scala.collection.mutable.Map


  val sessions = Map[String, String]()
  // user->digest
  val cars = Map[String, Map[String, Int]]() //digest->car

  def md5(s: String): String = {
    import java.security.MessageDigest
    MessageDigest.getInstance("MD5").digest(s.getBytes).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }

  def receive = {

    case x: Login =>
      println(s"Login: ${x.user}")

      val digest = md5(x.user)
      if (!sessions.contains(digest)) {
        s
      }

      sender() ! true

    case x: Logout =>
      println(s"Logout: ${x.sessionID}")


  }
}