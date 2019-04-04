Author Tran Huu Hoang
to run the code, first install: 
twitter4j-4.0.7
java-json.jar
json-simple-1.1.1.jar
junit-4.10.jar

Then open an IDE (Eclipse IDE), open the project.
 
Change EVERY path appear in the code to your path.

Then in command line, direct to the maven project. (Using cd)

Then type: mvn install

Then type: mvn exec:java -D"exec.mainClass"="com.codebind.TwitterPost"