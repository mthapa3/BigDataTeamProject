package mongodb.initialize

/**
  * Created by rrh on 4/7/16.
  */
object MongoDBReviewsImport {

  def main(args: Array[String]) {
    val arguments = new Array[String](7)
    arguments(0) = "--uri"
    arguments(1) = "mongodb://" + MongoFactory.SERVER + "/" + MongoFactory.DATABASE + "." + MongoFactory.REVIEWS_COLLECTION
    arguments(2) = "--file"
    arguments(3) = MongoFactory.REVIEWS_FILENAME
    arguments(4) = MongoFactory.UPSERT
    arguments(5) = "--fileType"
    arguments(6) = "reviews"


    val mongoimport = new MongoImport();
    mongoimport.dataImport(arguments)

  }


}
