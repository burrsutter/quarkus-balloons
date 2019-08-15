package com.redhat.developer.balloon.json;

import com.redhat.developer.balloon.GameBonus;

import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GameBonusJsonbSerializer extends JsonbSerializer<GameBonus> {


}
