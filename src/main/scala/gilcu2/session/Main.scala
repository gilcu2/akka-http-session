package gilcu2.session

import akka.actor.{ActorRef, ActorSystem}

import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import gilcu2.session.Worker.WorkerMsg

import scala.concurrent.duration._

/**
  * Created by gilcu2 on 2/12/17.
  */
object Main {

  def createActors: ActorRef = {
    val system = ActorSystem()
    val session = system.actorOf(Session.props)
    val app = system.actorOf(App.props(session))
    app
  }

  def main(args: Array[String]): Unit = {
    import Session._
    import App._

    val app = createActors
    implicit val timeout = Timeout(5 seconds)

    val register = Register("john", "john@gmail.com", "new york")
    val registerF = app.ask(register)
    val registerR = Await.result(registerF, timeout.duration).asInstanceOf[WorkerMsg]
    val worker = registerR.ref

    val loginF = worker.ask(Login(register.name))
    val loginR = Await.result(loginF, timeout.duration).asInstanceOf[WorkerMsg]
    val sessionId = loginR.msg.asInstanceOf[SessionId].sessionId

    val tocarF = worker.ask(ToCar(sessionId, "guitar", 2))
    val tocarR = Await.result(loginF, timeout.duration).asInstanceOf[CarVolumen]

    println(s"You have ${tocarR.quantity} in car")


  }

}
