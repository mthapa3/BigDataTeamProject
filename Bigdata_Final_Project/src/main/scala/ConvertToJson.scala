import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import com.google.gson.Gson

/**
  * Created by bala on 4/14/2016.
  */

case class Result(asin: String, rating: String)

object ConvertToJson {
  def main(args: Array[String]) {
    // Create new Spark Context
    val conf = new SparkConf().setAppName("RegLRSGDStratifiedSampling").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val data = sc.textFile("C://Users//Bala///Downloads//part-00000.txt")

    val json = data.map { line =>
      var parts = line.split(" ")
      val obj  = Result(parts(0).split("""\-""")(1), parts(72).split("""\|""")(1))
      new Gson().toJson(obj)
    }

    println("###########################################")
    json.foreach(println)
    println("###########################################")

  }
}
