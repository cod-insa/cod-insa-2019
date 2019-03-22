package com.codinsa.finale.Controler;

import com.codinsa.finale.Model.Board;
import org.springframework.stereotype.Component;

@Component
public class Controler {

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