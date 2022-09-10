FROM openjdk:17-alpine
COPY build/libs/RestApiAwsS3-0.0.1-SNAPSHOT.jar /restapiaws.jar
CMD ["java", "-jar", "/restapiaws.jar"]
