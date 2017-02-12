package gilcu2.session

/**
  * Created by gilcu2 on 2/12/17.
  */

import spray.json.DefaultJsonProtocol

/**
  * Created by madhu on 8/11/15.
  */
object Models {

  import Session._

  object ServiceJsonProtoocol extends DefaultJsonProtocol {
    implicit val registerProtocol = jsonFormat3(Register)
    implicit val loginProtocol = jsonFormat1(Login)
    implicit val logoutProtocol = jsonFormat1(Logout)
    implicit val tocarProtocol = jsonFormat3(ToCar)
    implicit val shopProtocol = jsonFormat1(Shop)
    implicit val registerOkProtocol = jsonFormat1(RegisterOk)
    implicit val sessionIdProtocol = jsonFormat1(SessionId)
    implicit val errorProtocol = jsonFormat1(Error)
    implicit val carVolumenProtocol = jsonFormat1(CarVolumen)
    implicit val carShoppedProtocol = jsonFormat1(CarShopped)
  }

}