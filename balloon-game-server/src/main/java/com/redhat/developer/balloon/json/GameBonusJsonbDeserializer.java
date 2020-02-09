package com.redhat.developer.balloon.json;

import com.redhat.developer.balloon.GameBonus;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GameBonusJsonbDeserializer extends JsonbDeserializer<GameBonus> {
	public GameBonusJsonbDeserializer() {
		super(GameBonus.class);
	}

}
