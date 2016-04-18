import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Random

/**
  * Created by rrh on 4/10/16.
  */



object ReviewFeatureSetGenerator {

  @volatile private var positiveList:org.apache.spark.broadcast.Broadcast[Array[(String, Long)]] = null
  @volatile private var negativeList:org.apache.spark.broadcast.Broadcast[Array[(String, Long)]] = null
  @volatile private var satiqList:org.apache.spark.broadcast.Broadcast[Array[(String, Long)]] = null
  @volatile private var wineList:org.apache.spark.broadcast.Broadcast[Array[(String, Long)]] = null
  @volatile private var jeoList:org.apache.spark.broadcast.Broadcast[Array[(String, Long)]] = null
  @volatile private var liwcList:org.apache.spark.broadcast.Broadcast[Map[String, Any]] = null
  @volatile private var categoriesList:org.apache.spark.broadcast.Broadcast[scala.collection.immutable.Set[String]] = null

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName(Properties.APP_NAME).setMaster(Properties.MASTER)
    val sc = new SparkContext(conf)
    Logger.getRootLogger().setLevel(Level.ERROR)

    val sqlContext = new SQLContext(sc)
    val fulldfs:org.apache.spark.sql.DataFrame = sqlContext.read.json(Properties.REVIEWS_TEST_5_PATH)

    val df = fulldfs.select(Properties.ASIN,Properties.REVIEWER_ID,Properties.REVIEW_TEXT,Properties.OVERALL)

    positiveList = sc.broadcast(sc.textFile(Properties.POSITIVE_WORDS_PATH).zipWithIndex().collect())
    negativeList = sc.broadcast(sc.textFile(Properties.NEGATIVE_WORDS_PATH).zipWithIndex().collect())
    satiqList = sc.broadcast(sc.textFile(Properties.SAT_IQ_WORDS_PATH).zipWithIndex().collect())
    wineList = sc.broadcast(sc.textFile(Properties.WINE_WORDS_PATH).zipWithIndex().collect())
    jeoList = sc.broadcast(sc.textFile(Properties.JEO_WORDS_PATH).zipWithIndex().collect())
    val _trie = {
      ScalaJson.fromFile[Map[String, Any]](Properties.LIWC_TRIE_2007_PATH,sc)
    }
    // println("LIWC-WordSet =>"+_trie)
    liwcList = sc.broadcast(_trie)

    val categories = Properties.CATEGORIES
     categoriesList = sc.broadcast(Set(categories: _*))



    val records = df.map(generateRecord)


    records.foreach(println)

    records.saveAsTextFile(Properties.DATA_OUT_PATH+ new Random().nextInt())
    println("Done")
  }

  def generateRecord(row:org.apache.spark.sql.Row): String ={
    val posWset = positiveList.value.toMap
    val negWset = negativeList.value.toMap
    val satIQWset= satiqList.value.toMap
    val wineWset= wineList.value.toMap
    val jeoWset= jeoList.value.toMap
    val liwcWset= liwcList.value
    val categoriesSet= categoriesList.value

    /*
     * regular expression to remove special characters and split review text into words for scoring
     */
    val regex = "[,.:;'\"\\?\\-!\\(\\)\\$]".r
    val temp = row(2).toString().replaceAll("\\$","")
    val review = row(2).toString().split(" ").flatMap(line => line.split("[\\s]")).map(word => regex.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)


    val postiveCnt = review.map{word => posWset.get(word).size match { case 1 => 1 case 0 => 0}}.foldLeft(0)(_+_)
    val negativeCnt = review.map{word => negWset.get(word).size match { case 1 => 1 case 0 => 0}}.foldLeft(0)(_+_)
    val satiqCnt = review.map{word => satIQWset.get(word).size match { case 1 => 1 case 0 => 0}}.foldLeft(0)(_+_)
    val wineCnt = review.map{word => wineWset.get(word).size match { case 1 => 1 case 0 => 0}}.foldLeft(0)(_+_)
    val jeoCnt = review.map{word => jeoWset.get(word).size match { case 1 => 1 case 0 => 0}}.foldLeft(0)(_+_)
    val liwcCnt = Liwc.apply(review,liwcWset)

    val diffSet = categoriesSet.diff(liwcCnt.keySet)
    val diffMap = diffSet.map(i => i -> 0).toMap
    val interMap = Map("positive" -> postiveCnt, "negative" -> negativeCnt, "satiq" -> satiqCnt, "wine" -> wineCnt, "jeopardy" -> jeoCnt)
    val newMap = interMap ++ liwcCnt ++ diffMap
    val finalMap = newMap.toSeq.sortBy((_._1))


    val featureString = finalMap.toList.flatMap( a => List(a._1+"|"+a._2)).mkString(" ")

    (row(1)+"-"+row(0)+" "+featureString+" "+"rating"+"|"+row(3))

  }

}



