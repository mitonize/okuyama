java -cp ./classes;./lib/log4j-1.2.14.jar;./lib/javamail-1.4.1.jar;./lib/commons-codec-1.4.jar -Xss512k -server -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseParNewGC -verbose:gc -Xmx512m -Xms512m okuyama.base.JavaMain /Main.properties /DataNode_small.properties -s 2048