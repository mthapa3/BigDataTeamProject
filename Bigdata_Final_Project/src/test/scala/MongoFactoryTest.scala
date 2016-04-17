/**
  * Created by Malika on 4/16/16.
  */
object MongoFactoryTest {
  val SERVER = "localhost"
  val PORT   = 27017
  val DATABASE = "AmazonReviews"
  val UPSERT = ""
  val REVIEWS_COLLECTION = "Reviews"
  val METADATA_COLLECTION = "Metadata"
  val REVIEWS_FILENAME= new java.io.File( "." ).getCanonicalPath+"/Bigdata_Final_Project/src/test/resources/data/test2000.json"
  val METADATA_FILENAME= new java.io.File( "." ).getCanonicalPath+"/Bigdata_Final_Project/src/test/resources/data/teststrict.json"
}
