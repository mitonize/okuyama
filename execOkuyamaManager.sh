#!/bin/sh
java -classpath .:./classes:./lib/jetty-6.1.0.jar:./lib/jetty-util-6.1.0.jar:./lib/servlet-api-2.5.jar:./lib/javamail-1.4.1.jar -Xmx128m org.imdst.manager.OkuyamaManagerServer 10088 127.0.0.1:8888,127.0.0.1:8889,127.0.0.1:11211

