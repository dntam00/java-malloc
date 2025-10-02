FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/java-malloc-1.0-SNAPSHOT-all.jar /app/app.jar

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]