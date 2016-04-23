# README #

This README would normally document whatever steps are necessary to get your application up and running.

### Repository for Big Data Project###

--------
MongoDB
--------
MongoDB server installed on AWS EC2
IP : 52.200.184.23

Steps to use
1. Install MongoDB Compass on local System
2. Click on "New Connection" and make the following entries
   Hostname : 52.200.184.23
   Port: 27017
   Authentication : None
   SSL : Off
   FavouriteName : <of your liking>
3. Click on 'Connect'

Note : authentication and DB details will be changed . This is just so that you can at least get connected and start.

-------------------------------------
Connect to MongoDB server through ssh
-------------------------------------
ssh -i ~/mongokeyvalue.pem ec2-user@52.200.184.23 

(Key shared separately)

To check if mongod running(this is the server daemon process) :
 sudo netstat -tulpn | grep :27017

If not run the below command :
 mongod --port 27017 



### Dependencies required ###

* Scala Version - 2.11.8
* casbah mongodb driver - 3.1.1
* Apache Spark 1.6.1 installed/configured
* spark-core 1.6.1
* spark-sql_2.11 1.6.1
* com.google.code.gson 2.3.1
* scalatest-embedmongo 0.2.3
* Apache Zeppelin 0.55
* Python 3+


### Running Zeppelin notebooks ###

* Start Zepplin <Zepplin Home Dir>/bin/start-daemon.sh start
* Import notebooks and run


### To run unit test suite###
* cd <Project Folder>
* sbt clean
* sbt compile
* sbt test

### To execute the program ###
* sbt run
* choose the appropriate module to run

### Path for input files ###
* Path for input files to the main modules can be modified in MongoFactory.scala and Properties.scala



