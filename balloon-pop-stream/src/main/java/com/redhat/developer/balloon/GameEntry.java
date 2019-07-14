package com.redhat.developer.balloon;

import java.util.ArrayList;
import java.util.List;

public class GameEntry {

	private String playerId;
	private String playerName;
	private Integer consecutive;
	private String balloonType;
	
	private List<GameBonus> bonuses = new ArrayList<>();

	public GameEntry() {
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

	public String getBalloonType() {
		return balloonType;
	}

	public void setBalloonType(String balloonType) {
		this.balloonType = balloonType;
	}
	
	public void addBonus(Integer bonus, String achievement, String description) {
		this.bonuses.add(new GameBonus(playerId, playerName, achievement, bonus, description));
	}

	public List<GameBonus> getBonuses() {
		return bonuses;
	}

	public void setBonuses(List<GameBonus> bonuses) {
		this.bonuses = bonuses;
	}

	@Override
	public String toString() {
		return "GameEntry [playerId=" + playerId + ", playerName=" + playerName + ", consecutive=" + consecutive
				+ ", balloonType=" + balloonType  + "]";
	}

}
