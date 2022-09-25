FROM eclipse-temurin:17
EXPOSE 8080
RUN mkdir /opt/app
COPY ./target/pdf-web-app-0.0.1.jar /opt/app
CMD ["java", "-jar", "/opt/app/report-integrator-0.0.1.jar"]