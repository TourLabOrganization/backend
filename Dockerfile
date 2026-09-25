FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

USER nobody

# alpine에는 tzdata가 없지만 JDK가 자체 tzdb를 들고 있어 zone ID를 해석할 수 있다.
# MaxRAMPercentage는 컨테이너 메모리 제한 기준이므로 인스턴스 크기와 무관하게 동작한다.
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-XX:MaxRAMPercentage=65", "-jar", "app.jar"]
