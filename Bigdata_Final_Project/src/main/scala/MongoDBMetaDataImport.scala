
/**
  * Created by rrh on 4/7/16.
  */
object MongoDBMetaDataImport {

  def main(args: Array[String]) {
    var arguments = new Array[String](5)
    arguments(0) = "--uri"
    arguments(1) = "mongodb://" + MongoFactory.SERVER + "/" + MongoFactory.DATABASE + "." + MongoFactory.METADATA_COLLECTION
    arguments(2) = "--file"
    arguments(3) = MongoFactory.METADATA_FILENAME
    arguments(4) = "--upsert"


    val mongoimport = new MongoImport();
    mongoimport.dataImport(arguments)

  }


}
