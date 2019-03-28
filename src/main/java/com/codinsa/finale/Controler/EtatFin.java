package com.codinsa.finale.Controler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EtatFin extends EtatDefaut {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, String> getBoard(String token, Controler c){
        //TODO Return txt file
        return c.map;}

    @Override
    public Map<String, String> reset(Controler c){
        c.tokenIA.clear();
        c.setEtatCourant(c.etatInitial);
        c.tempsTour.cancel();
        c.tempsTour.purge();
        c.map.clear();
        c.map.put("status","succes");
        return c.map;
    }

    @Override
    public String getState(){
        return "Fin";
    }
}
