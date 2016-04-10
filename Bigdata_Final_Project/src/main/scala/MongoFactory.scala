/**
  * Created by rrh on 4/9/16.
  */
object MongoFactory {
   val SERVER = "localhost"
   val PORT   = 27017
   val DATABASE = "BigData_FinalProject"
   val REVIEWS_COLLECTION = "reviews"
   val METADATA_COLLECTION = "metadata"
   val REVIEWS_FILENAME= new java.io.File( "." ).getCanonicalPath+"/Bigdata_Final_Project/src/main/resources/data/test2000.json"
   val METADATA_FILENAME= new java.io.File( "." ).getCanonicalPath+"/Bigdata_Final_Project/src/main/resources/data/metatest2000.json"
}

