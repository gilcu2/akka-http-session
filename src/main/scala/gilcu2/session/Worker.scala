package gilcu2.session


import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.duration._
import akka.pattern.ask
import gilcu2.session.Session.Register

import scala.concurrent.Await
import scala.util.{Failure, Success}

/**
  * Created by gilcu2 on 2/12/17.
  */

object Worker {
  def props(sessionRef: ActorRef): Props = Props(classOf[Worker], sessionRef)

}


class Worker(val sessionRef: ActorRef) extends Actor {

  import Session.{Error, Login, Logout}
  import context.dispatcher

  def receive = {

    case x: InMsg =>
      println(s"Worker: $x ")

      val result = sessionRef.ask(x)(5 seconds)
      val preSender = sender()

      result.onComplete {
        case Success(value) => {
          println(s"Worker success received : $value ")
          preSender ! value
        }
        case Failure(e) => sender() ! Error(e.toString)
      }

    case _ => sender() ! Error("Dont supported msg in Worker")
  }


}
