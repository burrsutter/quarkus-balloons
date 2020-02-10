package com.redhat.developer.balloon;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;


@ApplicationScoped
public class BonusProcessor {

    
  @Incoming("popstream")  
  @Outgoing("bonusstream")
  public Flowable<KafkaMessage<String, GameBonus>> applyBonus(Flowable<KafkaMessage<String, PopEvent>> msg) {
    return msg
      .filter(input -> accept(input))
      .flatMap(input -> calculateGameBonus(input))
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

  private Flowable<KafkaMessage<String, GameBonus>> calculateGameBonus(KafkaMessage<String,PopEvent> msg) {
     
    GameBonus bonus = new GameBonus();  
    Integer consecutive = msg.getPayload().getConsecutive();
    String playerId = msg.getPayload().getPlayerId();
    String playerName = msg.getPayload().getPlayerName();    
    String balloonType = msg.getPayload().getBalloonType();
    int score = msg.getPayload().getScore();

    /*
     The Game client can handle achievements of:
     pops1
     pops2
     pops3
     score1
     score2
     score3
     golden
     
     These badges will appear to the end-user at GameOver
     a particular player session can only receive one of each achievement
     
     https://github.com/burrsutter/quarkus-balloons/blob/master/balloon-game-mobile/src/app/%2Bgame/service/game.service.ts#L293
    */

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
    if (bonus.getAchievement() != null) {
      return Flowable.just(KafkaMessage.of("bonus",bonus));
    } else {
      return Flowable.empty();
    }    
  }
    
}