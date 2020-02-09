package com.redhat.developer.balloon;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;


@ApplicationScoped
public class BonusProcessor {

  // @Inject @Channel("bonusstream")
  // Emitter<String> bonusstream;
    
  @Incoming("popstream")  
  @Outgoing("bonusstream")
  public Flowable<KafkaMessage<String, GameBonus>> applyBonus(Flowable<KafkaMessage<String, PopEvent>> msg) {
    return msg
      .filter(input -> accept(input))
      .map(input -> calculateGameBonus(input))
      .doOnError(e -> System.out.println(e));
  }  

  /*
  We do not wish to accept every message and send it out as a Bonus
  */
  private boolean accept(KafkaMessage<String, PopEvent> msg) {
    
    Integer consecutive = msg.getPayload().getConsecutive();
    String playerId = msg.getPayload().getPlayerId();
    Integer score = msg.getPayload().getScore();
    String playerName = msg.getPayload().getPlayerName();
    String balloonType = msg.getPayload().getBalloonType();

    System.out.println("POP=consecutive: " + consecutive + " type: " + balloonType + " for " + playerName + " with score: " + score);

    if (consecutive > 3) {
      return true;
    }
    if (balloonType.equals("balloon_golden1") || balloonType.equals("balloon_golden2")) {
      return true;      
    }

    if (score > 1000) {
      return true;
    }

    return false;
  }

  private KafkaMessage<String, GameBonus> calculateGameBonus(KafkaMessage<String,PopEvent> msg) {
     
    GameBonus bonus = new GameBonus();  
    Integer consecutive = msg.getPayload().getConsecutive();
    String playerId = msg.getPayload().getPlayerId();
    String playerName = msg.getPayload().getPlayerName();    
    String balloonType = msg.getPayload().getBalloonType();
    int score = msg.getPayload().getScore();

    if (consecutive >= 3  && consecutive < 5) {
       
      System.out.println("\n** 3-in-a-row Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(100);
      bonus.setAchievement("pops1");
      bonus.setDescription("3 in a Row");  
            
    }  
    if (consecutive >= 5  && consecutive < 9) {
      
      System.out.println("\n** 5-in-a-row Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(200);
      bonus.setAchievement("pops2");
      bonus.setDescription("5 in a Row"); 
         
    }  
    if (consecutive >= 10) {
      
      System.out.println("\n** 10-in-a-row Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(300);
      bonus.setAchievement("pops3");
      bonus.setDescription("10 in a Row");   
       
    } 

    if (score > 1000) {
      
      System.out.println("\n** Score 1000 Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(200);
      bonus.setAchievement("score1");
      bonus.setDescription("You are Awesome");
      
    }

    if (score > 5000) {
      
      System.out.println("\n** Score 5000 Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(500);
      bonus.setAchievement("score2");
      bonus.setDescription("Boss Level");
            
    }
    if (score > 10000) {
      
      System.out.println("\n** Score 10000 Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(1000);
      bonus.setAchievement("score3");
      bonus.setDescription("Sheer Amazement");
      
    }

    if (balloonType.equals("balloon_golden1")) {
      
      System.out.println("\n** Gold Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(500);
      bonus.setAchievement("golden");
      bonus.setDescription("Burr Boss");
            
    } 
    if (balloonType.equals("balloon_golden2")) {
      
      System.out.println("\n** Gold Bonus**\n");            
      bonus.setPlayerId(playerId);
      bonus.setPlayerName(playerName);
      bonus.setBonus(500);
      bonus.setAchievement("golden");
      bonus.setDescription("Ray Boss"); 
      
    }  
   
    return KafkaMessage.of("bonus",bonus);  
  }

/*  
    if(balloonType.equals("balloon_red")) {      
      playerBonusObject = Json.createObjectBuilder()
      .add("playerId", playerId)
      .add("playerName", playerName)
      .add("achievement","score1")
      .add("bonus",10)
      .add("description","Red Bonus").build();  

      // bonusstream.send(playerBonusObject.toString());
    }
*/ 

    
  
  
}