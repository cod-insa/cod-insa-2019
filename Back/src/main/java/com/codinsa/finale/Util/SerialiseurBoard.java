package com.codinsa.finale.Util;

import com.codinsa.finale.Model.Board;
import com.codinsa.finale.Model.Node;
import com.codinsa.finale.Model.Serveur;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

/**
 * Serialise en objet JSON le plateau de jeu
 */
public class SerialiseurBoard extends StdSerializer<Board> {

    /**
     * Constructeur par defaut
     */
    public SerialiseurBoard() {
        this(null);
    }

    /**
     * Constructeur de la classe mere StdSerializer<Board>
     * @param t
     */
    public SerialiseurBoard(Class<List> t) {
        super(t, true);
    }

    /**
     * Sérialise le plateau en parametre.
     * @param plateau le plateau a serialiser
     * @param jsonGenerator le generateur json utilise
     * @param serializerProvider
     * @throws IOException
     */
    @Override
    public void serialize(Board plateau, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart("plateau");
        List<Node> graphe=plateau.getGraph();
        for(Node n : graphe){
            jsonGenerator.writeStartObject();

            //jsonGenerator.writeRaw(n.toJsonStringForBoard());
            jsonGenerator.writeNumberField("id",n.getId());
            jsonGenerator.writeNumberField("coordX",n.getCoordX());
            jsonGenerator.writeNumberField("coordY",n.getCoordY());
            jsonGenerator.writeNumberField("production",n.getProduction());
            jsonGenerator.writeArrayFieldStart("neighbors");
            for(Node neighbords : n.getNeighbors()){
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("id",neighbords.getId());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeBooleanField("bonus",n.hasBonus());
            jsonGenerator.writeNumberField("typeBonus",n.getTypeBonus());
            jsonGenerator.writeBooleanField("isServer",n instanceof Serveur);

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}