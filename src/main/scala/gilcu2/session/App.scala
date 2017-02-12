package gilcu2.session

/**
  * Created by gilcu2 on 2/10/17.
  */

import akka.actor.{Actor, ActorRef, Props}

object App {
  def props(sessionRef: ActorRef): Props = Props(classOf[App], sessionRef)

  case class AppMsg(msg: BeginMsg, sender: ActorRef)
}

class App(val sessionActor: ActorRef) extends Actor {

  import App.AppMsg
  import Session.Error

  def receive = {

    case x: BeginMsg =>
      println(s"App  Msg: ${x}")

      val worker = context.actorOf(Worker.props(sessionActor))

      worker ! AppMsg(x, sender())

    case _ => sender() ! Error("Dont supported msg in App")


  }
}

