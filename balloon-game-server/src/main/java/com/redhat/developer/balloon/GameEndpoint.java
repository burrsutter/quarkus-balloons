package com.redhat.developer.balloon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.io.StringReader;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonReader;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.concurrent.CompletionStage;


@ServerEndpoint("/game") // for the mobile game
@Path("/142sjer43") // for the administration
@ApplicationScoped
public class GameEndpoint {
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
  Map<String, Session> playerSessions = new ConcurrentHashMap<>();

  @Inject @Stream("popstream")
  Emitter<String> popstream;

  JsonObject pointsConfiguration = Json.createObjectBuilder()
  .add("red",1)
  .add("yellow",1)
  .add("green",1)
  .add("blue",1)
  .add("goldenSnitch1",50)
  .add("goldenSnitch2",50)
  .build();

  JsonObject currentGameConfiguration = Json.createObjectBuilder()
  .add("gameId", currentGameId)
  .add("background","default")
  .add("trafficPercentage",100)
  .add("scale","0.9")
  .add("opacity",85)
  .add("speed",50)
  .add("goldenSnitch1", Boolean.FALSE)
  .add("goldenSnitch2", Boolean.FALSE)
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
      if (message == null) return;

      JsonReader jsonReader = Json.createReader(new StringReader(message));
      JsonObject jsonMessage = jsonReader.readObject();      
      // LOG.info("jsonMessage: " + jsonMessage);
      String msgtype = jsonMessage.getString("type");
      // LOG.info("msgtype: " + msgtype);
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
  } // broadcast

  // send a message to a single client
  public void sendOnePlayer(String id, String message) {
    Session oneSession = playerSessions.get(id);
    oneSession.getAsyncRemote().sendObject(message, result ->  {
      if (result.getException() != null) {
          LOG.error("Unable to send message: " + result.getException());
      }
    });
  } // sendOnePlayer

  /*
    Mobile/Client/Game requests
  */
  public void register(JsonObject jsonMessage, Session session) {
    // right now, always create a new playerId during registration    
    String playerId = UUID.randomUUID().toString();
    String username = UserNameGenerator.generate();
    int teamNumber = ThreadLocalRandom.current().nextInt(1, 5);
    LOG.info("playerId: " + playerId);
    LOG.info("username: " + username);
    LOG.info("teamNumber: " + teamNumber);
    // JsonObject playerObject = Json.createObjectBuilder()
    // .add("playerId", playerId)
    // .add("username", username)
    // .add("teamNumber", teamNumber).build();
    
    playerSessions.putIfAbsent(playerId,session);

    /* 
    client needs 3 messages: id, configuration, game state     
    */

    JsonObject idResponse = Json.createObjectBuilder()
    .add("type","id")
    .add("id",playerId).build();

    LOG.info("idResponse: " + idResponse.toString());

    sendOnePlayer(playerId,idResponse.toString());

    JsonObject configurationResponse = Json.createObjectBuilder()
    .add("score",0)
    .add("team",teamNumber)
    .add("playerId",playerId)
    .add("username", username)
    .add("type","configuration")
    .add("configuration",currentGameConfiguration).build();

    sendOnePlayer(playerId,configurationResponse.toString());
    
    if(currentGameState.equals(STARTGAME)) {
      sendOnePlayer(playerId,startGameMsg.toString());
    } else if(currentGameState.equals(PLAY)) {
      sendOnePlayer(playerId,playGameMsg.toString());
    } else if(currentGameState.equals(PAUSE)) {
      sendOnePlayer(playerId,pauseGameMsg.toString());
    } else if(currentGameState.equals(GAMEOVER)) {
      sendOnePlayer(playerId,gameOverMsg.toString());
    }

  }

  /*
   With each balloon pop
  */
  public void score(JsonObject jsonMessage) {
    LOG.info("POP: " + jsonMessage.toString());    
    popstream.send(jsonMessage.toString());
  }

  /* 
  Listen for bonus  
  */
  @Incoming("bonusstream")
  // public CompletionStage<Void> process(KafkaMessage<String,String> msg) {
    public void process(String message) {
      
      String msg = message;
      LOG.info("\n!!!BONUSSTREAM!!! " + msg);

      JsonReader jsonReader = Json.createReader(new StringReader(msg));
      JsonObject jsonMessage = jsonReader.readObject();   
      String achievement = jsonMessage.getString("achievement");
      String playerId = jsonMessage.getString("playerId");
      String description = jsonMessage.getString("description");
      int bonus = jsonMessage.getInt("bonus");

      if (achievement != null && !achievement.trim().equals("")) {
        LOG.info("!!! Achievement !!! " + achievement + " for: " + playerId + " value: " + bonus);
        JsonArray achievements = Json.createArrayBuilder()
          .add(Json.createObjectBuilder()
          .add("type",achievement)
          .add("description",description)
          .add("bonus",bonus)).build();
    
        JsonObject allAcheivements = Json.createObjectBuilder()
          .add("type","achievements")
          .add("achievements",achievements)
          .build();
        
        LOG.info(allAcheivements.toString());

        sendOnePlayer(playerId, allAcheivements.toString());
        
      }
      // return msg.ack();
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
  @Path("/sessions")
  public Response sessions(){
    StringBuffer sb = new StringBuffer();
    sessions.values().forEach(session -> {
      sb.append(session.getId() + "<br>");
    });  
    return Response.ok(sb.toString()).status(200).build();
  }

  @GET
  @Path("/playersessions")
  public Response playersessions(){
    StringBuffer sb = new StringBuffer();    
    playerSessions.keySet().forEach(playerId -> {
      sb.append(playerSessions.get(playerId).getId() + " : " + playerId + "<br>");
    });  
    return Response.ok(sb.toString()).status(200).build();
  }

  @GET
  @Path("/achieve")
  @Produces(MediaType.APPLICATION_JSON)
  public Response achievement(){
    // There can be multiple achievements so send them all, per player
    JsonArray achievements = Json.createArrayBuilder()
    .add(Json.createObjectBuilder()
      .add("type","pops1")
      .add("description","1 in a row!")
      .add("bonus",10))
    .add(Json.createObjectBuilder()
      .add("type","pops2")
      .add("description","2 in a row!")
      .add("bonus",20))
    .add(Json.createObjectBuilder()
      .add("type","pops3")
      .add("description","3 in a row!")
      .add("bonus",30))
    .add(Json.createObjectBuilder()
      .add("type","score1")
      .add("description","Level 1")
      .add("bonus",100))
    .add(Json.createObjectBuilder()
      .add("type","score2")
      .add("description","Level 2")
      .add("bonus",200))
    .add(Json.createObjectBuilder()
      .add("type","score3")
      .add("description","Level 3")
      .add("bonus",300))
    .add(Json.createObjectBuilder()
      .add("type","golden")
      .add("description","Solid Gold")
      .add("bonus",1000))

    .build();


    JsonObject allAcheivements = Json.createObjectBuilder()
    .add("type","achievements")
    .add("achievements",achievements)
    .build();

    // for testing purposes, sending all players
    broadcast(allAcheivements.toString());
    
    return Response.ok(allAcheivements).status(200).build();
  }

  @GET
  @Path("/easyconfig")
  @Produces(MediaType.APPLICATION_JSON)
  public Response easyconfig(){
    JsonObject newpointsConfiguration = Json.createObjectBuilder()
    .add("red",1)
    .add("yellow",1)
    .add("green",1)
    .add("blue",1)
    .add("goldenSnitch1",100)
    .add("goldenSnitch2",100)
    .build();

    JsonObject newcurrentGameConfiguration = Json.createObjectBuilder()
    .add("gameId", currentGameId)
    .add("background","canary")
    .add("trafficPercentage",100)
    .add("scale",".9")
    .add("opacity",85)
    .add("speed",35)
    .add("goldenSnitch1", Boolean.TRUE)
    .add("goldenSnitch2", Boolean.TRUE)
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
    .add("goldenSnitch1",10)
    .add("goldenSnitch2",10)
    .build();

    JsonObject newcurrentGameConfiguration = Json.createObjectBuilder()
    .add("gameId", currentGameId)
    .add("background","blue")
    .add("trafficPercentage",100)
    .add("scale","0.4")
    .add("opacity",85)
    .add("speed",95)
    .add("goldenSnitch1", Boolean.FALSE)
    .add("goldenSnitch2", Boolean.FALSE)
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