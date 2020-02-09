package com.redhat.developer.balloon;

import java.io.StringReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Endpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

@ServerEndpoint("/game") // for the mobile game
@Path("/a") // for the administration
@ApplicationScoped
public class GameEndpoint {
  private static final Logger LOG = Logger.getLogger(Endpoint.class);
  private static final String STARTGAME = "start-game";
  private static final String PLAY = "play";
  private static final String PAUSE = "pause";
  private static final String GAMEOVER = "game-over";

  @Inject
  @RestClient
  ConfigurationService configurationService;

  // this value should be reset at "startgame" but hardcoding for now
  private String currentGameId = "ab7e90e1-18a6-94d1-05bb-1e17a9cc8dad";

  // websocket client sessions/connections
  Map<String, Session> sessions = new ConcurrentHashMap<>();

  // playerMap
  Map<String, Session> playerSessions = new ConcurrentHashMap<>();

  @ConfigProperty(name="kafkaforpops")
  boolean kafkaforpops;
  
  // send the player's pops to Kafka topic
  @Inject @Stream("popstream")
  Emitter<String> popstream;

  String prevPolledResponse = "";
  int pollCnt = 0;
  
  Points defaultPoints = new Points(
    1, 1, 1, 1, 50, 50
  );

  Config currentGame = new Config(
    currentGameId, 
    "default", // background
    100, // ignore
    "0.6", // size
    85, // opacity
    70, // speed
    false, // snitch1
    false, // snitch2
    defaultPoints
  );    

  JsonObject pointsConfiguration = Json.createObjectBuilder()
  .add("red",1)
  .add("yellow",1)
  .add("green",1)
  .add("blue",1)
  .add("goldenSnitch1",50)
  .add("goldenSnitch2",50)
  .build();


  JsonObject defaultGameConfiguration = Json.createObjectBuilder()
  .add("gameId", currentGameId)
  .add("background","default")
  .add("trafficPercentage",100)
  .add("scale","0.9")
  .add("opacity",85)
  .add("speed",50)
  .add("goldenSnitch1", Boolean.FALSE)
  .add("goldenSnitch2", Boolean.FALSE)
  .add("points",pointsConfiguration).build();

  // current can be overwritten at runtime
  JsonObject currentGameConfiguration = defaultGameConfiguration;

  JsonObject startGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state",STARTGAME).build();

  JsonObject playGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state",PLAY).build();

  JsonObject pauseGameMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state",PAUSE).build();

  JsonObject gameOverMsg = Json.createObjectBuilder()
  .add("type","state")
  .add("state",GAMEOVER).build();

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

  @OnMessage // from client to server
  public void onMessage(String message, Session session) { 
      if (message == null) return;

      JsonReader jsonReader = Json.createReader(new StringReader(message));
      JsonObject jsonMessage = jsonReader.readObject();      
      // LOG.info("jsonMessage: " + jsonMessage);
      String msgtype = jsonMessage.getString("type");
      // LOG.info("msgtype: " + msgtype);

      if (msgtype != null && !msgtype.equals("")) {
        if(msgtype.equals("register")) {
          registerClient(jsonMessage, session);
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
  public void registerClient(JsonObject jsonMessage, Session session) {
    // right now, always create a new playerId during registration    
    String playerId = UUID.randomUUID().toString();
    // and assigns a new generated user name
    String username = UserNameGenerator.generate();
    // and assigns a random team number
    int teamNumber = ThreadLocalRandom.current().nextInt(1, 5);
    LOG.info("\n\nCreating:");
    LOG.info("username: " + username);
    LOG.info("playerId: " + playerId);  
    LOG.info("teamNumber: " + teamNumber);
     
    playerSessions.putIfAbsent(playerId,session);

    /* 
    client needs 3 messages initially: id, configuration, game state     
    */

    JsonObject idResponse = Json.createObjectBuilder()
    .add("type","id")
    .add("id",playerId).build();

    LOG.info("idResponse: " + idResponse.toString());

    sendOnePlayer(playerId,idResponse.toString());

    RegistrationResponse configurationResponse = new RegistrationResponse(
      0, // initial score 
      teamNumber, 
      playerId, 
      username, 
      "configuration", // type, 
      currentGame);

    Jsonb jsonb = JsonbBuilder.create();
    String stringConfigurationResponse = jsonb.toJson(configurationResponse);

    sendOnePlayer(playerId,stringConfigurationResponse);
    
    LOG.info("\n GAME-STATE: " + currentGameState + "\n");

    if(currentGameState.equals(STARTGAME)) {
      sendOnePlayer(playerId,startGameMsg.toString());
    } else if(currentGameState.equals(PLAY)) {
      sendOnePlayer(playerId,playGameMsg.toString());
    } else if(currentGameState.equals(PAUSE)) {
      sendOnePlayer(playerId,pauseGameMsg.toString());
    } else if(currentGameState.equals(GAMEOVER)) {
      sendOnePlayer(playerId,gameOverMsg.toString());
    }

  } // registerClient

  /*
   With each balloon pop, send it to Kafka for async analysis
  */
  public void score(JsonObject jsonMessage) {    
    if (kafkaforpops) {
      // LOG.info("POP: " + jsonMessage.toString());    
      popstream.send(jsonMessage.toString());
    }
  }

  /* 
  Listen for bonus topic, bonuses should sent out the unique player 
  */
  @Incoming("bonusstream")
  public CompletionStage<Void> process(KafkaMessage<String,GameBonus> msg) {
  // public void process(String message) {
      
      
      LOG.info("\n!!!BONUSSTREAM!!! " + msg.getPayload().toString());


      String achievement = msg.getPayload().getAchievement();

      if (achievement != null && !achievement.trim().equals("")) {
        String playerId = msg.getPayload().getPlayerId();
        String description = msg.getPayload().getDescription();
        int bonus = msg.getPayload().getBonus();
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
        // In theory, the server could queue the achievements per player and send as one
        // the client is prepared to receive several achievements in a batch

        sendOnePlayer(playerId, allAcheivements.toString());
        
      }
      
     return msg.ack();
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
  @Path("/gamestate")
  @Produces(MediaType.APPLICATION_JSON)
  public Response gamestate(){
    return Response.ok(currentGameState).status(200).build();
  }

  @GET
  @Path("/config")
  @Produces(MediaType.APPLICATION_JSON)
  public Response config(){
    return Response.ok(currentGame).status(200).build();
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
    // There can be multiple achievements so send them all to all players
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
  @Path("/easy") 
  @Produces(MediaType.APPLICATION_JSON)
  public Response easyconfig(){

    Points easyPoints = new Points(
      4, 2, 3, 1, 100, 100
    );
  
    Config easyGame = new Config(
      currentGameId, 
      "default", // background
      100, // ignore
      "1.0", // size
      85, // opacity
      35, // speed
      false, // snitch1
      false, // snitch2
      easyPoints
    );    
    
    currentGame = easyGame;

    sendGameConfigUpdate(currentGame);

    return Response.ok(currentGame).status(200).build();
  }
  @GET
  @Path("/hard")
  @Produces(MediaType.APPLICATION_JSON)
  public Response hardconfig(){
    Points hardPoints = new Points(
      4, 2, 3, 1, 100, 100
    );
  
    Config hardGame = new Config(
      currentGameId, 
      "default", // background
      100, // ignore
      ".3", // size
      50, // opacity
      95, // speed
      false, // snitch1
      false, // snitch2
      hardPoints
    );    
    
    currentGame = hardGame;

    sendGameConfigUpdate(currentGame);

    return Response.ok(currentGame).status(200).build();
  }
  

  @GET
  @Path("/burr") 
  @Produces(MediaType.APPLICATION_JSON)
  public Response goldenSnitch1() {
    
    currentGame.setGoldenSnitch1(Boolean.TRUE);
    currentGame.setGoldenSnitch2(Boolean.FALSE);
    currentGame.setBackground("green");

    sendGameConfigUpdate(currentGame);

    return Response.ok(currentGame).status(200).build();
  }

  @GET
  @Path("/ray")
  @Produces(MediaType.APPLICATION_JSON)
  public Response goldenSnitch2() {
    currentGame.setGoldenSnitch1(Boolean.FALSE);
    currentGame.setGoldenSnitch2(Boolean.TRUE);
    currentGame.setBackground("blue");

    sendGameConfigUpdate(currentGame);

    return Response.ok(currentGame).status(200).build();
  }  

  @GET
  @Path("/default")
  @Produces(MediaType.APPLICATION_JSON)
  public Response defaultConfig() {
    
    // resetting to the default configuration
    prevPolledResponse = "";
    
    Points defaultPoints = new Points(
      1, 1, 1, 1, 50, 50
    );
  
    Config defaultGame = new Config(
      currentGameId, 
      "default", // background
      100, // ignore
      "0.6", // size
      85, // opacity
      70, // speed
      false, // snitch1
      false, // snitch2
      defaultPoints
    );    
    
    currentGame = defaultGame;

    sendGameConfigUpdate(currentGame);

    return Response.ok(currentGame).status(200).build();
  }

  private void sendGameConfigUpdate(Config newGameConfig) {
    Jsonb jsonb = JsonbBuilder.create();

    ConfigMsgType type = new ConfigMsgType("configuration",newGameConfig);
    String stringJsonMsgType = jsonb.toJson(type);
    
    System.out.println(stringJsonMsgType);
    // broadcast out the websocket to all players
    broadcast(stringJsonMsgType);
  }

  @Scheduled(every="2s")
  void pollConfig() {
    // System.out.println("every 2 sec");
    try {
      String response = configurationService.getConfig().trim();
      // response is a string of JSON
      // System.out.println("\n *** response");
      // System.out.println(response);
      
      // only alert clients if the config has changed since previous poll
      if (!response.equals(prevPolledResponse)) {        
        
        System.out.println("\n *** new response " + pollCnt++ + ":");
        prevPolledResponse = response;
        
        Config convertedConfig = convertResponseStringToConfig(response);  
        sendGameConfigUpdate(convertedConfig);
      }

    } catch (Exception ex) {
      System.out.println("Ignoring: " + ex);
    }
  }

  private Config convertResponseStringToConfig(String response) {
      
      Config convertedConfig = new Config();
      
      JsonReader jsonReader = Json.createReader(new StringReader(response));
      JsonObject jsonObjectConfig = jsonReader.readObject();  
      convertedConfig.setBackground(jsonObjectConfig.getString("background"));
      convertedConfig.setGameId(jsonObjectConfig.getString("gameId"));
      convertedConfig.setGoldenSnitch1(jsonObjectConfig.getBoolean("goldenSnitch1"));
      convertedConfig.setGoldenSnitch2(jsonObjectConfig.getBoolean("goldenSnitch2"));
      convertedConfig.setOpacity(jsonObjectConfig.getInt("opacity"));
      convertedConfig.setScale(jsonObjectConfig.getString("scale"));
      convertedConfig.setSpeed(jsonObjectConfig.getInt("speed"));
      convertedConfig.setTrafficPercentage(jsonObjectConfig.getInt("trafficPercentage"));
      
      Points convertedPoints = new Points(
        jsonObjectConfig.getJsonObject("points").getInt("red"),
        jsonObjectConfig.getJsonObject("points").getInt("yellow"),
        jsonObjectConfig.getJsonObject("points").getInt("green"),
        jsonObjectConfig.getJsonObject("points").getInt("blue"),
        jsonObjectConfig.getJsonObject("points").getInt("goldenSnitch1"),
        jsonObjectConfig.getJsonObject("points").getInt("goldenSnitch2")
      );

      convertedConfig.setPoints(convertedPoints);

      
      return convertedConfig;
  }
}