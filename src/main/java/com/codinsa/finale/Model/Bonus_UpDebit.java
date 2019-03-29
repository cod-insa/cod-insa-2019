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
    public TYPE_BONUS getType()
    {
        return TYPE_BONUS.UP_DEBIT;
    }
}
