package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EtatInitial extends EtatDefaut {
    //Temps pour le timer en millisecondes
    private final Long TIME_INTERVAL=1000L;
    private final String CHEMIN_RESSOURCE="src/main/resources/";

    private final Logger log = LoggerFactory.getLogger(getClass());
    private int MAX_JOUEUR=2;
    private List<String> nameMapKnown= new ArrayList<>(Arrays.asList("map0", "map1", "map2"));
    private List<Integer> playerMapKnown= new ArrayList<>(Arrays.asList(2, 3, 4));
    private String mapName=nameMapKnown.get(0);

    @Override
    public Map<String, Object> generateToken(String name, Controler c){
        c.map.clear();
        if(c.tokenIA.size()>=MAX_JOUEUR){
            c.map.put("status","error");
            c.map.put("error","All token are already used ! Reset game if you want one !");
            log.error("All token are already used ! Reset game if you want one !");
            return c.map;
        }

        int idJoueur=1;
        if(c.tokenIA.size()>0) {
            idJoueur = c.tokenIA.size()+1;
        }

        for(String s:c.tokenIA){
            if(s.substring(0,name.length()).equals(name)){
                c.map.put("status","error");
                c.map.put("error","This name is taken !");
                log.error(name+" - This name is taken !");
                return c.map;
            }
        }

        String keyspace = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
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
        c.map.put("status","success");
        c.map.put("id",""+idJoueur);
        c.map.put("token",s);

        return c.map;
    }

    @Override
    public Map<String, Object> setMap(String nameMap, Controler c){
        int i;
        boolean find=false;
        for (i=0;i<nameMapKnown.size();i++){
            if(nameMapKnown.get(i).equals(nameMap)){
                find=true;
                MAX_JOUEUR=playerMapKnown.get(i);
                //Si des malins essaient de trafiquer le nombre de joueur
                if(c.tokenIA.size()>MAX_JOUEUR){
                    for(int v=c.tokenIA.size();v>MAX_JOUEUR;v--){
                        c.tokenIA.remove(v);
                    }
                }
                break;
            }
        }
        c.map.clear();
        if(find){
            c.map.put("status","success");
        }else{
            c.map.put("status","error");
            c.map.put("error","You can't load an non-existing board !");
            log.error("You can't load an non-existing board !");
        }
        return c.map;
    }

    @Override
    public Map<String, Object> reset(Controler c) {
        c.map.clear();
        c.tokenIA.clear();
        c.map.put("status","success");
        return c.map;
    }

    @Override
    public Map<String, Object> start(Controler c){
        /*if(verifyToken(token,c)==-1){
            return errorToken(token,c);
        }*/
        c.map.clear();
        if(c.tokenIA.size()<MAX_JOUEUR){
            c.map.put("status","error");
            c.map.put("error","You can't play alone to the game, you need "+(MAX_JOUEUR-c.tokenIA.size())+" more players !");
            log.error("You can't play alone to the game, you need "+(MAX_JOUEUR-c.tokenIA.size())+" more players !");
            return c.map;
        }
        try{
            //String path = this.getClass().getClassLoader().getResource("map0.txt").toExternalForm();
            c.board= new Board(CHEMIN_RESSOURCE+mapName+".txt");
            c.etatTourJ1.setFin(c.tokenIA.size());
            c.setEtatCourant(c.etatTourJ1);
            c.map.put("status","success");
            /*c.tempsTour.schedule(new TimerTask() {

                @Override
                public void run() {
                    c.getEtatCourant().endTurnTimer(c);
                }
            }, 0,TIME_INTERVAL);*/

            return c.map;
        }catch (Exception e){
            c.map.put("status","error");
            c.map.put("message",e.getMessage());
            log.error("The map was not found !");
            return c.map;
        }
    }

    @Override
    public Map<String, Object> doWait(String token, Controler c){
        c.map.clear();
        c.map.put("status","success");
        c.map.put("wait","false");
        return c.map;
    }

    @Override
    public String getState(){
        return "Initial";
    }
}
