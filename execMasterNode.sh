#!/bin/sh
java -classpath ./classes:./lib/log4j-1.2.14.jar:./lib/javamail-1.4.1.jar:./lib/commons-codec-1.4.jar -Xmx256m -Xms128m org.batch.JavaMain /Main.properties /MasterNode.properties
