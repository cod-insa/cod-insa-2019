package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

public class EtatTourJ1 extends EtatDefaut {

    final Logger log = LoggerFactory.getLogger(getClass());

    private boolean checkToken(String token, Controler c){
        if(verifyToken(token,c)){
            return c.tokenIA.get(0).equals(token);
        }else{
            return false;
        }
    }

    // ->/Start/Turn
    @Override
    public Map<String, String> beginTurn(String token, Controler c){

        if(checkToken(token,c)){
            //TODO
            return c.map;
        }else{
            return errorToken(token,c);
        }

    }

    // ->/Get/Board
    @Override
    public Map<String, String> getBoard(String token, Controler c){
        if(checkToken(token,c)){
            //TODO
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Get/Visible
    @Override
    public Map<String, String> getVisible(String token, Controler c){
        if(checkToken(token,c)){
            //TODO
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Wait
    @Override
    public Map<String, String> doWait(String token, Controler c){
        if(checkToken(token,c)){
            c.map.clear();
            c.map.put("status","succes");
            c.map.put("wait","false");
            return c.map;
        }else{
            c.map.clear();
            c.map.put("status","succes");
            c.map.put("wait","true");
            return c.map;
        }
    }

    // ->/End/Turn
    @Override
    public Map<String, String> endTurn(String token, Controler c, ArrayList<Transaction> listT){
        if(checkToken(token,c)) {
            for (Transaction t : listT) {
                c.board.move(1, t.getFrom().getId(), t.getTo().getId(), t.getQtCode());
            }
            c.board.endTurn();
            //TODO check victory
            c.map.put("status", "succes");
            c.setEtatCourant(c.etatTourJ2);
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    @Override
    public String getState(){
        return "TourJ1";
    }
}
