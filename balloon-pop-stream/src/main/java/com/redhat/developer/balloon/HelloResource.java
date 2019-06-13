package com.redhat.developer.balloon;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.concurrent.CompletionStage;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Stream;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;
import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonReader;
import java.io.StringReader;


@Path("/hello")
public class HelloResource {
    private static final Logger LOG = Logger.getLogger(HelloResource.class);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    /*
    @Incoming("popstream")
    public CompletionStage<Void> process(KafkaMessage<String,String> msg) {        
        LOG.info(msg.getPayload());  
        return msg.ack();
    }
    */
    @Inject @Stream("popstream") PublisherBuilder<String> stream;

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> stream() {
      return stream.buildRs();
    }

    @GET
    @Path("/streamfilter")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> streamfilter() {
      return stream
        .map(item -> Json.createReader(new StringReader(item)).readObject())
        .filter(json -> json.getString("balloonType").equals("balloon_yellow"))
        .map(JsonValue::toString)
        .buildRs();
    }

}