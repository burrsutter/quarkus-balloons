package com.redhat.developer.balloon.json;

import com.redhat.developer.balloon.GameEntry;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GameEntryJsonbDeserializer extends JsonbDeserializer<GameEntry> {

	public GameEntryJsonbDeserializer() {
		super(GameEntry.class);
	}


}
