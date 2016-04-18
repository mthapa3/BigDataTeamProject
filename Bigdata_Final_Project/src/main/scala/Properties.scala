/**
  * Created by Malika on 4/18/16.
  */
package object Properties {

  lazy val POSITIVE_WORDS_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/positive_words.txt"
  lazy val NEGATIVE_WORDS_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/negative_words.txt"
  lazy val SAT_IQ_WORDS_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/sat_iq_words.txt"
  lazy val WINE_WORDS_PATH = new java.io.File(".").getCanonicalPath + "//Bigdata_Final_Project/src/main/resources/data/wine_words.txt"
  lazy val JEO_WORDS_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/jeo_words.txt"
  lazy val REVIEWS_TEST_5_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/test5.json"
  lazy val LIWC_TRIE_2007_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/liwc2007.trie"
  lazy val DATA_OUT_PATH = new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/out/"
  val CATEGORIES = List("funct", "pronoun", "ppron", "i", "we", "you", "shehe", "they", "ipron", "article", "verb", "auxverb", "past", "present", "future", "adverb", "preps", "conj", "negate", "quant", "number", "swear", "social", "family", "friend", "humans", "affect", "posemo", "negemo", "anx", "anger", "sad", "cogmech", "insight", "cause", "discrep", "tentat", "certain", "inhib", "incl", "excl", "percept", "see", "hear", "feel", "bio", "body", "health", "sexual", "ingest", "relativ", "motion", "space", "time", "work", "achieve", "leisure", "home", "money", "relig", "death", "assent", "nonfl", "filler")
  val REVIEWER_ID = "reviewerID"
  val ASIN = "asin"
  val REVIEW_TEXT = "reviewText"
  val OVERALL = "overall"
  val METADATA = "metadata"
  val APP_NAME = "Simple Application"
  val MASTER = "local"
}
