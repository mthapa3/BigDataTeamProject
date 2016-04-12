/**
  * Created by rrh on 4/10/16.
  */


object CheckCode extends App{
  val text = "I've had this Kindle for a while now. Many of the reviews say it all so I don't think I could add much. But I feel like I need to speak on behalf of this Kindle to shield it from reviewers and owners who seem to have no idea what they bought. If this could help clear up any confusion for a first time buyer then writing this is worth it. So I have to begin by asking reviewers that confuse us all?\n\nDo you have any clue what you bought? Let's all say it out loud: THIS KINDLE DOES NOT HAVE A LIGHT!!!!!! It is an entry level model with no light. Amazon has always sold non lighted Kindles, and I still have many of them. They are a cheap way to get into the e-reader scene. Can you read a real book in the dark? Of course not. So these non lighted Kindles are not epic failures for not having a light just as the last novel you bought was not an epic failure for not having a built in light. It is ridiculous to buy this Kindle then give it a 1 star review because it doesn't have a light. Read the product descriptions. This Kindle has no light, the Paperwhite and Voyage do.\n\nScream all to the heavens: THIS KINDLE DOES NOT HAVE PAGE TURNING BUTTONS!!!! To buy this Kindle then give it a 1 star review because your old Kindle had page turning buttons and this one does not is insane. Could you not see when you clicked on \"BUY\" that this Kindle had no page turning buttons. Sometimes I think there are two kinds of Kindle owners: those who want page turning buttons and those who don't mind moving their thumb a nano meter to turn a page. If you can tell by my sarcasm, I belong to the latter bunch.\n\nThen of course there are the technically deficient that have no clue what wifi is, are unable to sign on, and blame it on the Kindle. BAM! another 1 star review. Well I feel for you on this one, If you are new to the scene all Kindles come wifi ready, ready to sign on to your wifi set up at home. When this is done you can buy book from your Kindle or computer. All of your purchases are held in that magical storage space called \"the cloud\" and you can access those from any Kindle registered under your name.\n\nNow for my two cents. This is a great Kindle. The software is the latest, similar to the Paperwhite. It's quick, has any feature you could ask for from an entry level Kindle. I don't understand the hate for touch screen. You should embrace it. See that word on the page you don't know? Touch it with your finger and a dictionary pops up giving you the definition. How many clicks on the Kindle Keyboard would this same function take (still have my Keyboard and love it, but probably like one loves an antique typewriter). As far as page turns go, see my nano meter thumb comment above. Once you get used to it it's a piece of cake. Hey, the first time I picked up an iPhone to type a text message \"how are you?\" it came out \"lskdlj eiieoi c;?\" You need to acclimate yourself to a touch screen. Once you do those page turning buttons will start to feel like cheap little bits of plastic that will eventually stop working.\n\nUnder a lamp or in natural daylight the text is bold and easy to see. Some fonts are bolder than others. This is also a very light Kindle. I've found kind of a sweet spot for holding Kindles. Pinching the bezel to hold it is awkward. I place the bottom left corner in the middle of my palm, rest the back of it on my four fingers, and tilt it slightly to the left so it doesn't fall in my lap. My thumb is perfectly free to move that nano meter to turn a page.\n\nSo let's sum up:\n*This Kindle does not have a light.\n*This Kindle does not have page turning buttons.\n*This Kindle (like all Kindles) has wifi capability. When you are home on your wifi you can buy and download books from the Kindle store. If you go to the beach, you cannot. If you want this then get 3G. This is like a cell phone. Personally I've never needed to buy a book outside of my home so never felt the need for 3G.\n\nIf this helps one person it was worth typing."
  val regex = "[,.:;'\"\\?\\-!\\(\\)]".r

  val testReview = text.split(" ").flatMap(line => line.split("[\\s]")).map(word => regex.replaceAllIn(word.trim.toLowerCase, "")).filter(word => !word.isEmpty)
  val counts = Liwc.apply(testReview)
  val normalized_counts = counts.mapValues(_.toDouble / counts("WC"))
  println(counts)
  println(counts.size)
  println(normalized_counts)


  val categories = List("funct", "pronoun", "ppron", "i", "we", "you", "shehe","they", "ipron", "article", "verb", "auxverb", "past", "present", "future","adverb", "preps", "conj", "negate", "quant", "number", "swear", "social","family", "friend", "humans", "affect", "posemo", "negemo", "anx", "anger","sad", "cogmech", "insight", "cause", "discrep", "tentat", "certain","inhib", "incl", "excl", "percept", "see", "hear", "feel", "bio", "body","health", "sexual", "ingest", "relativ", "motion", "space", "time", "work","achieve", "leisure", "home", "money", "relig", "death", "assent", "nonfl","filler")
  val categoriesSet = Set(categories: _*)

  println(categoriesSet.size)


  val diffSet = categoriesSet.diff(counts.keySet)

  println(diffSet)

  val temp = diffSet.map(i => i -> 0).toMap
  val newMap = counts ++ temp

  println(newMap)
  println(newMap.size)
}
