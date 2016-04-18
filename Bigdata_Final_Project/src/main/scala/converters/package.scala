  import scala.collection.mutable
  import scala.collection.mutable.Map
  import scala.io.Source

  /**
    * Created by Malika on 4/10/16.
    */
  package object converters {

    object DicToTrieConverter {

      type Trie = mutable.Map[String,Any]

      def parseLine(line: String): List[String] = line.trim().split("\t").toList

      def addCategory(as: List[String], categories: Map[String, String]): Map[String, String] = categories ++= Map(as.head -> as(1))

      def toTrie(as: List[String], categories: Map[String, String], trie: Trie): Trie =
        traverse(as.head, as.tail map (e => categories(e)), trie,0)

      def add[A](key: String, cs: List[String], trie: Trie,keyPos : Int): Trie = {
        if (keyPos == key.length) Map("$" -> cs)
        else {
          key.charAt(keyPos) match {
            case '*' => trie ++= Map("*" -> cs) ++= add(key, cs, Map(),keyPos+1)
            case x: Char => trie ++= Map(x.toString -> add(key, cs, Map(),keyPos+1))
          }
        }
      }

      def traverse[A](key: String, cs: List[String], trie: Trie,pos: Int): Trie = {
        try {
          val c = key.charAt(pos).toString
          if (!trie.contains(c)) add(key, cs, trie,pos)
          else traverse(key, cs, trie(c).asInstanceOf[Trie], pos + 1)
        }catch{
          case e:Exception => trie
        }
      }

      def isAllDigits(x: String):Boolean = x forall Character.isDigit


      // return Trie
      def convert(fileName: String): Trie = {
        val bufferedSource = Source.fromFile(fileName)
        val categories = Map().asInstanceOf[Map[String, String]]
        val trie = mutable.Map().asInstanceOf[Trie]
        for (line <- bufferedSource.getLines()) {
          if (isAllDigits(parseLine(line).head))
            addCategory(parseLine(line), categories)
          else if (!line.startsWith("%")) {
            toTrie(parseLine(line), categories, trie)
          }
        }
        bufferedSource.close
        trie
      }
    }


    def main(args: Array[String]): Unit = {
      println(DicToTrieConverter.convert(new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/test.dic"))
    }
  }
