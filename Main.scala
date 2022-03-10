import scala.concurrent.ExecutionContext
import scala.util._

import akka.actor.typed._
import akka.actor.typed.scaladsl._
import akka.stream.scaladsl._
import akka.util.ByteString

@main def main =
  given system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "play-truncated-req")
  given ExecutionContext = system.classicSystem.dispatcher

  val request = """POST / HTTP/1.1
    |Host: localhost
    |Content-Length: 12
    |
    |abcdef""".stripMargin

  Source.single(ByteString(request))
    .via(Tcp(system).outgoingConnection("localhost", 9000))
    .runWith(Sink.foreach(r => println(r.utf8String)))
    .onComplete {
        case Success(_) =>
          system.terminate()
        case Failure(e) =>
          e.printStackTrace()
          system.terminate()
    }
