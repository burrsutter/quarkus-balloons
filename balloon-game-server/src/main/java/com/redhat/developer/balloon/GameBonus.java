package com.redhat.developer.balloon;

public class GameBonus {

	private String playerId;
	private String playerName;

	private String achievement;
	private Integer bonus;
	private String description;

	public GameBonus() {
	}

	public GameBonus(String playerId, String playerName, String achievement, Integer bonus, String description) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.achievement = achievement;
		this.bonus = bonus;
		this.description = description;
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
	
	public String getAchievement() {
		return achievement;
	}

	public void setAchievement(String achievement) {
		this.achievement = achievement;
	}
	
	public void addAchievement(String achievement) {
		if (this.achievement == null) {
			this.achievement = "";
		}
		this.achievement += achievement;
	}

	public Integer getBonus() {
		return bonus;
	}

	public void setBonus(Integer bonus) {
		this.bonus = bonus;
	}
	
	public void addBonus(Integer bonus) {
		if (this.bonus == null) {
			this.bonus = new Integer(0);
		}
		this.bonus += bonus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "GameBonus [playerId=" + playerId + ", playerName=" + playerName + ", achievement=" + achievement + ", bonus=" + bonus
				+ ", description=" + description + "]";
	}

}
