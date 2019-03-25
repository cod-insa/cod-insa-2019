package com.codinsa.finale.Controler;

import jdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@EnableAutoConfiguration
public class Endpoints {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Controler controler;

    @PostMapping("/IA/Join")
    Map<String, String> getToken(
            @RequestParam("IAName") String name
    ) {
        return controler.generateToken(name);
    }

    @GetMapping("/Reset")
    Map<String, String> resetGame(
    ) {
        return controler.resetGame();
    }

    @GetMapping("/Start/Game")
    Map<String, String> start(
            @RequestParam("Token") String token
    ) {
        return controler.startGame(token);
    }

    /*@GetMapping("/Start/Turn")
    Map<String, String> beginTurn(
            @RequestParam("Token") String token
    ) {
        return controler.doTurn(token);
    }

    @GetMapping("/Get/Board")
    Map<String, String> getBoard(
            @RequestParam("Token") String token
    ) {
        return controler.getBoard(token);
    }

    @GetMapping("/Get/Visible")
    Map<String, String> getVisible(
            @RequestParam("Token") String token
    ) {
        return controler.getVisible(token);
    }

    @GetMapping("/Wait")
    Map<String, String> doWait(
            @RequestParam("Token") String token
    ) {
        return controler.doWait(token);
    }

    @PostMapping("/End/Turn")
    Map<String, String> endTurn(
            @RequestParam("Token") String token,
            @RequestParam("Action") String action
    ) {
        return controler.endTurn(token,action);
    }*/

}

