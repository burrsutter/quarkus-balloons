package com.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/config")
public class MyResource {

    Points points = new Points(
      1, 1, 1, 1, 100, 1
    );

    Config myconfig = new Config(
      "burr", 
      "green", 
      100,
      "1.1",
      85, 
      50,
      true, 
      false, 
      points
    );

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Config config() {                
      return myconfig;

    }
}