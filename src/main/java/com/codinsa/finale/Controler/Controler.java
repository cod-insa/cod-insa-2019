package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Board;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class Controler {

    Etat    etatCourant;
    EtatInitial     etatInitial = new EtatInitial();    // etatCourant initial du controleur
    EtatTourJ1      etatTourJ1 = new EtatTourJ1();     // tour de J1
    EtatTourJ2      etatTourJ2 = new EtatTourJ2();      // tour de J2
    EtatFin         etatFin = new EtatFin();            // fin du jeu

    ArrayList<String> tokenIA= new ArrayList<>();
    HashMap<String, String> map = new HashMap<>();

    public Controler() {
        etatCourant = etatInitial;
    }

    Etat getEtatCourant() {
        return etatCourant;
    }

    void setEtatCourant(Etat etatCourant) {
        this.etatCourant = etatCourant;
    }

    public Map<String, String> generateToken(String name){
        return etatCourant.generateToken(name,this);
    }

    public Map<String, String> resetGame(){
        return etatCourant.reset(this);
    }

    public Map<String, String> startGame(String name){
        return etatCourant.start(name,this);
    }

    public void test(){
        Board b = new Board("map0.txt");
        //testEndTurn(b);
        testTransServeur(b);
    }

    public void testTransaction( Board b)
    {
        b.move(1,0,1,2);
    }
    public void testEndTurn(Board b)
    {
        b.statusNodes();
        b.move(1,0,1,2);
        b.move(2,2,1,2);
        b.endTurn();
        b.statusNodes();
    }
    public void testTransServeur(Board b)
    {
        b.statusNodes();
        b.move(1,0,1,5);
        b.endTurn();
        b.statusNodes();
        b.endTurn();
        b.statusNodes();
    }
}