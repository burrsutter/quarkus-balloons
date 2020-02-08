package com.redhat.developer.balloon;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RegisterRestClient
public interface ConfigurationService {
  @Path("/config")
  @GET
  @Produces("application/json")
  public String getConfig();

}