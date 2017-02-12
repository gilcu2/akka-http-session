package gilcu2.session

import akka.actor.{Actor, ActorRef}
import gilcu2.session.Session.Register

/**
  * Created by gilcu2 on 2/12/17.
  */

object Client {

  case object Start

  case object Login

}

class Client(val appActor: ActorRef) extends Actor {

  import Client._


  var worker: Option[ActorRef] = None

  def receive = {
    case Start =>
      val register = Register("john", "john@gmail.com", "new york")
      appActor ! register

    case x: Session.Error =>
      println(s"Server fail $x")
      context.stop(self)

    case Session.RegisterOk =>
      worker = Option(sender())

    case Login =>

  }
}
