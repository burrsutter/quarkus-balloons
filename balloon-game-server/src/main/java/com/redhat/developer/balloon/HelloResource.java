package com.redhat.developer.balloon;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.math.BigDecimal;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

@Path("/test")
public class HelloResource {

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/stuff")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject someJson() {
      JsonObject myobject = Json.createObjectBuilder()
      .add("name", "Burr")
      .add("age", BigDecimal.valueOf(3))
      .add("biteable", Boolean.FALSE).build();
      // return myobject.toString();
      return myobject;
    }
}