package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.ActionJson;
import com.codinsa.finale.Model.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Etat {
    int verifyToken(String token, Controler c);

    Map<String, Object> errorToken(String token, Controler c);

    // ->/IA/Join
    Map<String, Object> generateToken(String name, Controler c);

    // ->/Reset
    Map<String, Object> reset(Controler c);

    // ->/Start/ChooseMap
    Map<String, Object> setMap(String nameMap, Controler c);

    // ->/Start/Game
    Map<String, Object> start(String token, Controler c);

    // ->/Start/Turn
    Map<String, Object> doAction(String token, Controler c, List<ActionJson> listT);

    // ->/Get/Board
    Map<String, Object> getBoard(String token, Controler c);

    // ->/Get/Visible
    Map<String, Object> getVisible(String token, Controler c);

    // ->/Wait
    Map<String, Object> doWait(String token, Controler c);

    // ->/End/Turn
    Map<String, Object> endTurn(String token, Controler c);


    boolean endTurnTimer(Controler c);

    /**
     * @return etat de l'application
     */
    String getState();
}
