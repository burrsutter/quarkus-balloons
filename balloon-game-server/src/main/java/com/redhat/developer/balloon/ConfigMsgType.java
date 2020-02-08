package com.redhat.developer.balloon;

public class ConfigMsgType {
  private String type;
  private Config configuration;

  public ConfigMsgType(String type, Config config) {
    this.type = type;
    this.configuration = config;
    // configuration, achievements, state
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Config getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Config configuration) {
    this.configuration = configuration;
  }
  

}