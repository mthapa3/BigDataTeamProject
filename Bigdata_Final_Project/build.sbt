name := "Bigdata_Final_Project"

version := "1.0"

scalaVersion := "2.11.8"


val scalaTestVersion = "2.2.4"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "org.mongodb" %% "casbah" % "3.1.1",
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" % "spark-sql_2.11" % "1.6.1",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.3-SNAPSHOT"
)


