package com.codinsa.finale.Model;

public class Bonus_UpDebit extends Bonus{
    public Bonus_UpDebit()
    {

    }
    public void activate(Player p)
    {
        p.setMaxDebit((int)(p.getMaxDebit()*1.5));
    }
    public void deactivate(Player p)
    {
    }
}
