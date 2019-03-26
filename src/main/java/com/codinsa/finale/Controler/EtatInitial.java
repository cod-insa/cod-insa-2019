package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EtatInitial extends EtatDefaut {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, String> generateToken(String name, Controler c){
        c.map.clear();
        if(c.tokenIA.size()>=2){
            c.map.put("status","error");
            c.map.put("error","All token are already used ! Reset game if you want one !");
            log.error("All token are already used ! Reset game if you want one !");
            return c.map;
        }

        int idJoueur=1;
        if(c.tokenIA.size()>0) {
            idJoueur = 2;
        }

        for(String s:c.tokenIA){
            if(s.substring(0,name.length()).equals(name)){
                c.map.put("status","error");
                c.map.put("error","This name is taken !");
                log.error(name+" - This name is taken !");
                return c.map;
            }
        }

        String keyspace = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+~`:?><,.=";
        String s = "";
        int max = keyspace.length() - 1;
        do {
            for (int i = 0; i < 40; ++i) {
                s += keyspace.charAt((int)(Math.random()*(max+1)));
            }
            if(c.tokenIA.contains(s)){
                s="";
            }
        }while(s.isEmpty());
        s=name+"-"+s;

        c.tokenIA.add(s);
        c.map.put("status","sucess");
        c.map.put("id",""+idJoueur);
        c.map.put("token",s);
        return c.map;
    }

    @Override
    public Map<String, String> reset(Controler c) {
        c.map.clear();
        c.tokenIA.clear();
        c.map.put("status","sucess");
        return c.map;
    }

    @Override
    public Map<String, String> start(String token, Controler c){
        if(verifyToken(token,c)==-1){
            return errorToken(token,c);
        }
        c.map.clear();
        c.board= new Board("map0.txt");
        c.setEtatCourant(c.etatTourJ1);
        c.map.put("status","sucess");
        return c.map;
    }

    @Override
    public String getState(){
        return "Initial";
    }
}
