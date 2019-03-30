package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.ActionJson;
import com.codinsa.finale.Model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Controler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Etat    etatCourant;
    EtatInitial     etatInitial = new EtatInitial();    // etatCourant initial du controleur
    EtatTourJoueur etatTourJ1 = new EtatTourJoueur();     // tour de jeu
    EtatFin         etatFin = new EtatFin();            // fin du jeu

    ArrayList<String> tokenIA= new ArrayList<>();
    HashMap<String, Object> map = new HashMap<>();

    Board board;
    Timer tempsTour=new Timer("FinDuTour");

    public Controler() {
        etatCourant = etatInitial;
    }

    Etat getEtatCourant() {
        return etatCourant;
    }

    void setEtatCourant(Etat etatCourant) {
        this.etatCourant = etatCourant;
    }

    Map<String, Object> generateToken(String name){
        return etatCourant.generateToken(name,this);
    }

    Map<String, Object> resetGame(){
        return etatCourant.reset(this);
    }

    Map<String, Object> setMap(String nameMap){
        return etatCourant.setMap(nameMap,this);
    }

    Map<String, Object> startGame(){
        return etatCourant.start(this);
    }


    Map<String, Object> doAction(String token, List<ActionJson> jsonTransaction){
        return etatCourant.doAction(token,this, jsonTransaction);
    }

    Map<String, Object> getBoard(String token){
        return etatCourant.getBoard(token,this);
    }

    Map<String, Object> getVisible(String token){
        return etatCourant.getVisible(token,this);
    }

    Map<String, Object> doWait(String token){
        return etatCourant.doWait(token,this);
    }

    Map<String, Object> endTurn(String token){
        return etatCourant.endTurn(token,this);
    }

    /*public void test(){
        try{
            Board b = new Board("src/main/resources/map0.txt");

            //testEndTurn(b);
            testTransServeur(b);
        }
        catch(Exception e){

        }
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
        try{

            b.getStatusBoard(1);
            b.getStatusBoard(2);
        }catch(Exception e){
            log.error(e.getMessage());
        }
        b.statusNodes();
        b.move(1,0,1,15);
        b.endTurn();
        b.getStatusBoard(1);
        b.move(1,1,2,15);
        System.out.println(b.endTurn());
        b.statusNodes();
    }*/
}