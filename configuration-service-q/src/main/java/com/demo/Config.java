package com.demo;

public class Config {
   private String gameId;
   private String background;
   private int trafficPercentage;
   private String scale;
   private int opacity;
   private int speed;
   private boolean goldenSnitch1;
   private boolean goldenSnitch2;
   private Points points;

  public Config(
      String gameId, 
      String background, 
      int trafficPercentage, 
      String scale, 
      int opacity, 
      int speed,
      boolean goldenSnitch1, boolean goldenSnitch2, Points points) {
    this.gameId = gameId;
    this.background = background;
    this.trafficPercentage = trafficPercentage;
    this.scale = scale;
    this.opacity = opacity;
    this.speed = speed;
    this.goldenSnitch1 = goldenSnitch1;
    this.goldenSnitch2 = goldenSnitch2;
    this.points = points;
  }

  public String getGameId() {
    return gameId;
  }

  public String getBackground() {
    return background;
  }

  public int getTrafficPercentage() {
    return trafficPercentage;
  }

  public String getScale() {
    return scale;
  }

  public int getOpacity() {
    return opacity;
  }

  public int getSpeed() {
    return speed;
  }

  public boolean isGoldenSnitch1() {
    return goldenSnitch1;
  }

  public boolean isGoldenSnitch2() {
    return goldenSnitch2;
  }

  public Points getPoints() {
    return points;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public void setTrafficPercentage(int trafficPercentage) {
    this.trafficPercentage = trafficPercentage;
  }

  public void setScale(String scale) {
    this.scale = scale;
  }

  public void setOpacity(int opacity) {
    this.opacity = opacity;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public void setGoldenSnitch1(boolean goldenSnitch1) {
    this.goldenSnitch1 = goldenSnitch1;
  }

  public void setGoldenSnitch2(boolean goldenSnitch2) {
    this.goldenSnitch2 = goldenSnitch2;
  }

  public void setPoints(Points points) {
    this.points = points;
  }
   
}

/*

{
	"gameId": "d6d5dc35-c4cd-4299-a171-9092bb4ab645",
	"background": "canary",
	"trafficPercentage": 100,
	"scale": ".9",
	"opacity": 85,
	"speed": 35,
	"goldenSnitch1": true,
	"goldenSnitch2": true,
	"points": {
		"red": 1,
		"yellow": 1,
		"green": 1,
		"blue": 1,
		"goldenSnitch1": 100,
		"goldenSnitch2": 100
	}
}
- gameId: 
- background: can be default, blue, green, canary
- trafficPercentage: is ignored
- scale: is the size of the balloon
- opacity: is the opacity of the balloon
- speed: higher numbers (e.g. 95) might throw the balloons off the top of the screen, 
lower numbers (e.g. 20) mean the balloons barely make it halfway up the screen
- goldenSnitch1: the Burr balloon in play
  (based on balloon-game-mobile/src/app/+game/assets/balloons_v2.png)
- goldenSnitch2: the Ray balloon in play
- points per pop - the client maintains its own state of pops, achievements, 
calculated by the server may augment the client's score
*/
