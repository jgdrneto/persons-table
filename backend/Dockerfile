FROM eclipse-temurin:17-jdk-alpine as build

RUN apk add yq

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests

RUN export APP_ID=$(cat pom.xml | yq -p=xml '.project.artifactId') &&\
    echo "APP_ID=${APP_ID}" &&\
    export APP_VERSION=$(cat pom.xml | yq -p=xml '.project.version') &&\
    echo "APP_VERSION=${APP_VERSION}" &&\
    export "JAR_FILE=${APP_ID}-${APP_VERSION}.jar" &&\
    echo "JAR_FILE=${JAR_FILE}"&&\
    cp /workspace/app/target/${JAR_FILE} /workspace/app/app.jar

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=/workspace/app/app.jar
COPY --from=build ${JAR_FILE} /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]