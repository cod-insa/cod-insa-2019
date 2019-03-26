package com.codinsa.finale.Util;

import com.codinsa.finale.Model.Board;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

/**
 * Serialise en objet JSON une demande de livraison.
 */
class SerialiseurBoard extends StdSerializer<Board> {

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
     * @param plateau la demande de livraison a serialiser
     * @param jsonGenerator le generateur json utilise
     * @param serializerProvider
     * @throws IOException
     */
    @Override
    public void serialize(Board plateau, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        /*jsonGenerator.writeArrayFieldStart("demandeLivraison");
        jsonGenerator.writeNumber(demandeLivraison.getIdEntrepot());
        for(Livraison livraison : demandeLivraison.getLivraisons()){
            jsonGenerator.writeNumber(livraison.getIdAdresse());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeArrayFieldStart("durees");
        for(Livraison livraison : demandeLivraison.getLivraisons()){
            jsonGenerator.writeNumber(livraison.getDuree());
        }
        jsonGenerator.writeEndArray();*/
        jsonGenerator.writeEndObject();
    }
}