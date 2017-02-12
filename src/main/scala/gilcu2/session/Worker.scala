package gilcu2.session


import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.Await
import scala.util.{Failure, Success}

/**
  * Created by gilcu2 on 2/12/17.
  */

object Worker {
  def props(sessionRef: ActorRef): Props = Props(classOf[Worker], sessionRef)

  case class TimeOut()

}


class Worker(val sessionRef: ActorRef) extends Actor {

  import App.Msg
  import Session.Error
  import context.dispatcher

  def receive = {
    case x: Msg =>
      val result = sessionRef.ask(x.msg)(5 seconds)
      result.onComplete {
        case Success(value) => x.sender ! value
        case Failure(e) => x.sender ! Error(e.toString)
      }

    case x: SessionMsg =>
      val result = sessionRef.ask(x)(5 seconds)
      result.onComplete {
        case Success(value) => sender() ! value
        case Failure(e) => sender() ! Error(e.toString)
      }

    case _ => sender() ! Error("Dont supported msg in Worker")
  }


}
