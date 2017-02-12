package gilcu2.session

import akka.actor.{ActorRef, ActorSystem}

import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

/**
  * Created by gilcu2 on 2/12/17.
  */
object Main {

  def createActors: (ActorSystem, ActorRef) = {
    val system = ActorSystem()
    val session = system.actorOf(Session.props, "Session")
    val app = system.actorOf(App.props(session), "App")
    (system, app)
  }

  def main(args: Array[String]): Unit = {
    import Session._
    import App._

    val (system, app) = createActors
    implicit val timeout = Timeout(5 seconds)

    try {

      val workerF = app.ask(GetWorker)
      val worker = Await.result(workerF, timeout.duration).asInstanceOf[ReturnWorker].worker

      val register = Register("john", "john@gmail.com", "new york")
      val registerF = worker.ask(register)
      val registerR = Await.result(registerF, timeout.duration).asInstanceOf[RegisterOk]

      val loginF = worker.ask(Login(register.name))
      val loginR = Await.result(loginF, timeout.duration).asInstanceOf[SessionId]
      val sessionId = loginR.sessionId

      val tocarF = worker.ask(ToCar(sessionId, "guitar", 2))
      val tocarR = Await.result(tocarF, timeout.duration).asInstanceOf[CarVolumen]

      println(s"You have ${tocarR.quantity} in car")
    }
    finally {
      system.terminate()
    }



  }

}
