FROM openjdk:8-alpine

ADD ./dmukapi.jar dmukapi.jar

# Assumes postgres db is running in a Docker container on the same machine.
# Can be overridden using the -e flag when running the container.
ENV GH_POSTGRES_URL jdbc:postgresql://host.docker.internal:5432/postgres
ENV GH_POSTGRES_USERNAME postgres
ENV GH_POSTGRES_PASSWORD postgres

EXPOSE 8080

CMD java -jar dmukapi.jar
