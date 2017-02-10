package gilcu2.session

/**
  * Created by gilcu2 on 2/10/17.
  */

import akka.actor.Actor

object App {

  case class Login(user: String)

  case class Logout(sessionID: Long)

  case class ToCar(sessionID: Long, item: String, quantity: Int)

  case class Buy(sessionID: Long)

}

class App extends Actor {

  import App._

  def receive = {

    case x: Login =>
      println(s"App Login: ${x.user}")
      sender() ! true

    case x: Logout =>
      println(s"App Logout: ${x.sessionID}")

    case x: ToCar =>
      println(s"App ToCar: ${x}")

    case x: Buy =>
      println(s"App Buy: ${x.sessionID}")
  }
}

