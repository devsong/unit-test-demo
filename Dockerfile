FROM openjdk:11.0.15-jre
LABEL MAINTAINER=guanzhisong<guanzhisong@gmail.com>

COPY build/libs/unit-test-demo-1.0.0-SNAPSHOT.jar /app/unit-test-demo.jar

WORKDIR /app

CMD ["java", "-jar", "unit-test-demo.jar"]
EXPOSE 6061
