package com.redhat.developer.balloon.json;

import com.redhat.developer.balloon.PopEvent;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PopEventJsonbDeserializer extends JsonbDeserializer<PopEvent> {

	public PopEventJsonbDeserializer() {
		super(PopEvent.class);
	}


}
