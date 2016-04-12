/**
  * Created by rrh on 4/9/16.
  */
object MongoFactory {
   val SERVER = "localhost"
   val PORT   = 27017
   val DATABASE = "AmazonReviews"
   val UPSERT = "--upsert"
   val REVIEWS_COLLECTION = "Reviews"
   val METADATA_COLLECTION = "Metadata"
   val REVIEWS_FILENAME= new java.io.File( "." ).getCanonicalPath+"/src/main/resources/data/test2000.json"
   val METADATA_FILENAME= new java.io.File( "." ).getCanonicalPath+"/src/main/resources/data/metatest2000.json"
}

