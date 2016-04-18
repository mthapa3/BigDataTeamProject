package converters

import org.apache.spark.SparkContext

import scala.util.parsing.json

/**
  * Created by Malika on 4/18/16.
  */
object ScalaJson {
  def scalafy(entity: Any): Any = {
    entity match {
      case Some(x) => x
      case None => "?"
    }
  }

  def fromFile[A](path: String, sc: SparkContext): A = {
    val raw = sc.textFile(path).reduce(_ + _)
    scalafy(json.JSON.parseFull(raw)).asInstanceOf[A]

  }
}
