package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Node;
import com.codinsa.finale.Model.Serveur;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtatFin extends EtatDefaut {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, Object> getBoard(String token, Controler c){
        c.map.clear();
        List<Node> nodeList=c.board.getGraph();

        int idJoueurGagnant=c.board.getWinner();
        String tokenJoueur=c.tokenIA.get(idJoueurGagnant-1);
        String[] infoJoueur=tokenJoueur.split("-");

        c.map.put("winner", infoJoueur[0]);
        c.map.put("idWinner", ""+idJoueurGagnant);
/*
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
            jsonNode.addProperty("bonus",n.hasBonus());
            jsonNode.addProperty("typeBonus",n.getTypeBonus());
            jsonNode.addProperty("isServer",n instanceof Serveur);
            jsonNode.addProperty("owner",n.getOwner().getIdPlayer());
            jsonListe.add(jsonNode);
        }

        JsonObject container=new JsonObject();
        container.add("plateau",jsonListe);
        c.map.put("object",container);*/
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
                jsonNodeBis.put("id",n.getId());
                arrayNodeNeighbords.add(jsonNodeBis);
            }

            jsonNode.put("bonus",n.hasBonus());
            jsonNode.put("typeBonus",n.getTypeBonus());
            jsonNode.put("isServer",n instanceof Serveur);
            jsonNode.put("owner",n.getOwner().getIdPlayer());
            arrayNode.add(jsonNode);
        }
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.putPOJO("visible", arrayNode);
        c.map.put("object", jsonObject);

        c.map.put("status","succes");

        return c.map;
    }

    @Override
    public Map<String, Object> reset(Controler c){
        c.tokenIA.clear();
        c.setEtatCourant(c.etatInitial);
        c.board=null;
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
