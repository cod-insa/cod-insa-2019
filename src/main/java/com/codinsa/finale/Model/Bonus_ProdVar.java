package com.codinsa.finale.Model;

public class Bonus_ProdVar extends Bonus {
    double factor;
    public Bonus_ProdVar(double factor)
    {
        this.factor = factor;
    }
    public void activate(Player p)
    {
        p.factorProd*=factor;
    }
    public void deactivate(Player p)
    {
        p.factorProd/=factor;
    }
    public TYPE_BONUS getType()
    {
        return TYPE_BONUS.PRODVAR;
    }
}
