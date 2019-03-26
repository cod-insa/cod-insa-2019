package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Board;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Component
public class Controler {

    Etat    etatCourant;
    EtatInitial     etatInitial = new EtatInitial();    // etatCourant initial du controleur
    EtatTourJoueur etatTourJ1 = new EtatTourJoueur();     // tour de jeu
    EtatFin         etatFin = new EtatFin();            // fin du jeu

    ArrayList<String> tokenIA= new ArrayList<>();
    HashMap<String, String> map = new HashMap<>();

    Board board;
    Timer tempsTour=new Timer();

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
        b.move(1,0,1,15);
        b.endTurn();
        b.statusNodes();
        b.move(1,1,2,15);
        System.out.println(b.endTurn());
        b.statusNodes();
    }
}