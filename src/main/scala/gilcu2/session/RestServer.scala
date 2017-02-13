package gilcu2.session

/**
  * Created by gilcu2 on 2/12/17.
  */

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.nscala_time.time.Imports._

/**
  * Created by madhu on 8/11/15.
  */


class RestServer(implicit val system: ActorSystem,
                 implicit val materializer: ActorMaterializer,
                 implicit val session: ActorRef
                ) extends RestService {
  def startServer(address: String, port: Int) = {
    Http().bindAndHandle(route, address, port)
  }
}

object RestServer {

  def main(args: Array[String]) {

    implicit val actorSystem = ActorSystem("rest-server")
    implicit val materializer = ActorMaterializer()
    val sessionTimeout = 1.hour
    implicit val session: ActorRef = actorSystem.actorOf(Session.props(sessionTimeout), "Session")

    val server = new RestServer()
    server.startServer("localhost", 8080)
  }
}