import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Node {
    private float coordX;
    private float coordY;
    private List< Integer [] > historique;
    int type;
    public Node(float cX, float cY,int type){
        this.type = type;
        coordX = cX;
        coordY = cY;
        historique = new LinkedList<Integer[]>();
    }

    public void ajouterHistorique(Integer[] tour)
    {
        historique.add(tour);
    }

    public float getCoordX() {
        return coordX;
    }

    public float getCoordY() {
        return coordY;
    }
    public int getOwner(int tour)
    {
        return historique.get(tour)[0];
    }
    public int getQt(int tour)
    {
        return historique.get(tour)[1];
    }

}
