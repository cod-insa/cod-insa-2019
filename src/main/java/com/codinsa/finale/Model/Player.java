package com.codinsa.finale.Model;

import java.util.LinkedList;
import java.util.List;

public class Player {
    int idPlayer;
    int maxDebit;
    int current_debit;
    double factIncrDebit;
    public double factorProd = 1.0;
    List< Bonus > current_bonus;
    List < Bonus > next_bonus;
    boolean frozen = false;
    public Player(int idPlayer,int maxDebit)
    {
        this.idPlayer = idPlayer;
        this.maxDebit = maxDebit;
        current_bonus = new LinkedList<Bonus>();
        next_bonus = new LinkedList<Bonus>();
        factIncrDebit = 1.1;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public int getMaxDebit() {
        return maxDebit;
    }
    public int getCurrentDebit() {
        return current_debit;
    }

    public boolean addToCurrentDebit( int qt ){
        if(current_debit + qt <= maxDebit){
            current_debit += qt;
            return true;
        }
        return false;
    }
    public void setFrozen(boolean b)
    {
        frozen = b;
    }
    public boolean getFrozen()
    {
        return frozen;
    }
    public void addBonus(Bonus b)
    {
        next_bonus.add(b);
    }
    //TODO check passage from LinkedList to List
    public void endTurn(List<Node> graph)
    {
        maxDebit = (int)(maxDebit*factIncrDebit);
        current_debit = 0;
        for(Bonus b:current_bonus){
            b.deactivate(this);
        }
        current_bonus = new LinkedList<Bonus>();
        current_bonus.addAll(next_bonus);
        next_bonus = new LinkedList<>();
        for( Bonus b: current_bonus){
            b.activate(this);
            if(b.getType()==TYPE_BONUS.INFECTATION)
            {
                int depart = (int)(Math.random()*graph.size());
                int step = depart;
                while(step!=(depart+graph.size()) && graph.get(step%graph.size()).getOwner().getIdPlayer()==idPlayer)
                {
                    step+=1;
                }
                graph.get(step%graph.size()).setOwner(this);
            }
        }
    }

    public void setMaxDebit(int maxDebit) {
        this.maxDebit = maxDebit;
    }
}
