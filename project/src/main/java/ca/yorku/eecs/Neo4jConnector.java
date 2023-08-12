package ca.yorku.eecs;
import static org.neo4j.driver.v1.Values.parameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

/**
 * This class is used as a persistence layer to
 * communicate directly with the database using
 * Cypher commands in pre-built methods.
 */
public class Neo4jConnector {
    private Driver driver;
    private String uriDb;
    private final String kevinBaconId = "nm0000102";

    public Neo4jConnector() {
        uriDb = "bolt://localhost:7687"; // may need to change if you used a different port for your DBMS
        Config config = Config.builder().withoutEncryption().build();
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"), config);
    }

    /**
     * This method is used to add an actor to the Neo4j database.
     * @param name The name of the actor to be added
     * @param actorId The ID of the actor to be added
     * @pre The actor must not already exist in the database
     */
    public void addActor(String name, String actorId) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (a:Actor {name: $x, actorId: $y})",
                    parameters("x", name, "y", actorId)));
        }
    }

    /**
     * This method is used to check for the existence of an actor in the Neo4j database.
     * @param actorId The ID of the actor who is being checked
     * @return true if the actor exists, false otherwise
     */
    public boolean actorExists(String actorId) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x})"
                        + "RETURN COUNT(a) > 0 as exists", parameters("x", actorId));
                if (result.hasNext()) {
                    return result.next().get("exists").asBoolean();
                }
            }
        }
        return false;
    }

    /**
     * This method is used to get an actor from the Neo4j database.
     * @param actorId The ID of the actor to be retrieved
     * @pre The actor must exist
     * @return A formatted JSON String of the actor's information
     */
    public String getActor(String actorId) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x})"
                        + " OPTIONAL MATCH (a)-[:ACTED_IN]->(m:Movie)"
                        + " RETURN {name: a.name, actorId: a.actorId, movies: COLLECT(CASE WHEN m IS NULL THEN NULL ELSE m.movieId END) }"
                        + " AS actorDetails",
                        parameters("x", actorId));
                if (result.hasNext()) {
                    Value actorDetailsValue = result.next().get("actorDetails");
                    JSONObject actorDetailsJson = new JSONObject(actorDetailsValue.asMap());
                    formattedJson = actorDetailsJson.toString(4);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        assert formattedJson != null;
        return formattedJson;
    }

    /**
     * This method is used to add a movie to the Neo4j database.
     * @param name The name of the movie to be added
     * @param movieId The ID of the movie to be added
     * @pre The movie must not already exist in the database
     */
    public void addMovie(String name, String movieId) {
        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (m:Movie {name: $x, movieId: $y})",
                    parameters("x", name, "y", movieId)));
        }
    }

    /**
     * This method is used to check for the existence of a movie in the Neo4j database.
     * @param movieId The ID of the movie to be checked
     * @return true if the movie exists, false otherwise
     */
    public boolean movieExists(String movieId) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (m:Movie {movieId: $x})"
                        + "RETURN COUNT(m) > 0 as exists", parameters("x", movieId));
                if (result.hasNext()) {
                    return result.next().get("exists").asBoolean();
                }
            }
        }
        return false;
    }

    /**
     * This method is used to get a movie from the Neo4j database.
     * @param movieId The ID of the movie to be retrieved
     * @pre The actor must exist
     * @return A formatted JSON String of the movie's information
     */
    public String getMovie(String movieId) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (m:Movie {movieId: $x})"
                                + " OPTIONAL MATCH (a:Actor)-[:ACTED_IN]->(m)"
                                + " RETURN {name: m.name, movieId: m.movieId, actors: COLLECT(CASE WHEN a IS NULL THEN NULL ELSE a.actorId END) }"
                                + " AS movieDetails",
                        parameters("x", movieId));
                if (result.hasNext()) {
                    Value movieDetailsValue = result.next().get("movieDetails");
                    JSONObject movieDetailsJson = new JSONObject(movieDetailsValue.asMap());
                    formattedJson = movieDetailsJson.toString(4);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        assert formattedJson != null;
        return formattedJson;
    }

    /**
     * This method is used to add a relationship between an actor and a movie to the Neo4j database.
     * @param actorId The ID of the actor to create a relationship with
     * @param movieId The ID of the movie to create a relationship with
     * @pre The relationship must not already exist in the database
     */
    public void addRelationship(String actorId, String movieId) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH (a:Actor {actorId: $x}),"
                    + " (m:Movie {movieId: $y})" +
                    " MERGE (a)-[r:ACTED_IN]->(m)", parameters("x", actorId, "y", movieId)));
        }
    }

    /**
     * This method is used to check for the existence of a relationship in the Neo4j database.
     * @param actorId The ID of the actor to be checked
     * @param movieId The ID of the movie to be checked
     * @return true if the relationship exists, false otherwise
     */
    public boolean relationshipExists(String actorId, String movieId) {
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x}), (m:Movie {movieId: $y})"
                        + " OPTIONAL MATCH (a)-[r:ACTED_IN]->(m)"
                        + " RETURN COUNT(r) > 0 as exists", parameters("x", actorId, "y", movieId));
                if (result.hasNext()) {
                    return result.next().get("exists").asBoolean();
                }
            }
        }
        return false;
    }

    /**
     * This method is used to check if an actor and movie have a relationship
     * in the Neo4j database and get the boolean result.
     * @param actorId The ID of the actor to be checked
     * @param movieId The ID of the movie to be checked
     * @pre The actor and the movie must both exist
     * @return A formatted JSON String with a boolean value showing
     * whether the actor and movie have a relationship or not
     */
    public String hasRelationship(String actorId, String movieId) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x}), (m:Movie {movieId: $y})"
                                + " OPTIONAL MATCH (a)-[r:ACTED_IN]->(m)"
                                + " RETURN {actorId: a.actorId, movieId: m.movieId, hasRelationship: CASE WHEN r IS NULL THEN false ELSE true END}"
                                + " AS relationshipDetails",
                        parameters("x", actorId, "y", movieId));
                if (result.hasNext()) {
                    Value movieDetailsValue = result.next().get("relationshipDetails");
                    JSONObject movieDetailsJson = new JSONObject(movieDetailsValue.asMap());
                    formattedJson = movieDetailsJson.toString(4);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        assert formattedJson != null;
        return formattedJson;
    }

    /**
     * This method is used to compute the Bacon number of an actor using the Neo4j database.
     * @param actorId The ID of the actor whose Bacon number is to be computed
     * @pre The actor must exist
     * @return A formatted JSON String containing the Bacon number of the actor if there is a path, else return null
     */
    public String computeBaconNumber(String actorId) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x}), (bacon:Actor{actorId: $y})"
                                + " RETURN { baconNumber: CASE WHEN a.actorId = $y THEN 0"
                                + " ELSE length(shortestPath((a)-[:ACTED_IN*]-(bacon))) / 2 END }"
                                + " AS baconNumber",
                        parameters("x", actorId, "y", kevinBaconId));
                if (result.hasNext()) {
                    Value baconNumberValue = result.next().get("baconNumber");
                    JSONObject baconNumberJson = new JSONObject(baconNumberValue.asMap());
                    formattedJson = baconNumberJson.toString(4);
                    if (formattedJson.contains("null")) formattedJson = null;
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return formattedJson;
    }

    /**
     * This method is used to compute the Bacon path of an actor in the Neo4j database.
     * @param actorId The ID of the actor whose Bacon path is to be computed
     * @pre The actor must exist and the bacon number must not be null
     * @return A formatted JSON String containing the Bacon path of the actor
     */
    public String computeBaconPath(String actorId) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x}), (bacon:Actor{actorId: $y})"
                                + " RETURN { baconPath: CASE WHEN a.actorId = $y THEN [a.actorId]"
                                + " ELSE REDUCE(s = [], n IN nodes(shortestPath((a)-[:ACTED_IN*]-(bacon))) |"
                                + " CASE WHEN n:Actor THEN s + [n.actorId]"
                                + " WHEN n:Movie THEN s + [n.movieId] ELSE s END) END }"
                                + " AS baconPath",
                        parameters("x", actorId, "y", kevinBaconId));
                if (result.hasNext()) {
                    Value baconPathValue = result.next().get("baconPath");
                    JSONObject baconPathJson = new JSONObject(baconPathValue.asMap());
                    formattedJson = baconPathJson.toString(4);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        assert formattedJson != null;
        return formattedJson;
    }

    /**
     * This method is used to get a list of movies that the given actors have both acted in.
     * @param actor1Id The actorID of the first actor
     * @param actor2Id The actorID of the second actor
     * @pre Both actors must exist and they must be different
     * @return A formatted JSON String containing a list of the common movies both actors have acted in
     */
    public String getCommonMovies(String actor1Id, String actor2Id) {
        String formattedJson = null;
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (a:Actor {actorId: $x}), (b:Actor {actorId: $y})"
                                + " OPTIONAL MATCH (a)-[:ACTED_IN]->(m:Movie)<-[:ACTED_IN]-(b)"
                                + " RETURN { commonMovies: COLLECT (DISTINCT m.movieId) }"
                                + " AS commonMovies",
                        parameters("x", actor1Id, "y", actor2Id));
                if (result.hasNext()) {
                    Value commonMoviesValue = result.next().get("commonMovies");
                    JSONObject commonMoviesJson = new JSONObject(commonMoviesValue.asMap());
                    formattedJson = commonMoviesJson.toString(4);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        assert formattedJson != null;
        return formattedJson;
    }

    /**
     * Used to close the Neo4j driver
     */
    public void close() {
        driver.close();
    }
}