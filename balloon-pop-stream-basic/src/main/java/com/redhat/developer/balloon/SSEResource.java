package com.redhat.developer.balloon;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import java.util.concurrent.CompletionStage;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Channel;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;
import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonReader;
import java.io.StringReader;


@Path("/sse")
public class SSEResource {
    private static final Logger LOG = Logger.getLogger(SSEResource.class);


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @Inject @Channel("popstream") Publisher<String> popstream;
    // @Inject @Channel("popstream") PublisherBuilder<String> popstream;
    @Inject @Channel("bonusstream") Publisher<String> bonusstream;
    
    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> stream() {
      return popstream;
      // .map(item -> Json.createReader(new StringReader(item)).readObject())
      // .peek(s -> System.out.println("Received: " + s))
      // .map(JsonValue::toString)    
      // .buildRs();
    }

    @GET
    @Path("/streamfilter")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> streamfilter() {
      return popstream;
        // .map(item -> Json.createReader(new StringReader(item)).readObject())
        // .filter(json -> json.getString("balloonType").equals("balloon_yellow"))
        // .peek(s -> System.out.println("Received: " + s))
        // .map(JsonValue::toString)        
        // .buildRs();
    }

    @GET
    @Path("/bonusstream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Publisher<String> processed() {
      return bonusstream;
      //  .buildRs();
    }

}