package org.gsf.data

import java.util

import com.mongodb.{MongoClientURI, MongoClient}
import org.bson._
/**
  * Created by Scott on 5/9/16.
  */
abstract class DAO {
  def store(time:Long,pm2_5:Double,lng:Double,lat:Double,client:String):Boolean
  /*
  def near(lng:Double,lat:Double,radius:Float):String
  def client(id:String):String
  def count:Long
  def count(client:String):Long
  */
}

object DAO{
  /*
  def reactivemongo():DAO ={
    class MongoDAO extends DAO{
      import system.dispatcher
      private val driver = new MongoDriver()
      private val connection = driver.connection(List("localhost"))
      // Gets a reference to the database "plugin"
      private val db = connection("gsf")
      private val collection = db[BSONCollection]("quizzes")
      def store(time:Long,pm2_5:Double,lng:Double,lat:Double,client:String):Boolean = {
        val data = BSONDocument(
          "time" -> BSONLong(time),
          "pm2_5" -> BSONString(pm2_5.toString),
          "client" -> BSONString(client),
          "geo" -> BSONDocument(
            "type" -> BSONString("Point"),
            "coordinates" -> BSONArray(lat,lng))
        )
        val result = collection.insert(data)
        true
      }
    }
    new MongoDAO
  }
  */
  def mongo:DAO = {
    class MongoDAO extends DAO{
      val client = new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"))
      val collection = client.getDatabase("gsf").getCollection("airquality")
      def store(time:Long,pm2_5:Double,lng:Double,lat:Double,client:String):Boolean = {
        val data = new Document()
          .append("time",time)
          .append("pm2_5",pm2_5)
          .append("client",client)
          .append("geo",new Document()
            .append("lat",lat).append("lng",lng)
          )
        collection.insertOne(data)
        true
      }
    }
    new MongoDAO
  }
  def mongo(addr:String, port:String):DAO = {
    class MongoDAO extends DAO{
      val client = new MongoClient(new MongoClientURI("mongodb://" + addr + ":" + port))
      val collection = client.getDatabase("gsf").getCollection("airquality")
      def store(time:Long,pm2_5:Double,lng:Double,lat:Double,client:String):Boolean = {
        val data = new Document()
          .append("time",time)
          .append("pm2_5",pm2_5)
          .append("client",client)
          .append("geo",new Document()
            .append("lat",lat).append("lng",lng)
          )
        collection.insertOne(data)
        true
      }
    }
  }
}
