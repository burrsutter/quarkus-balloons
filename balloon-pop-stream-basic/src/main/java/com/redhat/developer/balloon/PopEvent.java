package com.redhat.developer.balloon;

public class PopEvent {

  private String playerId;
  private Integer score;
	private String playerName;
	private Integer consecutive;
	private String balloonType;
  
  // will be used by Kogito/Drools
	// private List<GameBonus> bonuses = new ArrayList<>();

	public PopEvent() {
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Integer getConsecutive() {
		return consecutive;
	}

	public void setConsecutive(Integer consecutive) {
		this.consecutive = consecutive;
	}

  public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getBalloonType() {
		return balloonType;
	}

	public void setBalloonType(String balloonType) {
		this.balloonType = balloonType;
	}
  
  /* will used by Drools
	public void addBonus(Integer bonus, String achievement, String description) {
		this.bonuses.add(new GameBonus(playerId, playerName, achievement, bonus, description));
	}

	public List<GameBonus> getBonuses() {
		return bonuses;
	}

	public void setBonuses(List<GameBonus> bonuses) {
		this.bonuses = bonuses;
	}
  */

	@Override
	public String toString() {
		return "GameEntry [playerId=" + playerId + ", playerName=" + playerName + ", consecutive=" + consecutive
				+ ", balloonType=" + balloonType  + "]";
	}

}
