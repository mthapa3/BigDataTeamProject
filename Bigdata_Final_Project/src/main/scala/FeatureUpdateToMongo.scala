import com.mongodb.casbah.Imports._
import com.mongodb.util.JSON
import org.apache.log4j.{Level, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

import scala.io.BufferedSource

/**
  * Created by rrh on 4/14/2016.
  */

object FeatureUpdateToMongo {
  def main(args: Array[String]) {
     Logger.getRootLogger().setLevel(Level.ERROR)
     val executionStart: Long = currentTime


    val data:BufferedSource = scala.io.Source.fromFile(MongoFactory.REVIEWS_FEATURES_FILENAME)


    val serverDetails = "mongodb://" + MongoFactory.SERVER + "/" + MongoFactory.DATABASE + "." + MongoFactory.REVIEWS_COLLECTION

    // Import File in a future so we can output a spinner
    val importer = future { mongoDbFeatureUpdate(data, serverDetails) }
       println("Importing...")
       loadingMessage(importer)
       val total = currentTime - executionStart
        println("Done with update - Time Taken" + total)

  }


  def mongoDbFeatureUpdate(rows:BufferedSource, uri:String){

    // Get URI
    val mongoClientURI = MongoClientURI(uri)
    if (mongoClientURI.collection.isEmpty) {
      Console.err.println(s"Missing collection name in the URI eg:  mongodb://<hostInformation>/<database>.<collection>[?options]")
      Console.err.println(s"Current URI: $mongoClientURI")
      sys.exit(1)
    }


    // Get the collection
    val mongoClient = MongoClient(mongoClientURI)
    val collection = mongoClient(mongoClientURI.database.get)(mongoClientURI.collection.get)
     mongoUpdate(collection,rows)

  }


  def mongoUpdate(collection: MongoCollection, rows:BufferedSource): Unit ={
    // Import in batches of 1000
    val lines = rows.getLines()
   while (lines.hasNext){
      val batch = lines.take(100000)
      val builder = collection.initializeOrderedBulkOperation

      for (line <- batch){
          val parts = line.toString.split(" ")
          val mymap = scala.collection.mutable.Map[String,String]()
          mymap(Properties.REVIEWER_ID) = parts(0).split("-")(0)
          mymap(Properties.ASIN) = parts(0).split("-")(1)
          val mydata = parts.tail.reverse.tail
          mydata.foreach(stg => {
            val k = stg.split("""\|""")(0)
            val v = stg.split("""\|""")(1)
            mymap(k) = v
          })
         val json = scala.util.parsing.json.JSONObject(mymap.toMap).toString()

        val doc: MongoDBObject =  new MongoDBObject(JSON.parse(json).asInstanceOf[DBObject])
        val (query, update) = {
          val query = doc filter {
            case (k, v) =>  k == Properties.REVIEWER_ID || k == Properties.ASIN
          }
          val update = doc filter {
            case (k, v) => k != Properties.REVIEWER_ID && k != Properties.ASIN
          }
          (query, update)
        }


      val updateQ:MongoDBObject = MongoDBObject(
        "$set" -> update)


       builder.find(query).upsert().updateOne(updateQ)
      }


      try builder.execute()
      catch {
        case be: BulkWriteException => for (e <- be.getWriteErrors) Console.err.println(e.getMessage)
      }

     }

  }

  private def currentTime = System.currentTimeMillis()

  /**
    * Shows a loading in the console
    *
    * @param someFuture the future we are all waiting for
    */
  private def loadingMessage(someFuture: Future[_]) {
    // Let the user know something is happening until futureOutput isCompleted
    val spinChars = List("*", "#")
    while (!someFuture.isCompleted) {
      spinChars.foreach({
        case char =>
          Console.err.print(char)
          Thread sleep 200
          Console.err.print("\b")
      })
    }
    Console.err.println("")
  }

}
