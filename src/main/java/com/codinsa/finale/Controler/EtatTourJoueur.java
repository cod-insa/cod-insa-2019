package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.ActionJson;
import com.codinsa.finale.Model.Node;
import com.codinsa.finale.Model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public class EtatTourJoueur extends EtatDefaut {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private boolean J1Fin=false;
    private boolean J2Fin=false;

    private boolean checkToken(String token, Controler c){
        return verifyToken(token, c) != -1;
    }

    // ->/Start/Turn
    // On réalise l'ensemble des actions demandés par le joueur
    @Override
    public Map<String, String> doAction(String token, Controler c, List<ActionJson> listT){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            //Protection contre les actions après avoir déclaré la fin du tour
            if(J1Fin||J2Fin){
                if(J1Fin&&idJoueur==1){
                    c.map.put("status","error");
                    c.map.put("error","You have already finish your turn !");
                    log.error("Player 1 try to send actions to do, despite having declared to have finish !");
                    return c.map;
                }
                if(J2Fin&&idJoueur==2){
                    c.map.put("status","error");
                    c.map.put("error","You have already finish your turn !");
                    log.error("Player 2 try to send actions to do, despite having declared to have finish !");
                    return c.map;
                }
            }

            for (ActionJson t : listT) {
                c.board.move(idJoueur, t.getFrom(), t.getTo(), t.getQtCode());
            }
            c.map.put("status","succes");
            return c.map;
        }else{
            return errorToken(token,c);
        }

    }

    // ->/Get/Board
    @Override
    public Map<String, String> getBoard(String token, Controler c){
        if(checkToken(token,c)){
            c.map.clear();
            List<Node> nodeList=c.board.getGraph();
            Gson gson= new GsonBuilder().setPrettyPrinting().create();
            JsonArray jsonListe=new JsonArray();

            for(Node n:nodeList){
                JsonObject jsonNode=new JsonObject();
                jsonNode.addProperty("id",n.getId());
                jsonNode.addProperty("coordX",n.getCoordX());
                jsonNode.addProperty("coordY",n.getCoordY());
                jsonNode.addProperty("production",n.getProduction());
                jsonNode.addProperty("qtCode",n.getQtCode());
                jsonNode.addProperty("neighbors",n.getNeighbors().size());
                jsonListe.add(jsonNode);
            }

            JsonObject container=new JsonObject();
            container.add("plateau",jsonListe);
            c.map.put("status","succes");
            c.map.put("object",gson.toJson(container));

            return c.map;

            /*ObjectMapper mapper = new ObjectMapper();
            SimpleModule module =
                    new SimpleModule("CustomBoardSerializer", new Version(1, 0, 0, null, null, null));
            module.addSerializer(Board.class, new SerialiseurBoard());
            mapper.registerModule(module);
            try{
                String plateauJson = mapper.writeValueAsString(c.board);
                c.map.put("status","succes");
                c.map.put("object",plateauJson);
                return c.map;
            }catch(IOException e){
                c.map.put("status","error");
                c.map.put("error","Serialisation of board has encountered a problem !");
                log.error("Serialisation of board has encountered a problem !");
                return c.map;
            }*/
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Get/Visible
    @Override
    public Map<String, String> getVisible(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            List<Node> nodeList=c.board.getStatusBoard(idJoueur);
            Gson gson= new GsonBuilder().setPrettyPrinting().create();
            JsonArray jsonListe=new JsonArray();

            for(Node n:nodeList){
                JsonObject jsonNode=new JsonObject();
                jsonNode.addProperty("id",n.getId());
                jsonNode.addProperty("coordX",n.getCoordX());
                jsonNode.addProperty("coordY",n.getCoordY());
                jsonNode.addProperty("production",n.getProduction());
                jsonNode.addProperty("qtCode",n.getQtCode());
                jsonNode.addProperty("neighbors",n.getNeighbors().size());
                jsonNode.addProperty("owner",n.getOwner().getIdPlayer());
                jsonListe.add(jsonNode);
            }

            JsonObject container=new JsonObject();
            container.add("visible",jsonListe);
            c.map.put("status","succes");
            c.map.put("object",gson.toJson(container));
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    // ->/Wait
    // Permet d'attendre durant le delta entre la fin du tour pour le jeu et pour le joueur
    @Override
    public Map<String, String> doWait(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            c.map.clear();
            c.map.put("status","succes");
            //Attente d'un joueur
            if((idJoueur==1&&!J2Fin)||(idJoueur==2&&!J1Fin)){
                c.map.put("wait","true");
            }
            //Attente du timer ?
            //TODO check if we need use of this
            else if(!J2Fin&&!J1Fin){
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
    public Map<String, String> endTurn(String token, Controler c){
        int idJoueur=verifyToken(token,c);
        if(idJoueur!=-1){
            //TODO verify good use
            if(idJoueur==1){
                J1Fin=true;
            }
            if(idJoueur==2){
                J2Fin=true;
            }
            if(J1Fin&&J2Fin){
                J1Fin=false;
                J2Fin=false;
                c.map.put("status", "succes");
                if(c.board.endTurn()){
                    c.setEtatCourant(c.etatFin);
                    c.map.put("partyEnd", "succes");
                }else{
                    c.map.put("partyEnd", "false");
                }
            }else{
                c.map.put("status", "succes");
                c.map.put("wait", "true");
            }
            return c.map;
        }else{
            return errorToken(token,c);
        }
    }

    @Override
    public boolean endTurnTimer(Controler c){
        J1Fin=true;
        J2Fin=true;
        //wait(500);
        //Victoire
        if(c.board.endTurn()){
            c.setEtatCourant(c.etatFin);
        }
        J1Fin=false;
        J2Fin=false;
        return true;
    }

    @Override
    public String getState(){
        return "TourJoueur";
    }
}
