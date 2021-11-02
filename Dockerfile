FROM  openjdk:8-jre-alpine
COPY target/EducationManagementSystemApplication-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
# docker build -t educationmanagementsystemapplication .
# sudo docker run --rm --name app -e POSTGRES_PASSWORD=1234567890 -e POSTGRES_USER=appuser -e POSTGRES_DB=app -d -p 5432:5432 -v app:/var/lib/postgresl/data  postgres