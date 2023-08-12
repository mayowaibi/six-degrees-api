# Six Degrees API
## Description
This is a Java implementation of a backend service that computes the shortest path (in relation to movies) between any given actor and Hollywood actor Kevin Bacon. This concept is based on the [Six Degrees of Kevin Bacon](https://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon).

## Examples
<img width="850" alt="neo4j DB example" src="https://github.com/mayowaibi/six-degrees-api/assets/66337024/97c5d265-647c-434c-a463-ee58345b0d9f">

* Consider the actor `Tom Cruise`:
  * Tom Cruise acted in “A Few Men” Kevin Bacon<br/>

So we would say that Tom Cruise has a “Bacon number” of 1.<br/>

* Consider the actor `Emil Eifrem`:
  * Emil Eifrem acted in “The Matrix” with Keanu Reeves
  * Keanu Reeves acted in “The Devil's Advocate” with Christopher Evans
  * Christopher Evans acted in “A Few Men” with Kevin Bacon

So we would say that Emil Eifrem has a “Bacon number” of 3.<br/>

## Application Details
The application is essentially a RESTful API that uses HTTP requests to create actor and movie nodes, relationships between the nodes, and compute information based on the nodes in the database. The backend is stored in a Neo4j database using the Java Neo4j driver.
Neo4j Specifications:
- Version: 4.4.23 or lower
- Database Port: ```bolt://localhost:7687```
- Username: neo4j
- Password: 12345678

## Node/Relationship Properties
- Actors:
  - Node label: Actor
  - Node properties: actorId, name

- Movies:
  - Node label: Movie
  - Node properties: movieId, name
  
- Relationships:
  - Relationship label: ACTED_IN

## Endpoints
* Request/Response Body:
  * JSON format
  
* Response Status:
  * 200 OK for a successful add
  * 400 BAD REQUEST if the request body is improperly formatted or missing required
information
  * 404 NOT FOUND if the actors or movies do not exist when adding a relationship
  * 500 INTERNAL SERVER ERROR if save or add was unsuccessful (Java Exception is
thrown)

* Existing endpoints:
  * (PUT) http://localhost:8080/api/v1/addActor
    * parameters:
      * name: string
      * actorId: string
  * (PUT) http://localhost:8080/api/v1/addMovie
    * parameters:
      * name: string
      * movieId: string      
  * (PUT) http://localhost:8080/api/v1/addRelationship
    * parameters:
      * actorId: string
      * movieId: string 
  * (GET) http://localhost:8080/api/v1/getActor
    * parameter:
      * actorId: string  
  * (GET) http://localhost:8080/api/v1/getMovie
    * parameter:
      * actorId: string
  * (GET) http://localhost:8080/api/v1/hasRelationship
    * parameters:
      * actorId: string
      * movieId: string 
  * (GET) http://localhost:8080/api/v1/computeBaconNumber
    * parameter:
      * actorId: string
  * (GET) http://localhost:8080/api/v1/computeBaconPath
    * parameter:
      * actorId: string
  * (GET) http://localhost:8080/api/v1/getCommonMovies
    * parameters:
      * actor1Id: string
      * actor2Id: string   

## Testing
To test the application, you can either:
- Download and run the Postman collection package [here](https://github.com/mayowaibi/six-degrees-api/blob/main/six_degrees_api.postman_collection.json).
- Download and run the Robot test file [here](https://github.com/mayowaibi/six-degrees-api/blob/main/test.robot).

For information on how to install and use the Robot Framework for testing, click [here](https://github.com/robotframework/robotframework/blob/master/INSTALL.rst).
