*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup    Create Session    localhost    http://localhost:8080

*** Test Cases ***
# Initialization
initializeActor1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon    actorId=nm0000102
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

initializeMovie1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Apollo 13    movieId=mv0000194
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

initializeRelationship1
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102    movieId=mv0000194
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeActor2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Tom Hanks    actorId=nm0000142
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

initializeRelationship2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000142    movieId=mv0000194
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeMovie2
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=The Matrix    movieId=mv0000168
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

initializeRelationship3
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000142    movieId=mv0000168
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeActor3
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Keanu Reeves     actorId=nm0000139
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

initializeRelationship4
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000139    movieId=mv0000168
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeActor4
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Chris Evans    actorId=nm0000114
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

initializeRelationship5
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000114    movieId=mv0000168
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeMovie3
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=The Avengers    movieId=mv0000169
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

initializeRelationship6
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000139    movieId=mv0000169
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

initializeRelationship7
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000114    movieId=mv0000169
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

# Actual Test Cases
addActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Tobey Maguire    actorId=nm0000176
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=200

addActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Tom Holland
    ${resp}=    PUT On Session    localhost    /api/v1/addActor    json=${params}    headers=${headers}    expected_status=400

addMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Spiderman    movieId=mv0000159
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=200

addMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Spiderman 2    movieId=mv0000159
    ${resp}=    PUT On Session    localhost    /api/v1/addMovie    json=${params}    headers=${headers}    expected_status=400

addRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000176    movieId=mv0000159
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=200

addRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102    movieId=mv0000001
    ${resp}=    PUT On Session    localhost    /api/v1/addRelationship    json=${params}    headers=${headers}    expected_status=404

getActorPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=200
    # Checking for actorId
    Dictionary Should Contain Key    ${resp.json()}    actorId
    ${actorId} =      Get From Dictionary       ${resp.json()}    actorId
    Should Be Equal As Strings      ${actorId}        nm0000102
    # Checking for name
    Dictionary Should Contain Key    ${resp.json()}    name
    ${name} =      Get From Dictionary       ${resp.json()}    name
    Should Be Equal As Strings      ${name}        Kevin Bacon
    # Checking for movies list
    Dictionary Should Contain Key    ${resp.json()}    movies
    ${movies} =      Get From Dictionary       ${resp.json()}    movies
    List Should Contain Value    ${movies}    mv0000194

getActorFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actor=nm0000176
    ${resp}=    GET On Session    localhost    /api/v1/getActor    params=${params}    headers=${headers}    expected_status=400

getMoviePass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=mv0000194
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=200
    # Checking for movieId
    Dictionary Should Contain Key    ${resp.json()}    movieId
    ${movieId} =      Get From Dictionary       ${resp.json()}    movieId
    Should Be Equal As Strings      ${movieId}        mv0000194
    # Checking for name
    Dictionary Should Contain Key    ${resp.json()}    name
    ${name} =      Get From Dictionary       ${resp.json()}    name
    Should Be Equal As Strings      ${name}        Apollo 13
    # Checking for actors list
    Dictionary Should Contain Key    ${resp.json()}    actors
    ${actors} =      Get From Dictionary       ${resp.json()}    actors
    List Should Contain Value    ${actors}    nm0000102

getMovieFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=mv0000001
    ${resp}=    GET On Session    localhost    /api/v1/getMovie    params=${params}    headers=${headers}    expected_status=404

hasRelationshipPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102    movieId=mv0000194
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=200
    # Checking for actorId
    Dictionary Should Contain Key    ${resp.json()}    actorId
    ${actorId} =      Get From Dictionary       ${resp.json()}    actorId
    Should Be Equal As Strings      ${actorId}        nm0000102
    # Checking for movieId
    Dictionary Should Contain Key    ${resp.json()}    movieId
    ${movieId} =      Get From Dictionary       ${resp.json()}    movieId
    Should Be Equal As Strings      ${movieId}        mv0000194
    # Checking for hasRelationship boolean
    Dictionary Should Contain Key    ${resp.json()}    hasRelationship
    ${hasRelationship} =      Get From Dictionary       ${resp.json()}    hasRelationship
    Should Be Equal As Strings      ${hasRelationship}        ${true}

hasRelationshipFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102
    ${resp}=    GET On Session    localhost    /api/v1/hasRelationship    params=${params}    headers=${headers}    expected_status=400

computeBaconNumberPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000139
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=200
    # Checking for baconNumber boolean
    Dictionary Should Contain Key    ${resp.json()}    baconNumber
    ${baconNumber} =      Get From Dictionary       ${resp.json()}    baconNumber
    Should Be Equal As Strings      ${baconNumber}        ${2}

computeBaconNumberFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000176
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconNumber    params=${params}    headers=${headers}    expected_status=404

computeBaconPathPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000114
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=200
    # Checking for baconPath list
    Dictionary Should Contain Key    ${resp.json()}    baconPath
    ${baconPath} =      Get From Dictionary       ${resp.json()}    baconPath
    List Should Contain Value    ${baconPath}    nm0000114
    List Should Contain Value    ${baconPath}    mv0000168
    List Should Contain Value    ${baconPath}    nm0000142
    List Should Contain Value    ${baconPath}    mv0000194
    List Should Contain Value    ${baconPath}    nm0000102

computeBaconPathFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actor=nm0000114
    ${resp}=    GET On Session    localhost    /api/v1/computeBaconPath    params=${params}    headers=${headers}    expected_status=400

getCommonMoviesPass
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actor1Id=nm0000139   actor2Id=nm0000114
    ${resp}=    GET On Session    localhost    /api/v1/getCommonMovies    params=${params}    headers=${headers}    expected_status=200
    # Checking for commonMovies list
    Dictionary Should Contain Key    ${resp.json()}    commonMovies
    ${commonMovies} =      Get From Dictionary       ${resp.json()}    commonMovies
    List Should Contain Value    ${commonMovies}    mv0000168
    List Should Contain Value    ${commonMovies}    mv0000169

getCommonMoviesFail
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actor1Id=nm0000142   actor2Id=nm0000142
    ${resp}=    GET On Session    localhost    /api/v1/getCommonMovies    params=${params}    headers=${headers}    expected_status=400