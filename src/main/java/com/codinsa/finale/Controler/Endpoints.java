package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.ActionJson;
import com.codinsa.finale.Model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@EnableAutoConfiguration
public class Endpoints {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private Controler controler;

    //Spring se lance sur localhost:8080
    @PostMapping("/TestSetUp")
    Map<String, String> test() {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        //controler.test();
        return m;
    }

    @PostMapping("/IA/Join")
    Map<String, Object> getToken(
            @RequestParam("IAName") String name
    ) {
        return controler.generateToken(name);
    }

    @GetMapping("/Reset")
    Map<String, Object> resetGame(
    ) {
        return controler.resetGame();
    }

    @GetMapping("/Start/Game")
    Map<String, Object> start() {
        return controler.startGame();
    }

    @GetMapping("/Start/ChooseMap")
    Map<String, Object> changeMap(
            @RequestParam("Map") String map
    ) {
        return controler.setMap(map);
    }

    @PostMapping("/PlayAction")
    Map<String, Object> beginTurn(
            @RequestParam("Token") String token, @RequestBody List<ActionJson> jsonAction
    ) {
        return controler.doAction(token,jsonAction);
    }

    @GetMapping("/Get/Board")
    Map<String, Object> getBoard(
            @RequestParam("Token") String token
    ) {
        return controler.getBoard(token);
    }

    @GetMapping("/Get/Visible")
    Map<String, Object> getVisible(
            @RequestParam("Token") String token
    ) {
        return controler.getVisible(token);
    }

    @GetMapping("/Wait")
    Map<String, Object> doWait(
            @RequestParam("Token") String token
    ) {
        return controler.doWait(token);
    }

    @GetMapping("/Get/Turn")
    Map<String, Object> getTurn(
            @RequestParam("Token") String token
    ) {
        return controler.getTurn(token);
    }

    @PostMapping("/End/Turn")
    Map<String, Object> endTurn(
            @RequestParam("Token") String token
    ) {
        return controler.endTurn(token);
    }

}

