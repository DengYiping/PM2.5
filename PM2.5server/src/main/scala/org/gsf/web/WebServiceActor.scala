package org.gsf.web
import org.gsf.data.DAO
/**
  * Created by Scott on 5/11/16.
  */
class WebServiceActor extends WebService{
  val superviser = context.parent
  def actorRefFactory = context
  def receive = runRoute(route)
  val dao = DAO.mongo
}
