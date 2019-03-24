package com.codinsa.finale.Controler;

import java.util.ArrayList;
import java.util.Map;

public interface Etat {
    boolean verifyToken(String token, Controler c);

    Map<String, String> errorToken(String token, Controler c);

    // ->/IA/Join
    Map<String, String> generateToken(String name, Controler c);

    // ->/Reset
    Map<String, String> reset(Controler c);

    // ->/Start/Game
    Map<String, String> start(String token, Controler c);

    // ->/Start/Turn
    Map<String, String> beginTurn(String token, Controler c);

    // ->/Get/Board
    Map<String, String> getBoard(String token, Controler c);

    // ->/Get/Visible
    Map<String, String> getVisible(String token, Controler c);

    // ->/Wait
    Map<String, String> doWait(String token, Controler c);

    // ->/End/Turn
    Map<String, String> endTurn(String token, Controler c);

    /**
     * @return etat de l'application
     */
    String getState();
}
