package com.redhat.developer.balloon;

public class RegistrationResponse {
  private int score;
  private int team;
  private String playerId;
  private String username;
  private String type;
  private Config configuration;
  private String locationKey;

  public RegistrationResponse(int score, int team, String playerId, String username, String type,
  Config configuration, String locationKey) {
    this.score = score;
    this.team = team;
    this.playerId = playerId;
    this.username = username;
    this.type = type;
    this.configuration = configuration;
    this.locationKey = locationKey;
  }


  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getTeam() {
    return team;
  }

  public void setTeam(int team) {
    this.team = team;
  }

  public String getPlayerId() {
    return playerId;
  }

  public void setPlayerId(String playerId) {
    this.playerId = playerId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Config getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Config configuration) {
    this.configuration = configuration;
  }

  public String getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(String locationKey) {
    this.locationKey = locationKey;
  }

}