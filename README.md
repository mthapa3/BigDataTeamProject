# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###
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



* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact