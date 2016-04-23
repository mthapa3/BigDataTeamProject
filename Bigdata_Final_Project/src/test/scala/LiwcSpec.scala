
import converters.ScalaJson
import liwc.Liwc
import org.scalatest.{FlatSpec, Matchers}

import scala.util.parsing.json

/**
  * Created by Malika on 4/16/16.
  */

/*
  This class tests if Liwc is loaded correctly and returns correct results on parsing Trie
 */

class LiwcSpec extends FlatSpec with Matchers {

  // load dictionary
  val _trie = {
    ScalaJson.scalafy(json.JSON.parseFull(scala.io.Source.fromFile(Properties.LIWC_TRIE_2007_PATH).mkString)).asInstanceOf[Map[String, Any]]
  }
  val reg = "[,.:;'\"\\?\\-!\\(\\)]".r

  it should "return correct word count for review with one word" in {
    val text = "accepts"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    assert(counts("WC") == 1)
  }

  it should "give correct word categories for review with one word" in {
    val text = "accepts"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    assert(counts("posemo") == 1)
    assert(counts("affect") == 1)
    assert(counts("cogmech") == 1)
    assert(counts("insight") == 1)

  }

  it should "return correct word count for review with 'I hate it' words" in {
    val text = "I hate it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    assert(counts("WC") == 3)
  }

  it should "give correct categories for review with 'I hate it' " in {
    val text = "I hate it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    assert(counts("anger") == 1)
    assert(counts("negemo") == 1)
    assert(counts("funct") == 2)
    assert(counts("present") == 1)
    assert(counts("affect") == 1)
    assert(counts("pronoun") == 2)
    assert(counts("i") == 1)
    assert(counts("ipron") == 1)
    assert(counts("ppron") == 1)
    assert(counts("verb") == 1)

  }

  it should "'contain all the categories as 'I hate It' except negative ones ;for review 'I love it' " in {
    val text = "I love it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    //contains all the categories as 'I Hate It' except negative ones
    assert(counts("funct") == 2)
    assert(counts("present") == 1)
    assert(counts("affect") == 1)
    assert(counts("pronoun") == 2)
    assert(counts("i") == 1)
    assert(counts("ipron") == 1)
    assert(counts("ppron") == 1)
    assert(counts("verb") == 1)
    try {
     if(counts("negemo") == 1) assert(false)
    } catch {
      case e: NoSuchElementException => assert(true)
    }
    try {
      if(counts("anger") == 1)assert(false)
    } catch {
      case e: NoSuchElementException => assert(true)
    }

  }

  it should "work for reviews with quotes " in {
    val text = "I'm loving it"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    //contains all the categories as 'I Hate It' except negative ones
    assert(counts("funct") == 2)
    assert(counts("present") == 1)
    assert(counts("affect") == 1)
    assert(counts("pronoun") == 2)
    assert(counts("i") == 1)
    assert(counts("ipron") == 1)
    assert(counts("ppron") == 1)
    assert(counts("verb") == 1)
    try {
      if(counts("negemo") == 1) assert(false)
    } catch {
      case e: NoSuchElementException => assert(true)
    }
    try {
      if(counts("anger") == 1) assert(false)
    } catch {
      case e: NoSuchElementException => assert(true)
    }

  }

  it should "work for reviews with quotes and #" in {
    val text = "I'm loving it #loveit"
    val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => reg.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
    val counts = Liwc.apply(testReview, _trie)
    assert(counts("WC") == 4)
  }

}