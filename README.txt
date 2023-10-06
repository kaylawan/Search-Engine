Java program that crawls the web, builds an index to quickly access the web, and responds to search queries. The index is stored in the disk to accommodate large webs, and is loaded in a web server before queries are run. The search engine supports word and phrase queries, and allows the use of logical and, or, and not operators. Search results are ranked by connectedness (how many other pages link to this page).

== INCLUDED FILES ==

README.txt                 This file
WebCrawler.java            This is some sample code that crawls pages; run this to run the crawler
CrawlingMarkupHandler.java Your Crawler code goes here
Index.java                 A class with serialization code
WebIndex.java              Your index goes here
WebServer.java             Sample code for a simple server; run this to run the webserver
WebQueryEngine.java        Your query engine goes here
Page.java                  A wrapper class for page results
tsoogle.png                Part of the web interface
attoparser-*.jar           A library used for crawling
president.zip              A sample website for you to test with

== CRAWLER INSTRUCTIONS ==

First, make sure you add the attoparser library to your classpath! Then, you can run the WebCrawler
class, providing it ABSOLUTE URLS, on the two provided testing websites. Absolute URLS are prefixed
by "file://", and look something like this:

file://<absolute-path-to-file>

For example, on the command line, using linux:

java -cp attoparser-2.0.0.BETA2.jar:bin assignment.WebCrawler file:///<path-to-project>/president96/index.html

This should run your crawler and save an index to "index.db"!

== WEBSERVER INSTRUCTIONS ==

To run the webserver, run the assignment.WebServer class; this will load your previously generated
index.db file from your crawler, and then set up an HTTP webserver you can connect to in your
browser:

java -cp attoparser-2.0.0.BETA2.jar:bin assignment.WebServer

The program will output "listening on port 1989"; to see the actual website, go to

localhost:1989

in your browser; the UI should show up promptly.
