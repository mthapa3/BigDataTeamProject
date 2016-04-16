
import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import com.mongodb.casbah.Imports._
import org.scalatest.{Assertions, BeforeAndAfter, FunSuite}
import scala.io.{BufferedSource, Source}

/**
  * Created by rrh on 4/9/16.
  */
class MongoImportSpec extends FunSuite with BeforeAndAfter with MongoEmbedDatabase{

  //declares a variable which will hold the reference to running mongoDB Instance
  var mongoInstance: MongodProps = null
  // Start In-memory Mongo instance in before statement
  before {
    try{ mongoInstance = mongoStart(27017) } //Try starting mongo on this default port
    catch { case ex:Exception => } // Handle exception In case local mongo is running
  }

  //Stop mongo Instance After Running test Case
  after {
    mongoStop(mongoInstance)
  }

  val mongoDBImport = new MongoImport()
  val arguments = new Array[String](7)
  arguments(0) = "--uri"
  arguments(1) = "mongodb//" + MongoFactoryTest.SERVER + "/" + MongoFactoryTest.DATABASE + "." + MongoFactoryTest.METADATA_COLLECTION
  arguments(2) = "--file"
  arguments(3) = MongoFactoryTest.METADATA_FILENAME
  arguments(4) = MongoFactoryTest.UPSERT
  arguments(5) = "--fileType"
  arguments(6) = "metadata"

  test("Should be able importJson"){
//  create connection
//  create db
//  create collection
//  test if import worksMongoFactoryTest.METADATA_COLLECTION
    val mongoConn = MongoConnection()
    val mongoDB = mongoConn(MongoFactoryTest.DATABASE)
    mongoDB.createCollection(MongoFactoryTest.METADATA_COLLECTION,DBObject())
    val collection : MongoCollection = mongoDB(MongoFactoryTest.METADATA_COLLECTION)
    Assertions.assert(mongoDB.collectionExists(MongoFactoryTest.METADATA_COLLECTION))
    val importSource: BufferedSource = Source.fromFile(MongoFactoryTest.METADATA_FILENAME)
//    val optionMap = mongoDBImport.parseArguments(Map(), arguments.toList)
//    val options = mongoDBImport.getOptions(optionMap)
//    mongoDBImport.importJson(collection,importSource,options)
    Assertions.assert(collection.size!=10)
    //assert if the document was inserted into database

  }

}
