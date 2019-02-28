package com.codinsa.finale.Controler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@EnableAutoConfiguration
public class Endpoints {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private Controler controler;

  @GetMapping("/testMethodCall")
  private boolean getBooleanCall() {
    return controler.getTest();
  }

  @GetMapping("/testParamCall")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
      return test;
  }

}

