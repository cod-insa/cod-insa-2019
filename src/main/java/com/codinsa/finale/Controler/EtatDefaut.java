package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class EtatDefaut implements Etat {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean verifyToken(String token, Controler c){
        for (String s:c.tokenIA){
            if(s.equals(token)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, String> errorToken(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You need a valid token to do this action !");
        log.error("A bad token was use to call an end point : "+token);
        return c.map;
    }

    @Override
    public Map<String, String> generateToken(String name, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't generate a token in game !");
        log.error("You can't generate a token in game !");
        return c.map;
    }

    @Override
    public Map<String, String> reset(Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't reset the game now !");
        log.error("You can't reset the game now !");
        return c.map;
    }

    @Override
    public Map<String, String> start(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't restart the game !");
        log.error("You can't restart the game !");
        return c.map;
    }

    @Override
    public Map<String, String> beginTurn(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't start your turn, you have to wait !");
        log.error("You can't start your turn, you have to wait !");
        return c.map;}

    @Override
    public Map<String, String> getBoard(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't get the board, you have to wait !");
        log.error("You can't get the board, you have to wait !");
        return c.map;}

    @Override
    public Map<String, String> getVisible(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You can't get the wisible element, you have to wait !");
        log.error("You can't get the wisible element, you have to wait !");
        return c.map;}

    @Override
    public Map<String, String> doWait(String token, Controler c){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You must finish your turn first or start the game !");
        log.error("You must finish your turn first or start the game !");
        return c.map;}

    @Override
    public Map<String, String> endTurn(String token, Controler c, ArrayList<Transaction> listT){
        c.map.clear();
        c.map.put("status","error");
        c.map.put("error","You must start your turn first or start the game !");
        log.error("You must start your turn first or start the game !");
        return c.map;
    }

    @Override
    public String getState(){
        return "Defaut";
    }
}
