
import org.scalatest.{Assertions, FlatSpec, Matchers}

import scala.util.parsing.json

/**
  * Created by Malika on 4/16/16.
  */

/*
  This class tests if Liwc is loaded correctly and returns correct results on parsing Trie
 */

class LiwcSpec extends FlatSpec with Matchers{

  //keep list of categories
  //val categories = List("funct", "pronoun", "ppron", "i", "we", "you", "shehe", "they", "ipron", "article", "verb", "auxverb", "past", "present", "future", "adverb", "preps", "conj", "negate", "quant", "number", "swear", "social", "family", "friend", "humans", "affect", "posemo", "negemo", "anx", "anger", "sad", "cogmech", "insight", "cause", "discrep", "tentat", "certain", "inhib", "incl", "excl", "percept", "see", "hear", "feel", "bio", "body", "health", "sexual", "ingest", "relativ", "motion", "space", "time", "work", "achieve", "leisure", "home", "money", "relig", "death", "assent", "nonfl", "filler")
  // load dictionary
  val _trie = {ScalaJson.scalafy(json.JSON.parseFull(scala.io.Source.fromFile(new java.io.File( "." ).getCanonicalPath+"/src/test/resources/data/liwc2007.trie").mkString)).asInstanceOf[Map[String,Any]]
  }
  val reg = "[,.:;'\"\\?\\-!\\(\\)]".r

  it should "return correct word count for review with one word" in {
    val text = "accepts"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    Assertions.assert(counts("WC")==1)
  }

  it should "give correct word categories for review with one word" in {
    val text = "accepts"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    Assertions.assert(counts("posemo")==1)
    Assertions.assert(counts("affect")==1)
    Assertions.assert(counts("cogmech")==1)
    Assertions.assert(counts("insight")==1)

  }

  it should "return correct word count for review with 'I hate it' words" in {
    val text = "I hate it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    Assertions.assert(counts("WC")==3)
  }

  it should "give correct categories for review with 'I hate it' " in {
    val text = "I hate it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    Assertions.assert(counts("anger")==1)
    Assertions.assert(counts("negemo")==1)
    Assertions.assert(counts("funct")==2)
    Assertions.assert(counts("present")==1)
    Assertions.assert(counts("affect")==1)
    Assertions.assert(counts("pronoun")==2)
    Assertions.assert(counts("i")==1)
    Assertions.assert(counts("ipron")==1)
    Assertions.assert(counts("ppron")==1)
    Assertions.assert(counts("verb")==1)

  }

  it should "'contain all the categories as 'I hate It' except negative ones ;for review 'I love it' " in {
    val text = "I love it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
   //contains all the categories as 'I Hate It' except negative ones
    Assertions.assert(counts("funct")==2)
    Assertions.assert(counts("present")==1)
    Assertions.assert(counts("affect")==1)
    Assertions.assert(counts("pronoun")==2)
    Assertions.assert(counts("i")==1)
    Assertions.assert(counts("ipron")==1)
    Assertions.assert(counts("ppron")==1)
    Assertions.assert(counts("verb")==1)
    try {
      Assertions.assert(counts("negemo")==1)
    }catch{
      case e: NoSuchElementException => Assertions.assert(true)
      case _ => Assertions.assert(false)
    }
    try {
      Assertions.assert(counts("anger") == 1)
    }catch{
      case e: NoSuchElementException => Assertions.assert(true)
      case _ => Assertions.assert(false)
    }

  }

  it should "work for reviews with quotes " in {
    val text = "I'm loving it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    //contains all the categories as 'I Hate It' except negative ones
    Assertions.assert(counts("funct")==2)
    Assertions.assert(counts("present")==1)
    Assertions.assert(counts("affect")==1)
    Assertions.assert(counts("pronoun")==2)
    Assertions.assert(counts("i")==1)
    Assertions.assert(counts("ipron")==1)
    Assertions.assert(counts("ppron")==1)
    Assertions.assert(counts("verb")==1)
    try {
      Assertions.assert(counts("negemo")==1)
    }catch{
      case e: NoSuchElementException => Assertions.assert(true)
      case _ => Assertions.assert(false)
    }
    try {
      Assertions.assert(counts("anger") == 1)
    }catch{
      case e: NoSuchElementException => Assertions.assert(true)
      case _ => Assertions.assert(false)
    }

  }

  it should "work for reviews with quotes and #" in {
    val text = "I'm loving it #loveit"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview,_trie)
    Assertions.assert(counts("WC")==4)
  }

}