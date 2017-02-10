package sample.hello

/**
  * Created by gilcu2 on 2/10/17.
  */
object Main {

  def main(args: Array[String]): Unit = {
    akka.Main.main(Array(classOf[HelloWorld].getName))
  }

}