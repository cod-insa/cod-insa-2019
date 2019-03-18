import java.util.LinkedList;
import java.util.List;

public class Serveur extends Node{
    public Serveur(int pid, float pcoordX, float pcoordY, int pproduction, int qtInit) {
        super(pid, pcoordX, pcoordY, pproduction, qtInit);
    }

    @Override
    public List<Transaction> endTurn() {
        List<Transaction> wave = new LinkedList<Transaction>();
        super.endTurn();
        if(estTombe){
            estTombe = false;
            for(Node n:getNeighbors()) {
                wave.add(new Transaction(getOwner(), this, n, getQtCode()));
            }
        }
        return wave;
    }
}
