package com.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/config")
public class MyResource {

    Points points = new Points(
      1, 1, 1, 1, 50, 50
    );

    Config myconfig = new Config(
      "default", 
      "default", 
      100,
      "0.6",
      70, 
      85,
      false, 
      false, 
      points
    );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Config config() {                
      return myconfig;

    }
}