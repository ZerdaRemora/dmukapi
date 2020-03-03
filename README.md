# dmukapi
![](https://github.com/Apple103/dmukapi/workflows/Java%20Build/badge.svg)
![](https://github.com/Apple103/dmukapi/workflows/Test%20Runner/badge.svg)

## Setting up Dev Environment
The project consists of three major components: a Spring Boot powered backend, 
an Angular frontend and a Postgres database.

Sample IntelliJ run configurations can be found in _.idea/runConfigurations_.
Each can be used individually, or alternatively, the `Run Dev Stack` compound
configuration can be used to run all three at once.


### Spring Boot Backend
The project currently targets JDK 8 but should be compatible with later JDK versions too.

If using IntelliJ, a Spring Boot run configuration should be created, using 
`com.bclers.dmukapi.DmukapiApplication` as the Main Class and `dmukapi.Main` as the 
module.

Run a Gradle Import when first launching the project and when pulling new changes from 
the repo. The `bootJar` gradle task will build a .jar file of the backend for deployment.

The backend runs on port 8080 by default.


### Angular Frontend
The frontend targets Angular 9 and requires node and npm installed.

To run a local development server (on port 4200), run `npm start` in the 
_angularclient_ directory.

The frontend application expects to find endpoints hosted locally on port 8080.

`ng build` will compile the Angular project to static HTML, JS and CSS and saves them 
in the _angularclient/dist_ directory.
The `buildAngular` gradle task will run `ng build` for you.


### Postgres Database
The backend application uses a postgres database for storing comment data.
The easiest way to setup a postgres database is to run it as a Docker container.

The dev database Docker compose file (found at _docker/dev/docker-compose.yml_) can 
be used for quickly setting up a database. This can be accomplished by running 
`docker-compose -f ./docker/dev/docker-compose.yml up -d`. 

Data in the database will be persisted in a named Docker volume (`db-data`).


### Config Files
The Spring Boot backend application expects to find a number of different properties 
files to be used when connecting to different services.
The properties files currently used by the project are:

* postgres.properties
* reddit.properties

These should be located in the _src/main/resources_ directory. Template properties files
for each of these can be found in the same directory, so copying one of these 
template files and filling in the correct information is the easiest way to get set-up.

If using a local postgres docker container, use the following in the _postgres.properties_
file:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

The above properties files are .gitignored and subsequently should **not** be committed to
the repository.