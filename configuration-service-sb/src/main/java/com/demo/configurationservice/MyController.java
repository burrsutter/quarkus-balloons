package com.demo.configurationservice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path="/config", produces=MediaType.APPLICATION_JSON_VALUE)
public class MyController {

  @GetMapping
  @ResponseBody
  public Config config() {
      Points points = new Points(
          1, 1, 1, 1, 1, 100
      );

      Config myconfig = new Config(
        "ray", 
        "green", 
        100,
        "1.1",
        85, 
        50,
        false, 
        true, 
        points
      );

      return myconfig;
  }

} 