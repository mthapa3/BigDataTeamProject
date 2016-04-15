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
      traverse(as.head, as.tail map (e => categories.get(e)), trie,0)

    def add[A](key: String, cs: List[Option[A]], trie: Trie): Trie = {
      if (key == "") Map("$" -> toList(cs))
      else {
        key.charAt(0) match {
          case '*' => trie ++= Map("*" -> toList(cs)) ++= add("", cs, Map())
          case x: Char => trie ++= Map(x.toString -> add(key.substring(1), cs, Map()))
        }
      }
    }

    def traverse[A](key: String, cs: List[Option[A]], trie: Trie,pos: Int): Trie = {
      try {
        val c = key.charAt(pos).toString
        if (!trie.contains(c)) add(key.substring(pos), cs, trie)
        else traverse(key, cs, trie.get(c).get.asInstanceOf[Trie], pos + 1)
      }catch{
        case e:Exception => trie
      }
    }


    def toList[A](lso: List[Option[A]]): List[A] = lso flatMap {
      case Some(b) => List(b)
      case None => List()
    }

    def isAllDigits(x: String):Boolean = x forall Character.isDigit


    // return categories
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
    println(DicToTrieConverter.convert(new java.io.File(".").getCanonicalPath + "/Bigdata_Final_Project/src/main/resources/data/LIWC2007_English100131.dic"))
  }
}
