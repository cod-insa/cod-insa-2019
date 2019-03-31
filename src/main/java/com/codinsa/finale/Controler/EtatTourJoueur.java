package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.ActionJson;
import com.codinsa.finale.Model.Board;
import com.codinsa.finale.Model.Node;
import com.codinsa.finale.Model.Serveur;
import com.codinsa.finale.Util.SerialiseurBoard;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class EtatTourJoueur extends EtatDefaut {
    public static int nbTurn = 0;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ArrayList<Boolean> finIA= new ArrayList<>();
    private final int MAX_TURN=200;

    private boolean checkToken(String token, Controler c){
        return verifyToken(token, c) != -1;
    }

    void setFin(int i){
        for(int c=0;c<i;c++){
            finIA.add(false);
        }
    }


    // ->/Start/Turn
    // On réalise l'ensemble des actions demandés par le joueur
    @Override
    public Map<String, Object> doAction(String token, Controler c, List<ActionJson> listT){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            //Protection contre les actions après avoir déclaré la fin du tour
            boolean endExpr=false;
            for (Boolean aBoolean : finIA) {
                endExpr = endExpr || aBoolean;
            }

            if(endExpr){
                if(finIA.get(idJoueur-1)){
                    c.map.put("status","error");
                    c.map.put("error","You have already finish your turn !");
                    log.error("Player "+idJoueur+" try to send actions to do, despite having declared to have finish !");
                    return c.map;
                }

            }

            for (ActionJson t : listT) {
                c.board.move(idJoueur, t.getFrom(), t.getTo(), t.getQtCode());
            }

            c.map.put("status","success");

            return c.map;
        }else{
            return errorToken(token,c);
        }

    }

    // ->/Get/Board
    @Override
    public Map<String, Object> getBoard(String token, Controler c){
        if(checkToken(token,c)){
            c.map.clear();
            c.map.put("status","success");
            c.map.put("object",c.board);
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Get/Visible
    @Override
    public Map<String, Object> getVisible(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            List<Node> nodeList=c.board.getStatusBoard(idJoueur);

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();
            for(Node n:nodeList){
                ObjectNode jsonNode = mapper.createObjectNode();
                jsonNode.put("id",n.getId());
                jsonNode.put("coordX",n.getCoordX());
                jsonNode.put("coordY",n.getCoordY());
                jsonNode.put("production",n.getProduction());
                jsonNode.put("qtCode",n.getQtCode());

                ArrayNode arrayNodeNeighbords = mapper.createArrayNode();
                for(Node neighbords:n.getNeighbors()){
                    ObjectNode jsonNodeBis = mapper.createObjectNode();
                    jsonNodeBis.put("id",neighbords.getId());
                    jsonNodeBis.put("debit",c.board.getDebitLink(neighbords.getId(),n.getId()));
                    arrayNodeNeighbords.add(jsonNodeBis);
                }
                jsonNode.putPOJO("neighbors", arrayNodeNeighbords);
                jsonNode.put("bonus",n.hasBonus());
                jsonNode.put("typeBonus",n.getTypeBonus());
                jsonNode.put("isServer",n instanceof Serveur);
                jsonNode.put("owner",n.getOwner().getIdPlayer());
                arrayNode.add(jsonNode);
            }
            ObjectNode jsonObject = mapper.createObjectNode();
            jsonObject.putPOJO("visible", arrayNode);
            c.map.put("object", jsonObject);

            c.map.put("status","success");
            //c.map.put("object",jsonListe);
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Wait
    // Permet d'attendre durant le delta entre la fin du tour pour le jeu et pour le joueur
    @Override
    public Map<String, Object> doWait(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            c.map.put("status","success");
            //Attente d'un joueur

            boolean waitExpr=true;
            boolean endExpr=true;
            for (int i=1;i<finIA.size();i++) {
                if(i!=idJoueur){
                    //On regarde si les autres on fini de jouer
                    waitExpr = waitExpr && !finIA.get(i-1);
                }
                endExpr=endExpr&&!finIA.get(i-1);
            }

            if(waitExpr){
                c.map.put("wait","true");
            }else if(endExpr){
                c.map.put("wait","true");
            }else{
                c.map.put("wait","false");
            }

            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/End/Turn
    @Override
    public Map<String, Object> endTurn(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            //TODO verify good use
            /*finIA.set(idJoueur-1,true);
            boolean endExpr=true;
            for (Boolean aBoolean : finIA) {
                endExpr = endExpr && aBoolean;
            }
            if(endExpr){
                c.map.put("status", "success");
                if(c.board.endTurn()){*/
                    /*nbTurn++;
                    c.tempsTour.cancel();
                    c.tempsTour.purge();
                    c.tempsTour.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            c.getEtatCourant().endTurnTimer(c);
                        }
                    }, 0,c.etatInitial.TIME_INTERVAL);*/


                    /*c.setEtatCourant(c.etatFin);
                    c.map.put("partyEnd", "success");

                    //On identifie le gagnant
                    int idJoueurGagnant=c.board.getWinner();

                    String tokenJoueur=c.tokenIA.get(idJoueurGagnant-1);
                    String[] infoJoueur=tokenJoueur.split("-");

                    log.info("Le joueur "+infoJoueur[0]+" à remporté la partie !");

                    c.map.put("winner", infoJoueur[0]);
                }else{
                    c.map.put("partyEnd", "false");
                }
            }else{
                c.map.put("status", "success");
                c.map.put("wait", "true");
            }*/
            c.map.put("status", "success");
            c.map.put("wait", "true");
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    @Override
    public boolean endTurnTimer(Controler c){

        for(int i=0;i<finIA.size();i++){
            finIA.set(i,true);
        }
        //wait(500);
        //Victoire
        if(c.board.endTurn()){
            c.setEtatCourant(c.etatFin);
        }

        nbTurn++;
        if(nbTurn>MAX_TURN){
            c.setEtatCourant(c.etatFin);
            int idJoueurGagnant=c.board.getWinner();

            String tokenJoueur=c.tokenIA.get(idJoueurGagnant-1);
            String[] infoJoueur=tokenJoueur.split("-");

            log.info("Le joueur "+infoJoueur[0]+" à remporté la partie !");
        }

        for(int i=0;i<finIA.size();i++){
            finIA.set(i,false);
        }

        return true;
    }

    @Override
    public Map<String, Object> reset(Controler c){
        c.tokenIA.clear();
        c.setEtatCourant(c.etatInitial);
        c.board=null;
        c.tempsTour.cancel();
        c.tempsTour.purge();
        c.map.clear();
        c.map.put("status","success");
        nbTurn = 0;
        return c.map;
    }
    @Override
    public String getState(){
        return "TourJoueur";
    }
    @Override
    public Map<String, Object> getTurn(String token, Controler c){
        c.map.clear();
        c.map.put("turn",nbTurn);
        return c.map;
    }
}
