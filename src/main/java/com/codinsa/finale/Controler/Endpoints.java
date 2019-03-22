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
  protected int i=0;
  @Autowired
  private Controler controler;

  @GetMapping("/testMethodCall")
  private int getBooleanCall() {
    //controler.test();
    i++;
    return i;
  }

  @GetMapping("/testMethodCall2")
  private int getBooleanCall2() {
    //controler.test();
    i--;
    return i;
  }

  /*@GetMapping("/testParamCall")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
      return test;
  }*/

  /*@PostMapping("/IA/Upload")
  private boolean getBooleanParam(
          @RequestParam("token") boolean test
  ) {
    return test;
  }*/

  /*@PostMapping("/IA/Drop")
  private boolean getBooleanParam(
          @RequestParam("token") boolean test
  ) {
    return test;
  }*/

  /*@GetMapping("/Start/Game")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

  /*@GetMapping("/Start/Turn")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

  /*@GetMapping("/Wait")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

  /*@PostMapping("/Action")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

  /*@GetMapping("/Get/com.codinsa.finale.Model.Board")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

  /*@GetMapping("/Get/Turn")
  private boolean getBooleanParam(
          @RequestParam("lol") boolean test
  ) {
    return test;
  }*/

}

