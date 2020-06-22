FROM openjdk:8-jre-alpine

ARG JAR_FILE

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY ${JAR_FILE} $VERTICLE_HOME/app.jar

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar app.jar"]
