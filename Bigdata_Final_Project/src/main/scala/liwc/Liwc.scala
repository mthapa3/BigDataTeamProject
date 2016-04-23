package liwc


/**
  * Created by rrh.
  */

import scala.Predef._

object Liwc {

  val categories = Properties.CATEGORIES

  def _walk(token: String, index: Int, cursor: Map[String, Any]): List[String] = {
    if (cursor.contains("*")) {
      // assert cursor("*") = List[String]
      return cursor("*").asInstanceOf[List[String]]
    }
    else if (cursor.contains("$") && index == token.length) {
      return cursor("$").asInstanceOf[List[String]]
    }
    else if (index < token.length) {
      val letter = token(index).toString
      if (cursor.contains(letter)) {
        val nextCursor = cursor(letter).asInstanceOf[Map[String, Any]]
        return _walk(token, index + 1, nextCursor)
      }
    }
    List()
  }

  // : Map[String, Int]
  def apply(tokens: Seq[String], liwcWset: Map[String, Any]) = {
    // returns a map from categories to counts
    val categories = tokens.map(_walk(_, 0, liwcWset))
    Map("Dic" -> categories.count(_.nonEmpty), "WC" -> tokens.size) ++
      categories.flatten.groupBy(identity).mapValues(_.size)
  }

}

