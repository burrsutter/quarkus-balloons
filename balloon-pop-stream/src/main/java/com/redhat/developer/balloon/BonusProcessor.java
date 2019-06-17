package com.redhat.developer.balloon;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.reactive.messaging.annotations.Broadcast;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonReader;
import java.io.StringReader;


import javax.inject.Inject;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.concurrent.CompletionStage;


@ApplicationScoped
public class BonusProcessor {

  @Inject @Stream("bonusstream")
  Emitter<String> bonusstream;
    
  @Incoming("popstream")  
  public CompletionStage<Void> applyBonus(KafkaMessage<String,String> msg) {
    JsonReader jsonReader = Json.createReader(new StringReader(msg.getPayload()));
    JsonObject jsonMessage = jsonReader.readObject();   
    
    String playerId = jsonMessage.getString("playerId");
    String playerName = jsonMessage.getString("playerName");
    Integer consecutive = jsonMessage.getInt("consecutive");
    String balloonType = jsonMessage.getString("balloonType");

    System.out.println("* Evaluating consecutive: " + consecutive + " type: " + balloonType);
    JsonObject playerBonusObject = null;
    

    if (consecutive >= 3  && consecutive < 10) {
      System.out.println("** 3-in-a-row Bonus");
      playerBonusObject = Json.createObjectBuilder()
      .add("playerId", playerId)
      .add("playerName", playerName)
      .add("achievement","pops1")
      .add("bonus",100)
      .add("description","3 in a Row").build();  

      bonusstream.send(playerBonusObject.toString());
    } 
/*
    if (consecutive >= 10  && consecutive < 15) {
      System.out.println("** 10-in-a-row Bonus");
      playerBonusObject = Json.createObjectBuilder()
      .add("playerId", playerId)
      .add("playerName", playerName)
      .add("achievement","pops2")
      .add("bonus",200)
      .add("description","10 in a Row").build();  

      bonusstream.send(playerBonusObject.toString());
    } 

    if (consecutive >= 15) {
      System.out.println("** 15-in-a-row Bonus");
      playerBonusObject = Json.createObjectBuilder()
      .add("playerId", playerId)
      .add("playerName", playerName)
      .add("achievement","pops3")
      .add("bonus",300)
      .add("description","15 in a Row").build();  

      bonusstream.send(playerBonusObject.toString());
    } 

*/    
    if(balloonType.equals("balloon_red")) {      
      playerBonusObject = Json.createObjectBuilder()
      .add("playerId", playerId)
      .add("playerName", playerName)
      .add("achievement","score1")
      .add("bonus",10)
      .add("description","Red Bonus").build();  

      bonusstream.send(playerBonusObject.toString());
    }
  
    return msg.ack(); 
  }
  
}