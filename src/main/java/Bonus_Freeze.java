public class Bonus_Freeze extends Bonus{
    public Bonus_Freeze()
    {

    }
    public void activate(Player p)
    {
        p.setFrozen(true);
    }
    public void deactivate(Player p)
    {
        p.setFrozen(false);
    }
}
