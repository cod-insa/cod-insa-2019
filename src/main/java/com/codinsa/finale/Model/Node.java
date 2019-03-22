package com.codinsa.finale.Model;

import java.util.LinkedList;
import java.util.List;

public class Node {


    private int id;
    private float coordX;
    private float coordY;
    private int production;
    private int qtCode;
    private Player owner;
    private List< Node > neighbors;
    private List< Transaction > transactions_en_cours;

    private Bonus bonus_on_node;
    protected boolean estTombe = false;
    public Node( int pid, float pcoordX, float pcoordY, int pproduction, int qtInit )
    {
        id = pid;
        coordX = pcoordX;
        coordY = pcoordY;
        production = pproduction;
        qtCode = qtInit;
        neighbors = new LinkedList<Node>();
        transactions_en_cours = new LinkedList<Transaction>();
    }
    public void setOwner( Player p )
    {
       owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public int getProduction() {
        return production;
    }

    public int getQtCode() {
        return qtCode;
    }

    public List < Node > getNeighbors()
    {
        return neighbors;
    }
    public int getId(){
        return id;
    }

    public void add_transaction(Transaction t)
    {
        transactions_en_cours.add(t);
        //System.out.println(t);
    }
    public void process_transactions()
    {
        int [] tab_opp = new int [10];
        for( Transaction tr : transactions_en_cours) {
            if (tr.getOwner().getIdPlayer() == owner.getIdPlayer()) {
                qtCode += tr.getQtCode();
            } else {
                tab_opp[tr.getOwner().getIdPlayer()] += tr.getQtCode();
            }
        }
        // trouver celui qui a la plus grande quantite de code, si egalite alors random
        //trouver max
        int maxi = -1;
        List < Integer > idMax = new LinkedList<Integer >();
        for( int i = 0 ; i < tab_opp.length ; i++)
        {
            if(tab_opp[i] > maxi)
            {
                idMax = new LinkedList<Integer>();
                idMax.add(i);
                maxi = tab_opp[i];
            }
            else if (tab_opp[i] == maxi)
            {
                idMax.add(i);
            }
        }
        if(maxi > qtCode)
        {
            int idWin = idMax.get((int)(Math.random()*idMax.size()));
            for( Transaction tr : transactions_en_cours)
            {
                if(tr.getOwner().getIdPlayer()==idWin)
                {
                    // passer en winner, update la qtte de code
                    owner = tr.getOwner();
                    qtCode = maxi - qtCode;
                    estTombe = true;
                    // on applique le bonus au joueur
                    if(bonus_on_node != null)
                    {
                        owner.addBonus(bonus_on_node);
                        bonus_on_node = null;
                    }
                }
            }
        }
        else { // pas d'attaquants assez forts
            if(maxi != -1)
            {
                qtCode -= maxi;
            }
        }

        transactions_en_cours = new LinkedList < Transaction>();
    }

    public List<Transaction> endTurn()
    {
        if( !owner.getFrozen())
        {
            qtCode+=production;
        }
        return new LinkedList<Transaction>();
    }

    public void removeCode(int q)
    {
        qtCode-=q;
    }
    @Override
    public String toString() {
        return "com.codinsa.finale.Model.Node{" +
                "id=" + id +
                ", coordX=" + coordX +
                ", coordY=" + coordY +
                ", production=" + production +
                ", qtCode=" + qtCode +
                ", owner=" + Integer.toString(owner.getIdPlayer()) +
                ", neighbors=" + neighbors.size() +
                ", transactions_en_cours=" + transactions_en_cours +
                '}';
    }

    public void assign_Bonus(Bonus b)
    {
        bonus_on_node = b;
    }
}
