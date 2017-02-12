package gilcu2.akka.http

/**
  * Created by gilcu2 on 2/12/17.
  */

import spray.json.DefaultJsonProtocol

/**
  * Created by madhu on 8/11/15.
  */
object Models {

  case class Customer(name: String)

  object ServiceJsonProtoocol extends DefaultJsonProtocol {
    implicit val customerProtocol = jsonFormat1(Customer)
  }

}