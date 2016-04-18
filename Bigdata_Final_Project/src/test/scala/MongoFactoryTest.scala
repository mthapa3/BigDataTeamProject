/**
  * Created by Malika on 4/16/16.
  */
object MongoFactoryTest {
  val SERVER = "localhost"
  val PORT = 27017
  val DATABASE = "AmazonReviews"
  val UPSERT = ""
  val REVIEWS_COLLECTION = "Reviews"
  val METADATA_COLLECTION = "Metadata"
  val REVIEWS_FILENAME = new java.io.File(".").getCanonicalPath + "/src/test/resources/data/reviewstest2000.json"
  val REVIEWS_UPSERT_FILENAME = new java.io.File(".").getCanonicalPath + "/src/test/resources/data/reviewsupserttest.json"
  val METADATA_FILENAME = new java.io.File(".").getCanonicalPath + "/src/test/resources/data/metatest2000.json"
  val METADATA_UPSERT_FILENAME = new java.io.File(".").getCanonicalPath + "/src/test/resources/data/metaupserttest.json"
  val REVIEWS_FEATURE_FILENAME = new java.io.File(".").getCanonicalPath + "/src/test/resources/data/testfeatureupdate"
}
