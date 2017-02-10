/**
  * Created by gilcu2 on 2/10/17.
  */

package sample.hello

import akka.actor.Actor

object Greeter {

  case object Greet

  case object Done

}

class Greeter extends Actor {
  def receive = {
    case Greeter.Greet =>
      println("Hello World!")
      sender() ! Greeter.Done
  }
}
