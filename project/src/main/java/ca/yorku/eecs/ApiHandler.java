package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to create the different handlers
 * required for the server context.
 */
public class ApiHandler implements HttpHandler {
    Neo4jConnector neo;

    /**
     * This method is called to handle the given request
     * invoked by the server context and allocate it to
     * the corresponding method.
     * @param request the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException When there is an I/O exception
     */
    @Override
    public void handle(HttpExchange request) throws IOException {
        try {
            if (request.getRequestMethod().equals("GET")) {
                handleGet(request);
                System.out.println("GET");
            } else if (request.getRequestMethod().equals("PUT")) {
                handlePut(request);
                System.out.println("PUT");
            } else {
                sendString(request, "Unimplemented method", 501);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendString(request, "Server error", 500);
        }
    }

    /**
     * This method is used to handle PUT requests.
     * @param request The request to be handled
     * @throws IOException When there is an I/O exception
     * @throws JSONException When there is a JSON exception from the request body
     */
    public void handlePut(HttpExchange request) throws IOException, JSONException {
        neo = new Neo4jConnector();

        // Getting the path of the endpoint
        String path = request.getRequestURI().getPath();

        // Getting the JSON input from the request
        String jsonBody = Utils.getBody(request);
        JSONObject jsonObject = new JSONObject(jsonBody);

        try {
            if (path.contains("/addActor")) {
                // If name and actorId are both provided, attempt to add actor, else return 400 status code
                if (jsonObject.has("name") && jsonObject.has("actorId")) {
                    String name = jsonObject.getString("name");
                    String actorId = jsonObject.getString("actorId");
                    // If actor doesn't exist then add actor, else return 400 status code
                    if (!neo.actorExists(actorId)) {
                        neo.addActor(name, actorId);
                        request.sendResponseHeaders(200, -1);
                    } else {
                        sendString(request, "Actor already exists", 400);
                    }
                } else {
                    sendString(request, "Incorrect request body format", 400);
                }
            } else if (path.contains("/addMovie")) {
                // If name and movieId are both provided, attempt to add movie, else return 400 status code
                if (jsonObject.has("name") && jsonObject.has("movieId")) {
                    String name = jsonObject.getString("name");
                    String movieId = jsonObject.getString("movieId");
                        // If movie doesn't exist then add movie, else return 400 status code
                        if (!neo.movieExists(movieId)) {
                            neo.addMovie(name, movieId);
                            request.sendResponseHeaders(200, -1);
                        } else {
                            sendString(request, "Movie already exists", 400);
                        }
                } else {
                    sendString(request, "Incorrect request body format", 400);
                }
            } else if (path.contains("/addRelationship")) {
                // If actorId and movieId are both provided, attempt to add relationship, else return 400 status code
                if (jsonObject.has("actorId") && jsonObject.has("movieId")) {
                    String actorId = jsonObject.getString("actorId");
                    String movieId = jsonObject.getString("movieId");
                    // If actor or movie don't exist, return 404 status code
                    if (!neo.actorExists(actorId) || !neo.movieExists(movieId)) {
                        sendString(request, "Actor/Movie not found", 404);
                    } // If relationship doesn't exist then add relationship, else return 400 status code
                    else if (!neo.relationshipExists(actorId, movieId)) {
                        neo.addRelationship(actorId, movieId);
                        request.sendResponseHeaders(200, -1);
                    } else {
                        sendString(request, "Relationship already exists", 400);
                    }
                } else {
                    sendString(request, "Incorrect request body format", 400);
                }
            } else {
                // Return 400 Bad Request for unknown endpoints/invalid requests
                sendString(request, "Unknown endpoint", 400);
            }
            neo.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Return 500 Internal Server Error if an exception is thrown
            sendString(request, "Server error", 500);
        }
    }

    /**
     * This method is used to handle GET requests
     * @param request The request to be processed
     * @throws IOException When there is an I/O exception
     */
    public void handleGet(HttpExchange request) throws IOException {
        neo = new Neo4jConnector();

        // Getting the path of the endpoint
        String path = request.getRequestURI().getPath();

        // Getting the query parameters
        String query = request.getRequestURI().getQuery();
        Map<String, String> queryParam = Utils.splitQuery(query);

        try {
            if (path.contains("/getActor")) {
                if (query.contains("actorId")) {
                    String actorId = queryParam.get("actorId");
                    // If actor exists, get actor details and return them, else return 404 status code
                    if (neo.actorExists(actorId)) {
                        String actorDetails = neo.getActor(actorId);
                        sendString(request, actorDetails, 200);
                    } else {
                        sendString(request, "Actor not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else if (path.contains("/getMovie")) {
                if (query.contains("movieId")) {
                    String movieId = queryParam.get("movieId");
                    // If movie exists, get movie details and return them, else return 404 status code
                    if (neo.movieExists(movieId)) {
                        String movieDetails = neo.getMovie(movieId);
                        sendString(request, movieDetails, 200);
                    } else {
                        sendString(request, "Movie not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else if (path.contains("/hasRelationship")) {
                if (query.contains("actorId") && query.contains("movieId")) {
                    String actorId = queryParam.get("actorId");
                    String movieId = queryParam.get("movieId");
                    // If actor and movie both exist, get their details and return them, else return 404 status code
                    if (neo.actorExists(actorId) && neo.movieExists(movieId)) {
                        String relationshipDetails = neo.hasRelationship(actorId, movieId);
                        sendString(request, relationshipDetails, 200);
                    } else {
                        sendString(request, "Actor/Movie not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else if (path.contains("/computeBaconNumber")) {
                if (query.contains("actorId")) {
                    String actorId = queryParam.get("actorId");
                    // If actor exists, attempt to compute bacon number, else return 404 status code
                    if (neo.actorExists(actorId)) {
                        String baconNumber = neo.computeBaconNumber(actorId);
                        // If bacon number isn't null (path found), return the bacon number with a 200 status code
                        if (baconNumber != null) {
                            sendString(request, baconNumber, 200);
                        } // If bacon number is null (no path found), return 404 status code
                        else {
                            sendString(request, "No path to Kevin Bacon", 404);
                        }
                    } else {
                        sendString(request, "Actor not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else if (path.contains("/computeBaconPath")) {
                if (query.contains("actorId")) {
                    String actorId = queryParam.get("actorId");
                    // If actor exists, attempt to compute bacon number, else return 404 status code
                    if (neo.actorExists(actorId)) {
                        String baconNumber = neo.computeBaconNumber(actorId);
                        // If bacon number isn't null (path found), compute the bacon path and return with a 200 status code
                        if (baconNumber != null) {
                            String baconPath = neo.computeBaconPath(actorId);
                            sendString(request, baconPath, 200);
                        } // If bacon number is null (no path found), return 404 status code
                        else {
                            sendString(request, "No path to Kevin Bacon", 404);
                        }
                    } else {
                        sendString(request, "Actor not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else if (path.contains("/getCommonMovies")) {
                if (query.contains("actor1Id") && query.contains("actor2Id")) {
                    String actor1Id = queryParam.get("actor1Id");
                    String actor2Id = queryParam.get("actor2Id");
                    // If actors both exist, attempt to find common movies, else return 404 status code
                    if (neo.actorExists(actor1Id) && neo.actorExists(actor2Id)) {
                        // If actors aren't the same, find common movies
                        if (!actor1Id.equals(actor2Id)) {
                            String commonMovies = neo.getCommonMovies(actor1Id, actor2Id);
                            sendString(request, commonMovies, 200);
                        } // If actors are the same, return 400 status code
                        else {
                            sendString(request, "Given actors cannot be the same. Use getActor instead.", 400);
                        }
                    } else {
                        sendString(request, "Actor(s) not found", 404);
                    }
                } else {
                    sendString(request, "Invalid query", 400);
                }
            } else {
                // Return 400 Bad Request for unknown endpoints/invalid requests
                sendString(request, "Unknown endpoint", 400);
            }
            neo.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Return 500 Internal Server Error if an exception is thrown
            sendString(request, "Server error", 500);
        }
    }

    /**
     * This method is used to send a response back to
     * the client based on the success/failure of request's
     * execution.
     * @param request The original request from the client
     * @param data The actual data to send back to the client
     * @param restCode The status code to return
     * @throws IOException When there is an I/O exception
     */
    private void sendString(HttpExchange request, String data, int restCode)
            throws IOException {
        request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
    }
}