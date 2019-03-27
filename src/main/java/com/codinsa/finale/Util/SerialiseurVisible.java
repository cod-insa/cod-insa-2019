package com.codinsa.finale.Util;

import com.codinsa.finale.Model.Board;
import com.codinsa.finale.Model.Node;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

/**
 * Serialise en objet JSON le plateau de jeu
 */
public class SerialiseurVisible extends StdSerializer<List<Node>> {

    /**
     * Constructeur par defaut
     */
    public SerialiseurVisible() {
        this(null);
    }

    /**
     * Constructeur de la classe mere StdSerializer<Board>
     * @param t
     */
    public SerialiseurVisible(Class<List> t) {
        super(t, true);
    }

    /**
     * Sérialise le plateau en parametre.
     * @param nodeList le plateau a serialiser
     * @param jsonGenerator le generateur json utilise
     * @param serializerProvider
     * @throws IOException
     */
    public void serialize(List<Node> nodeList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart("alentours");
        for(Node n : nodeList){
            jsonGenerator.writeStartObject();
            jsonGenerator.writeString(n.toJsonStringForVisible());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}