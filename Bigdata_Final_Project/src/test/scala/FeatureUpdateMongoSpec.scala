import com.github.simplyscala.MongoEmbedDatabase
import com.mongodb.casbah.Imports._
import mongodb.feature.FeatureUpdateToMongo
import mongodb.initialize.{MongoImport, MongoFactory}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.io.{BufferedSource, Source}


/**
  * Created by Malika on 4/17/16.
  */
class FeatureUpdateMongoSpec extends FunSuite with BeforeAndAfter with MongoEmbedDatabase {

  private val mongoDBImport = new MongoImport()
  val connection = MongoClient(MongoFactory.SERVER, MongoFactory.PORT)
  private lazy val reviewsCollection = connection(MongoFactoryTest.DATABASE)(MongoFactoryTest.REVIEWS_COLLECTION)
  private lazy val argumentsR = new Array[String](7)
  argumentsR(0) = "--uri"
  argumentsR(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.REVIEWS_COLLECTION
  argumentsR(2) = "--file"
  argumentsR(3) = MongoFactoryTest.REVIEWS_FILENAME
  argumentsR(4) = MongoFactoryTest.UPSERT
  argumentsR(5) = "--fileType"
  argumentsR(6) = "reviews"

  test("Should be able update Reviews with features") {
    val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FILENAME)
    val optionMap = mongoDBImport.parseArguments(Map(), argumentsR.toList)
    val options = mongoDBImport.getOptions(optionMap)
    assert(reviewsCollection.size == 0)
    mongoDBImport.importJson(reviewsCollection, importSource, options)
    assert(reviewsCollection.size == 2000)
    val sizeR = reviewsCollection.find(MongoDBObject("reviewerID" -> "AKM1MP6P0OYPR")).size
    assert(sizeR == 1)
    //test if feature column present
    val sizeRF = reviewsCollection.find(MongoDBObject("reviewerID" -> "AKM1MP6P0OYPR", "jeopardy" -> "17")).size
    //feature not added
    assert(sizeRF == 0)
    //update review with features
    val importSourceU: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FEATURE_FILENAME)
    FeatureUpdateToMongo.mongoUpdate(reviewsCollection, importSourceU)
    assert(reviewsCollection.size == 2000)
    //feature added
    val sizeRFU = reviewsCollection.find(MongoDBObject("reviewerID" -> "AKM1MP6P0OYPR", "jeopardy" -> "17")).size
    val sizeRU = reviewsCollection.find(MongoDBObject("reviewerID" -> "AKM1MP6P0OYPR")).size
    assert(sizeRFU == 1)
    assert(sizeRU == 1)
  }
}
