FROM openjdk:17-alpine
COPY build/libs/RestApiAwsS3-0.0.1-SNAPSHOT.jar /restapiaws.jar
CMD ["java", "-jar", "/restapiaws.jar"]

#ARG aws_access_key="AKIAYVGFQ67EVANGZXDA"
#ENV aws_access_key=${aws_access_key}
#ARG spring_datasource_url="jdbc:postgresql://postgres2/RestApiAwsS3"
#ENV datasource_url=${spring_datasource_url}

