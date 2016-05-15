package org.gsf.web

/**
  * Created by Scott on 5/11/16.
  */
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import spray.routing.HttpService
import spray.json._
import spray.httpx.SprayJsonSupport._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor._
import spray.http.StatusCodes.NotFound
import akka.pattern.ask
import scala.concurrent.Future
import org.gsf.data.DAO

object MasterJson extends DefaultJsonProtocol{
  implicit val hello = jsonFormat1(StateReport)
}
case class StateReport(status:String)
trait WebService extends HttpService with Actor with ActorLogging{
  import MasterJson._
  implicit val timeout = Timeout(5 seconds)
  val dao:DAO
  val superviser:ActorRef
  val route = {
    val dir = "html/"
    path("data" / "store" / Rest){  path =>
      parameters('p,'time,'temp,'latitude,'longitude) { (p,time,temp,latitude,longitude) =>
        detach(){
          jsonpWithParameter("jsonp"){
            complete{
              log.info(p,time,temp,latitude,longitude)
              try{
                if(dao.store(time.toLong,p.toDouble,longitude.toDouble,latitude.toDouble,"")){
                  StateReport("success")
                }
                else
                  StateReport("failure")
              }catch {
                case _:Throwable =>StateReport("failure").toJson.asJsObject
              }
            }
          }
        }
      }
    }~ complete(NotFound)
  }
}
