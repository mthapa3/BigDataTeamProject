
import com.github.simplyscala.MongoEmbedDatabase
import com.mongodb.casbah.Imports._
import de.flapdoodle.embed.mongo.distribution.Version
import mongodb.initialize.{MongoImport, MongoFactory}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.io.{BufferedSource, Source}

/**
  * Created by Malika on 4/9/16.
  */
class MongoImportSpec extends FunSuite with BeforeAndAfter with MongoEmbedDatabase {

  private val WAIT_TIME = 10000L
  private val mongoDBImport = new MongoImport()
  val connection = MongoClient(MongoFactory.SERVER, MongoFactory.PORT)
  private lazy val metaCollection = connection(MongoFactoryTest.DATABASE)(MongoFactoryTest.METADATA_COLLECTION)
  private lazy val reviewsCollection = connection(MongoFactoryTest.DATABASE)(MongoFactoryTest.REVIEWS_COLLECTION)

  private lazy val argumentsR = new Array[String](7)
  argumentsR(0) = "--uri"
  argumentsR(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.REVIEWS_COLLECTION
  argumentsR(2) = "--file"
  argumentsR(3) = MongoFactoryTest.REVIEWS_FILENAME
  argumentsR(4) = MongoFactoryTest.UPSERT
  argumentsR(5) = "--fileType"
  argumentsR(6) = "reviews"


  private lazy val argumentsM = new Array[String](7)
  argumentsM(0) = "--uri"
  argumentsM(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.METADATA_COLLECTION
  argumentsM(2) = "--file"
  argumentsM(3) = MongoFactoryTest.METADATA_FILENAME
  argumentsM(4) = MongoFactoryTest.UPSERT
  argumentsM(5) = "--fileType"
  argumentsM(6) = "metadata"


  test("Should be able to import Json for Metadata") {

    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      // sleep the thread to give the MongoDBEmbedded instance time to start up
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsM.toList)
      val options = mongoDBImport.getOptions(optionMap)
      mongoDBImport.importJson(metaCollection, importSource, options)
      assert(metaCollection.size == 2000)
    }
  }

  test("Should be able to import Json for Reviews") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      // sleep the thread to give the MongoDBEmbedded instance time to start up
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsR.toList)
      val options = mongoDBImport.getOptions(optionMap)
      mongoDBImport.importJson(reviewsCollection, importSource, options)
      assert(reviewsCollection.size == 2000)
    }
  }
  test("Should return correct results after importing Json for Reviews") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsR.toList)
      val options = mongoDBImport.getOptions(optionMap)
      reviewsCollection.drop()
      assert(reviewsCollection.size == 0)
      mongoDBImport.importJson(reviewsCollection, importSource, options)
      val size = reviewsCollection.find(MongoDBObject("reviewerID" -> "A3JSAGWSLY5044")).size
      assert(size == 1)
    }
  }

  test("Should fail for incorrect request after importing Json for Metadata") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsM.toList)
      val options = mongoDBImport.getOptions(optionMap)
      metaCollection.drop()
      assert(metaCollection.size == 0)
      mongoDBImport.importJson(metaCollection, importSource, options)
      val size = metaCollection.find(MongoDBObject("reviewerID" -> "A3JSAGWSLY5044")).size
      assert(size == 0)
    }
  }

  test("Should return correct results after importing Json for Metadata") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsM.toList)
      val options = mongoDBImport.getOptions(optionMap)
      metaCollection.drop()
      assert(metaCollection.size == 0)
      mongoDBImport.importJson(metaCollection, importSource, options)
      val size = metaCollection.find(MongoDBObject("asin" -> "0594017343")).size
      assert(size == 1)
    }
  }

  test("Should fail for incorrect request after importing Json for  Reviews") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsR.toList)
      val options = mongoDBImport.getOptions(optionMap)
      reviewsCollection.drop()
      assert(reviewsCollection.size == 0)
      mongoDBImport.importJson(reviewsCollection, importSource, options)
      val size = reviewsCollection.find(MongoDBObject("imUrl" -> "http://ecx.images-amazon.com/images/I/21YJXPCzBXL._SY300_.jpg")).size
      assert(size == 0)
    }
  }

  test("Should be able to --upsert for Reviews") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val argumentsU = new Array[String](7)
      argumentsU(0) = "--uri"
      argumentsU(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.REVIEWS_COLLECTION
      argumentsU(2) = "--file"
      argumentsU(3) = MongoFactoryTest.REVIEWS_UPSERT_FILENAME
      argumentsU(4) = "--upsert"
      argumentsU(5) = "--fileType"
      argumentsU(6) = "reviews"

      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsR.toList)
      val options = mongoDBImport.getOptions(optionMap)
      reviewsCollection.drop()
      assert(reviewsCollection.size == 0)
      mongoDBImport.importJson(reviewsCollection, importSource, options)
      assert(reviewsCollection.size == 2000)
      val sizeR = reviewsCollection.find(MongoDBObject("reviewerID" -> "A3JSAGWSLY5044", "reviewerName" -> "Norma Cullen")).size
      assert(sizeR == 1)
      val importSourceU: BufferedSource = Source.fromFile(MongoFactoryTest.REVIEWS_UPSERT_FILENAME)
      val optionMapU = mongoDBImport.parseArguments(Map(), argumentsU.toList)
      val optionsU = mongoDBImport.getOptions(optionMapU)
      mongoDBImport.importJson(reviewsCollection, importSourceU, optionsU)
      assert(reviewsCollection.size == 2000)
      val sizeU = reviewsCollection.find(MongoDBObject("reviewerID" -> "A3JSAGWSLY5044", "reviewerName" -> "Malika Thapa")).size
      val sizeRU = reviewsCollection.find(MongoDBObject("reviewerID" -> "A3JSAGWSLY5044", "reviewerName" -> "Norma Cullen")).size
      assert(sizeU == 1)
      assert(sizeRU == 0)
    }
  }

  test("Should be able to --upsert for Metadata") {
    withEmbedMongoFixture(MongoFactory.PORT, Version.V3_1_0) { mongodProps =>
      Thread.sleep(WAIT_TIME)
      val argumentsU = new Array[String](7)
      argumentsU(0) = "--uri"
      argumentsU(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.METADATA_COLLECTION
      argumentsU(2) = "--file"
      argumentsU(3) = MongoFactoryTest.METADATA_UPSERT_FILENAME
      argumentsU(4) = "--upsert"
      argumentsU(5) = "--fileType"
      argumentsU(6) = "metadata"

      val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_FILENAME)
      val optionMap = mongoDBImport.parseArguments(Map(), argumentsM.toList)
      val options = mongoDBImport.getOptions(optionMap)
      metaCollection.drop()
      assert(metaCollection.size == 0)
      mongoDBImport.importJson(metaCollection, importSource, options)
      assert(metaCollection.size == 2000)
      val sizeM = metaCollection.find(MongoDBObject("asin" -> "0594514789", "imUrl" -> "http://ecx.images-amazon.com/images/I/21YJXPCzBXL._SY300_.jpg")).size
      assert(sizeM == 1)
      val importSourceU: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_UPSERT_FILENAME)
      val optionMapU = mongoDBImport.parseArguments(Map(), argumentsU.toList)
      val optionsU = mongoDBImport.getOptions(optionMapU)
      mongoDBImport.importJson(metaCollection, importSourceU, optionsU)
      assert(metaCollection.size == 2000)
      val sizeU = metaCollection.find(MongoDBObject("asin" -> "0594514789", "imUrl" -> "MALIKA_THAPA_TEST.jpg")).size
      val sizeMU = metaCollection.find(MongoDBObject("asin" -> "0594514789", "imUrl" -> "http://ecx.images-amazon.com/images/I/21YJXPCzBXL._SY300_.jpg")).size
      assert(sizeU == 1)
      assert(sizeMU == 0)
    }
  }

}
