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

  case class WorkerMsg(msg: AnswerMsg, ref: ActorRef)

}


class Worker(val sessionRef: ActorRef) extends Actor {

  import App.AppMsg
  import Worker.WorkerMsg
  import Session.{Error, Login, Logout}
  import context.dispatcher

  def receive = {
    case x: AppMsg =>
      val result = sessionRef.ask(x.msg)(5 seconds)
      result.onComplete {
        case Success(value) => x.sender ! WorkerMsg(x.asInstanceOf[AnswerMsg], self)
        case Failure(e) => x.sender ! Error(e.toString)
      }

    case x: Login =>
      val result = sessionRef.ask(x)(5 seconds)
      result.onComplete {
        case Success(value) => sender() ! value
        case Failure(e) => sender() ! Error(e.toString)
      }

    case x: Logout =>
      sessionRef ! x
      context.stop(self)

    case x: SessionMsg =>
      val result = sessionRef.ask(x)(5 seconds)
      result.onComplete {
        case Success(value) => sender() ! value
        case Failure(e) => sender() ! Error(e.toString)
      }

    case _ => sender() ! Error("Dont supported msg in Worker")
  }


}
