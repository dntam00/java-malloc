FROM eclipse-temurin:17-jdk-jammy
RUN apt update && apt-get install -y google-perftools
ENV LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libtcmalloc.so.4
