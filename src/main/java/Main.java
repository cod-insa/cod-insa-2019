public class Main {
    public static void main(String [] args)
    {
        Board b = new Board("map0.txt");
        //testEndTurn(b);
        testTransServeur(b);
    }
    public static void testTransaction( Board b)
    {
        b.move(1,0,1,2);
    }
    public static void testEndTurn(Board b)
    {
        b.statusNodes();
        b.move(1,0,1,2);
        b.move(2,2,1,2);
        b.endTurn();
        b.statusNodes();
    }
    public static void testTransServeur(Board b)
    {
        b.statusNodes();
        b.move(1,0,1,5);
        b.endTurn();
        b.statusNodes();
        b.endTurn();
        b.statusNodes();
    }
}
