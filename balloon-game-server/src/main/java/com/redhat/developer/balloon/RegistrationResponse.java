package com.redhat.developer.balloon;

public class RegistrationResponse {
  private int score;
  private int teamNumber;
  private String playerId;
  private String username;
  private String type;
  private Config configuration;

  public RegistrationResponse(int score, int teamNumber, String playerId, String username, String type,
  Config configuration) {
    this.score = score;
    this.teamNumber = teamNumber;
    this.playerId = playerId;
    this.username = username;
    this.type = type;
    this.configuration = configuration;
  }


  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getTeamNumber() {
    return teamNumber;
  }

  public void setTeamNumber(int teamNumber) {
    this.teamNumber = teamNumber;
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


}