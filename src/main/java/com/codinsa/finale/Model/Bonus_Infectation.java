package com.codinsa.finale.Model;

import org.jvnet.hk2.config.Changed;

public class Bonus_Infectation extends Bonus {
    double factor;
    public Bonus_Infectation(double factor)
    {
        this.factor = factor;
    }
    public void activate(Player p)
    {
    }
    public void deactivate(Player p)
    {
    }
    public TYPE_BONUS getType()
    {
        return TYPE_BONUS.INFECTATION;
    }
}
