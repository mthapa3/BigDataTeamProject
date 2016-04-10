/**
  * Created by rrh.
  */

import java.io.{ByteArrayOutputStream, PrintStream}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.io.{BufferedSource, Source}

import com.mongodb.casbah.Imports._
import com.mongodb.util.JSON

class MongoImport {
  val usage = """
                |Import JSON data into MongoDB using Casbah scala api
                |
                |When importing JSON documents, each document must be a separate line of the input file.
                |
                |Example:
                |  mongoimport --uri mongodb://localhost/my_db.my_collection < mydocfile.json
                |
                |Options:
                |  --help                                produce help message
                |  --quiet                               silence all non error diagnostic
                |                                        messages
                |  -u [ --uri ] arg                      The connection URI - must contain a collection
                |                                        mongodb://[username:password@]host1[:port1][,host2[:port2]]/database.collection[?options]
                |                                        See: http://docs.mongodb.org/manual/reference/connection-string/
                |  --file arg                            file to import from; if not specified
                |                                        stdin is used
                |  --drop                                drop collection first
                |  --upsert                              insert or update objects that already
                |                                        exist
                |  --upsertFields arg                    comma-separated fields for the query
                |                                        part of the upsert. You should make
                |                                        sure this is indexed
                |  --stopOnError                         stop importing at first error rather
                |                                        than continuing
                |  --jsonArray                           load a json array, not one item per
                |                                        line. Currently limited to 16MB.
              """

  /**
    * The main export program
    *
    * @param args the commandline arguments
    */
  def dataImport(args: Array[String]) {

    /*The time when the execution of this program started, in milliseconds since 1 January 1970 UTC. */
    val executionStart: Long = currentTime

    if (args.length == 0 | args.contains("--help")) {
      Console.err.println(usage)
      sys.exit(1)
    }

    val optionMap = parseArguments(Map(), args.toList)
    val options = getOptions(optionMap)

    if (options.uri == None) {
      Console.err.println(s"Missing URI")
      Console.err.println(usage)
      sys.exit(1)
    }

    // Get source
    val importSource: BufferedSource = options.file match {
      case None => Source.stdin
      case Some(fileName) => Source.fromFile(fileName)
    }

   hideSLF4JWarnings()

    // Get URI
    val mongoClientURI = MongoClientURI(options.uri.get)
    if (mongoClientURI.collection == None) {
      Console.err.println(s"Missing collection name in the URI eg:  mongodb://<hostInformation>/<database>.<collection>[?options]")
      Console.err.println(s"Current URI: $mongoClientURI")
      sys.exit(1)
    }


    // Get the collection
    val mongoClient = MongoClient(mongoClientURI)
    val collection = mongoClient(mongoClientURI.database.get)(mongoClientURI.collection.get)

    if (options.drop) {
      val name = collection.fullName
      if (!options.quiet) Console.err.println(s"Dropping: $name")
      collection.drop()
    }

    // Import JSON in a future so we can output a spinner
    val importer = future { importJson(collection, importSource, options) }

    if (!options.quiet) Console.err.print("Importing...")
    loadingMessage(importer)
    val total = currentTime - executionStart
    if (!options.quiet) Console.err.println(s"Finished: $total ms")  }

  /**
    * Imports JSON into the collection
    *
    * @param collection the collection to import into
    * @param importSource the data source
    * @param options the configuration options
    */
  private def importJson(collection: MongoCollection, importSource: BufferedSource, options: Options) {
    options.jsonArray match {
      case true =>
        // Import all
        val batch = JSON.parse(importSource.mkString).asInstanceOf[BasicDBList]
        val builder = options.stopOnError match {
          case true => collection.initializeOrderedBulkOperation
          case false => collection.initializeUnorderedBulkOperation
        }
        for (doc <- batch) builder.insert(doc.asInstanceOf[DBObject])
        builder.execute()
      case false =>
        // Import in batches of 1000
        val lines = importSource.getLines()
        while (lines.hasNext) {
          val batch = lines.take(1000)
          val builder = options.stopOnError match {
            case true => collection.initializeOrderedBulkOperation
            case false => collection.initializeUnorderedBulkOperation
          }
          for (line <- batch) {
            val doc: MongoDBObject = JSON.parse(line).asInstanceOf[DBObject]
            val check = doc filter {
              case (k, v) => k == "reviewerID" || k == "asin" && v != ""
            }

            if (check.size > 1) {
            options.upsert match {
              case false => builder.insert(doc)
              case true =>
                val (query, update) = options.upsertFields match {
                  case Some(value) =>
                    val query = doc filter {
                      case (k, v) => value contains k
                    }
                    val update = doc filter {
                      case (k, v) => !(value contains k)
                    }
                    (query, update)
                  case None =>
                    val query = doc filter {
                      case (k, v) => k == "reviewerID" || k == "asin"
                    }
                    val update = doc filter {
                      case (k, v) => k != "reviewerID" || k != "asin"
                    }
                    (query, update)
                }
                builder.find(query).upsert().replaceOne(update)
            }
          }
        }
          try builder.execute()
          catch {
            case be: BulkWriteException => for (e <- be.getWriteErrors) Console.err.println(e.getMessage)
          }
        }
    }
  }


  /**
    * Helper methods
    */

  /**
    * Recursively convert the args list into a Map of options
    *
    * @param map - the initial option map
    * @param args - the args list
    * @return the parsed OptionMap
    */
  private def parseArguments(map: Map[String, Any], args: List[String]): Map[String, Any] = {
    args match {
      case Nil => map
      case "--quiet" :: tail =>
        parseArguments(map ++ Map("quiet" -> true), tail)
      case "-u" :: value :: tail =>
        parseArguments(map ++ Map("uri" -> value), tail)
      case "--uri" :: value :: tail =>
        parseArguments(map ++ Map("uri" -> value), tail)
      case "--file" :: value :: tail =>
        parseArguments(map ++ Map("file" -> value), tail)
      case "--drop" :: tail =>
        parseArguments(map ++ Map("drop" -> true), tail)
      case "--upsert" :: tail =>
        parseArguments(map ++ Map("upsert" -> true), tail)
      case "--upsertFields" :: value :: tail =>
        parseArguments(map ++ Map("upsertFields" -> value.split(",").toList), tail)
      case "--stopOnError" :: tail =>
        parseArguments(map ++ Map("stopOnError" -> true), tail)
      case "--jsonArray" :: tail =>
        parseArguments(map ++ Map("jsonArray" -> true), tail)
      case option :: tail =>
        Console.err.println("Unknown option " + option)
        Console.err.println(usage)
        sys.exit(1)
    }
  }

  /**
    * Convert the optionMap to an Options instance
    *
    * @param optionMap the parsed args options
    * @return Options instance
    */
  private def getOptions(optionMap: Map[String, _]): Options = {
    val default = Options()
    Options(
      quiet = optionMap.getOrElse("quiet", default.quiet).asInstanceOf[Boolean],
      uri = optionMap.get("uri") match {
        case None => default.uri
        case Some(value) => Some(value.asInstanceOf[String])
      },
      file = optionMap.get("file") match {
        case None => default.file
        case Some(value) => Some(value.asInstanceOf[String])
      },
      drop = optionMap.getOrElse("drop", default.drop).asInstanceOf[Boolean],
      upsert = optionMap.getOrElse("upsert", default.upsert).asInstanceOf[Boolean],
      upsertFields = optionMap.get("upsertFields") match {
        case None => default.upsertFields
        case Some(value) => Some(value.asInstanceOf[List[String]])
      },
      stopOnError = optionMap.getOrElse("stopOnError", default.stopOnError).asInstanceOf[Boolean],
      jsonArray = optionMap.getOrElse("jsonArray", default.jsonArray).asInstanceOf[Boolean]
    )
  }

  case class Options(quiet: Boolean = false, uri: Option[String] = None, file: Option[String] = None,
                     drop: Boolean = false, upsert: Boolean = false, upsertFields: Option[List[String]] = None,
                     stopOnError: Boolean = false, jsonArray: Boolean = false)

  private def currentTime = System.currentTimeMillis()

  /**
    * Hide SLF4J  warnings
    */
  private def hideSLF4JWarnings() {
    val stderr = Console.err
    val err = new PrintStream(new ByteArrayOutputStream())
    System.setErr(err)
    MongoClientURI("mongodb://"+MongoFactory.SERVER)
    System.setErr(stderr)
  }

  /**
    * Shows a loading in the console
    *
    * @param someFuture the future we are all waiting for
    */
  private def loadingMessage(someFuture: Future[_]) {
    // Let the user know something is happening until futureOutput isCompleted
    val spinChars = List("*", "#")
    while (!someFuture.isCompleted) {
      spinChars.foreach({
        case char =>
          Console.err.print(char)
          Thread sleep 200
          Console.err.print("\b")
      })
    }
    Console.err.println("")
  }
}
