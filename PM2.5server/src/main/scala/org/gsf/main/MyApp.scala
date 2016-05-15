package org.gsf.main
import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.io.IO
import akka.util.Timeout
import org.gsf.web.WebServiceActor
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask
/**
  * Created by Scott on 5/10/16.
  */
object MyApp extends App{
  implicit val system = ActorSystem("ShuoshuoCrawlerSystem")
  val service=system.actorOf(Props(new WebServiceActor),"WebService")
  implicit val timeout=Timeout(5.seconds)
  IO(Http) ? Http.Bind(service,interface="0.0.0.0",port=8080)
}
