package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Node;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EtatFin extends EtatDefaut {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, String> getBoard(String token, Controler c){
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
            jsonNode.addProperty("bonus",n.hasBonus());
            jsonListe.add(jsonNode);
        }

        JsonObject container=new JsonObject();
        container.add("plateau",jsonListe);
        c.map.put("status","succes");
        c.map.put("object",gson.toJson(container));

        return c.map;
    }

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
