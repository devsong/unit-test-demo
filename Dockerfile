FROM openjdk:11.0.15-jre
LABEL MAINTAINER=guanzhisong<guanzhisong@gmail.com>

COPY build/libs/spring-test-demo-1.0.0-SNAPSHOT.jar /app/spring-test-demo.jar
# for maridb4j
COPY libncurses.so.5.9 /usr/lib/x86_64-linux-gnu/
COPY libtinfo.so.5.9 /usr/lib/x86_64-linux-gnu/
RUN ln -s /usr/lib/x86_64-linux-gnu/libncurses.so.5.9 /usr/lib/x86_64-linux-gnu/libncurses.so.5
RUN ln -s /usr/lib/x86_64-linux-gnu/libtinfo.so.5.9   /usr/lib/x86_64-linux-gnu/libtinfo.so.5

WORKDIR /app

CMD ["java", "-jar", "spring-test-demo.jar"]
EXPOSE 6061
