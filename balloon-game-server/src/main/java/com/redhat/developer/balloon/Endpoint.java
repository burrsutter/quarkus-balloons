package com.redhat.developer.balloon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.json.Json;
import javax.json.JsonObject;
// import javax.json.JsonValue;
import javax.json.JsonReader;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;


@ServerEndpoint("/game") // for the mobile game
@Path("/142sjer43") // for the administration
@ApplicationScoped
public class Endpoint {
  private static final Logger LOG = Logger.getLogger(Endpoint.class);
  private static final String STARTGAME = "start-game";
  private static final String PLAY = "play";
  private static final String PAUSE = "pause";
  private static final String GAMEOVER = "game-over";

  // this value should be reset at "startgame"
  private String currentGameId = "ab7e90e1-18a6-94d1-05bb-1e17a9cc8dad";

  // client sessions/connections
  Map<String, Session> sessions = new ConcurrentHashMap<>();

  // playerMap
  Map<String, JsonObject> players = new ConcurrentHashMap<>();

  @Inject @Stream("popstream")
  Emitter<String> emitter;

  JsonObject pointsConfiguration = Json.createObjectBuilder()
  .add("red",1)
  .add("yellow",1)
  .add("green",1)
  .add("blue",1)
  .add("goldenSnitch",50).build();

  JsonObject currentGameConfiguration = Json.createObjectBuilder()
  .add("gameId", currentGameId)
  .add("background","default")
  .add("trafficPercentage",100)
  .add("scale","0.9")
  .add("opacity",85)
  .add("speed",50)
  .add("goldenSnitch", Boolean.FALSE)
  .add("points",pointsConfiguration).build();

  JsonObject startGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state",STARTGAME).build();

  JsonObject playGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state","play").build();

  JsonObject pauseGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state","pause").build();

  JsonObject gameOverMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state","game-over").build();

  String currentGameState = STARTGAME;

  @OnOpen
  public void onOpen(Session session) {
    LOG.info("onOpen");
    LOG.info("id: " + session.getId());    
    sessions.put(session.getId(),session);
    
  }

  @OnClose
  public void onClose(Session session) {
      LOG.info("onClose");
      sessions.remove(session.getId());      
  }

  @OnError
  public void onError(Session session, Throwable throwable) {        
      LOG.error("onError", throwable);        
  }

  @OnMessage
  public void onMessage(String message, Session session) {      
      JsonReader jsonReader = Json.createReader(new StringReader(message));
      JsonObject jsonMessage = jsonReader.readObject();      
      LOG.info("jsonMessage: " + jsonMessage);
      String msgtype = jsonMessage.getString("type");
      LOG.info("msgtype: " + msgtype);

      if (msgtype != null && !msgtype.equals("")) {
        if(msgtype.equals("register")) {
          register(jsonMessage, session);
        } else if(msgtype.equals("score")) {
          score(jsonMessage);
        }
      }

  } // OnMessage
  
  // broadcast a message to all connected clients
  public void broadcast(String message) {
    sessions.values().forEach(session -> {
      session.getAsyncRemote().sendObject(message, result ->  {
          if (result.getException() != null) {
              LOG.error("Unable to send message: " + result.getException());
          }
      });
    });
    // send the message twice, helps with some clients being semi-connected
    sessions.values().forEach(session -> {
      session.getAsyncRemote().sendObject(message, result ->  {
          if (result.getException() != null) {
              LOG.error("Unable to send message: " + result.getException());
          }
      });
    });

  } // broadcast

  // send a message to a single client
  public void send(String id, String message) {
    Session oneSession = sessions.get(id);
    oneSession.getAsyncRemote().sendObject(message, result ->  {
      if (result.getException() != null) {
          LOG.error("Unable to send message: " + result.getException());
      }
    });
  } // send

  /*
    Mobile/Client/Game requests
  */
  public void register(JsonObject jsonMessage, Session session) {
    // right now, always create a new Player during registration
    // note: this should change as reconnects/refreshes are common and players will wish to maintain their state
    String playerId = UUID.randomUUID().toString();
    String username = UserNameGenerator.generate();
    int teamNumber = ThreadLocalRandom.current().nextInt(1, 5);
    LOG.info("playerId: " + playerId);
    LOG.info("username: " + username);
    LOG.info("teamNumber: " + teamNumber);
    JsonObject playerObject = Json.createObjectBuilder()
    .add("playerId", playerId)
    .add("username", username)
    .add("teamNumber", teamNumber).build();
    players.put(playerId,playerObject);

    /* 
    client needs 3 messages: id, configuration, game state     
    */

    JsonObject idResponse = Json.createObjectBuilder()
    .add("type","id")
    .add("id",playerId).build();

    LOG.info("idResponse: " + idResponse.toString());

    send(session.getId(),idResponse.toString());

    JsonObject configurationResponse = Json.createObjectBuilder()
    .add("score",0)
    .add("team",teamNumber)
    .add("playerId",playerId)
    .add("username", username)
    .add("type","configuration")
    .add("configuration",currentGameConfiguration).build();

    send(session.getId(),configurationResponse.toString());
    
    if(currentGameState.equals(STARTGAME)) {
      send(session.getId(),startGameMsg.toString());
    } else if(currentGameState.equals(PLAY)) {
      send(session.getId(),playGameMsg.toString());
    } else if(currentGameState.equals(PAUSE)) {
      send(session.getId(),pauseGameMsg.toString());
    } else if(currentGameState.equals(GAMEOVER)) {
      send(session.getId(),gameOverMsg.toString());
    }

  }

  /*
   With each balloon pop
  */
  public void score(JsonObject jsonMessage) {
    LOG.info("POP: " + jsonMessage.getString("balloonType"));
    emitter.send(jsonMessage.toString());
  }

  /*
   Admin Endpoints
  */
  @GET 
  @Path("/start")
  public Response startGame() {
    LOG.info("start a new game");
    currentGameId=UUID.randomUUID().toString();
    currentGameState=STARTGAME;
    broadcast(startGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/play")
  public Response playGame() {
    LOG.info(PLAY);    
    currentGameState=PLAY;
    broadcast(playGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/pause")
  public Response pauseGame() {
    LOG.info(PAUSE);
    currentGameState=PAUSE;
    broadcast(pauseGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/gameover")
  public Response gameOver() {
    LOG.info(GAMEOVER);
    currentGameState=GAMEOVER;
    broadcast(gameOverMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/config")
  @Produces(MediaType.APPLICATION_JSON)
  public Response config(){
    return Response.ok(currentGameConfiguration).status(200).build();
  }

  @GET
  @Path("/easyconfig")
  @Produces(MediaType.APPLICATION_JSON)
  public Response easyconfig(){
    JsonObject newpointsConfiguration = Json.createObjectBuilder()
    .add("red",10)
    .add("yellow",20)
    .add("green",30)
    .add("blue",40)
    .add("goldenSnitch",100).build();

    JsonObject newcurrentGameConfiguration = Json.createObjectBuilder()
    .add("gameId", currentGameId)
    .add("background","canary")
    .add("trafficPercentage",100)
    .add("scale","1.9")
    .add("opacity",85)
    .add("speed",20)
    .add("goldenSnitch", Boolean.TRUE)
    .add("points",newpointsConfiguration).build();

    currentGameConfiguration = newcurrentGameConfiguration;
    LOG.info(currentGameConfiguration);

    JsonObject newConfigResponse = Json.createObjectBuilder()
    .add("type","configuration")
    .add("configuration",currentGameConfiguration).build();

    broadcast(newConfigResponse.toString());

    return Response.ok(currentGameConfiguration).status(200).build();
  }
  @GET
  @Path("/hardconfig")
  @Produces(MediaType.APPLICATION_JSON)
  public Response hardconfig(){
    JsonObject newpointsConfiguration = Json.createObjectBuilder()
    .add("red",1)
    .add("yellow",2)
    .add("green",3)
    .add("blue",4)
    .add("goldenSnitch",10).build();

    JsonObject newcurrentGameConfiguration = Json.createObjectBuilder()
    .add("gameId", currentGameId)
    .add("background","blue")
    .add("trafficPercentage",100)
    .add("scale","0.4")
    .add("opacity",85)
    .add("speed",95)
    .add("goldenSnitch", Boolean.FALSE)
    .add("points",newpointsConfiguration).build();

    currentGameConfiguration = newcurrentGameConfiguration;
    LOG.info(currentGameConfiguration);

    JsonObject newConfigResponse = Json.createObjectBuilder()
    .add("type","configuration")
    .add("configuration",currentGameConfiguration).build();

    broadcast(newConfigResponse.toString());

    return Response.ok(currentGameConfiguration).status(200).build();
  }


}