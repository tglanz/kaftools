FROM openjdk:17-jdk-slim-bullseye

ENV KAFKA_HOME=/opt/kafka
ENV KAFKA_BRANCH=3.1.0-rc0

RUN apt-get update
RUN apt-get install -y git vim curl wget

RUN git clone --branch $KAFKA_BRANCH --single-branch https://github.com/apache/kafka.git $KAFKA_HOME

WORKDIR $KAFKA_HOME
RUN ./gradlew jar


CMD ["/bin/bash"]
