package gilcu2.session

/**
  * Created by gilcu2 on 2/10/17.
  */

import akka.actor.{Actor, ActorRef, Props}

object App {
  def props(sessionRef: ActorRef): Props = Props(classOf[App], sessionRef)

  case object GetWorker

  case class ReturnWorker(worker: ActorRef)
}

class App(val sessionActor: ActorRef) extends Actor {

  import App._
  import Session.Error

  def receive = {

    case GetWorker =>
      println(s"App  GetWorker")

      val worker = context.actorOf(Worker.props(sessionActor), "Worker")

      sender() ! ReturnWorker(worker)

    case _ => sender() ! Error("Dont supported msg in App")


  }
}

