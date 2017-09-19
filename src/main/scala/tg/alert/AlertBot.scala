package tg.alert

import java.util.concurrent.Executors

import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._
import io.vertx.scala.core.Vertx
import org.joda.time.{DateTime, DateTimeConstants, Interval, LocalDate}

import scala.collection.mutable
import scala.concurrent.Future

class AlertBot(token: String, vertx: Vertx) extends BaseBot(token) with Polling {


  val chatId: Long = -1001120701192l

  object Persone extends Enumeration {
    type Persone = Value
    val Anton, Alex, Pavel = Value
  }

  import Persone._

  val que = mutable.Queue(Anton, Alex, Pavel)
  val DEF_TIMER: Long = -15
  var timerId: Long = DEF_TIMER

  override def receiveMessage(msg: Message): Unit = {
    for (text <- msg.text) {
      text match {
       // case "/reg Anton"  => makefront(Anton)
      //  case "/reg Pavel" => makefront(Pavel)
      //  case "/reg Alex" => makefront(Alex)
        case "/who" => who
      //  case "/next" => next
        case "/help" => help
        case "/t" => update_timer

        case _ =>
        //          request(SendMessage(msg.source, text.reverse))
      }
    }
  }

  private def makefront(persone: Persone): Unit = {
    while (que.front != persone) {
      que += que.dequeue()
    }
  }

  def who: Future[Message] = {
    request(SendMessage(chatId, s"На этих выходных убирает: ${que.front}"))
  }

  def next: que.type = {
    que += que.dequeue()
  }

  def help: Future[Message] = {
    request(SendMessage(chatId, s"Команды: \n" +
      s"/reg {Alex,Pavel,Anton} - делает первым в очереди\n" +
      s"/next - переключает на следующего\n" +
      s"/who - узнать кто дежуный на этих выходных\n" +
      s"/t - показать таймер\n"))
  }

  def send(string: String): Future[Message] = {
    request(SendMessage(chatId, string))
  }

  def update_timer: Long = {
    if (timerId != DEF_TIMER)
      vertx.cancelTimer(timerId)
    var time = calcNextDayOfWeek(DateTimeConstants.SATURDAY, timeHours = 10)
    timerId = vertx.setTimer(time,
      h => {
        who
        next
      })
    send(s"Сработает через: ${time / 1000 / 3600} часов")
    time
  }

  def calcNextDayOfWeek(dayOfWeek: Int, timeHours: Int = 0, timeMinutes: Int = 0): Long = {
    var d = new LocalDate()
    var day: LocalDate =

      if (d.getDayOfWeek < dayOfWeek)
        d.withDayOfWeek(dayOfWeek)
      else
        d.plusWeeks(1).withDayOfWeek(dayOfWeek)

    new Interval(
      DateTime.now(),
      day.toDateTimeAtCurrentTime.withTimeAtStartOfDay().plusHours(timeHours).plusMinutes(timeMinutes)
    ).toDurationMillis
  }

  update_timer
}

abstract class BaseBot(val token: String) extends TelegramBot