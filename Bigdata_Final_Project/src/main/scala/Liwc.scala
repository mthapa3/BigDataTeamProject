
/**
  * Created by rrh.
  */

import scala.Predef._
import scala.util.parsing.json
object Liwc {

  val categories = List("funct", "pronoun", "ppron", "i", "we", "you", "shehe", "they", "ipron", "article", "verb", "auxverb", "past", "present", "future", "adverb", "preps", "conj", "negate", "quant", "number", "swear", "social", "family", "friend", "humans", "affect", "posemo", "negemo", "anx", "anger", "sad", "cogmech", "insight", "cause", "discrep", "tentat", "certain", "inhib", "incl", "excl", "percept", "see", "hear", "feel", "bio", "body", "health", "sexual", "ingest", "relativ", "motion", "space", "time", "work", "achieve", "leisure", "home", "money", "relig", "death", "assent", "nonfl", "filler")
  lazy val _trie = {
    ScalaJson.fromFile[Map[String, Any]](new java.io.File( "." ).getCanonicalPath+"/Bigdata_Final_Project/src/main/resources/data/liwc2007.trie")
  }

  def _walk(token: String, index: Int, cursor: Map[String, Any]): List[String] = {
    if (cursor.contains("*")) {
      // assert cursor("*") = List[String]
      return cursor("*").asInstanceOf[List[String]]
    }
    else if (cursor.contains("$") && index == token.size) {
      return cursor("$").asInstanceOf[List[String]]
    }
    else if (index < token.size) {
      var letter = token(index).toString
      if (cursor.contains(letter)) {
        val nextCursor = cursor(letter).asInstanceOf[Map[String, Any]]
        return _walk(token, index + 1, nextCursor)
      }
    }
    return List()
  }

  // : Map[String, Int]
  def apply(tokens: Seq[String]) = {
    // returns a map from categories to counts
    val categories = tokens.map(_walk(_, 0, _trie))
    Map("Dic" -> categories.count(_.size > 0), "WC" -> tokens.size) ++
      categories.flatten.groupBy(identity).mapValues(_.size)
  }

}

object ScalaJson {
  def scalafy(entity: Any): Any = {
       entity match {
      case Some(x) => x
      case None => "?"
    }
  }

  def fromFile[A](path: String): A = {
    val raw = io.Source.fromFile(path).mkString
    scalafy(json.JSON.parseFull(raw)).asInstanceOf[A]

  }
}