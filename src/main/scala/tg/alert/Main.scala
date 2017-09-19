package tg.alert

import io.vertx.scala.core.Vertx

object Main extends App {
  val vertx: Vertx = Vertx.vertx()
  new AlertBot("344747482:AAFIYVpe0L1y4B2o4Y0lDQfjibObZFRNFmo", vertx).run()
}
