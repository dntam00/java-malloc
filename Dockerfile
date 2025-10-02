FROM eclipse-temurin:17-jdk-jammy
RUN apt update && apt-get install -y google-perftools
ENV LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libtcmalloc.so.4

WORKDIR /app

COPY build/libs/java-malloc-1.0-SNAPSHOT-all.jar /app/app.jar

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]