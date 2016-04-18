package mongodb.initialize

/**
  * Created by rrh on 4/7/16.
  */
object MongoDBMetaDataImport {

  def main(args: Array[String]) {
    val arguments = new Array[String](7)
    arguments(0) = "--uri"
    arguments(1) = "mongodb://" + MongoFactory.SERVER + "/" + MongoFactory.DATABASE + "." + MongoFactory.METADATA_COLLECTION
    arguments(2) = "--file"
    arguments(3) = MongoFactory.METADATA_FILENAME
    arguments(4) = MongoFactory.UPSERT
    arguments(5) = "--fileType"
    arguments(6) = "metadata"


    val mongoimport = new MongoImport();
    mongoimport.dataImport(arguments)

  }


}
