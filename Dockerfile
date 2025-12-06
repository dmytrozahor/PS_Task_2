
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build
COPY . /build

RUN set -eux; \
    if ./gradlew :app:fatJar --no-daemon -x test; then \
      echo "Built fatJar"; \
    else \
      echo "fatJar not available, falling back to bootJar"; \
      ./gradlew :app:bootJar --no-daemon -x test; \
    fi

FROM eclipse-temurin:21-jre

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /build/app/build/libs/*.jar /app/

WORKDIR /app
EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "JAR=$(ls /app | grep -m1 \"\\.jar$\"); if [ -z \"$JAR\" ]; then echo 'No jar found in /app'; exit 1; fi; exec java $JAVA_OPTS -jar /app/$JAR"]
