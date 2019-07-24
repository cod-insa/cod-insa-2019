import java.awt.*;

import java.util.LinkedList;
import java.util.List;
public class Player {
    private String name;
    Color couleur;
    private List<Integer> histoDebitMax;
    public Player(String name, Color couleur) {
        this.name = name;
        this.couleur = couleur;
        this.histoDebitMax = new LinkedList<Integer>();
    }

    public String getName() {
        return name;
    }

    public Color getCouleur() {
        return couleur;
    }
    public void addToHisto(int d)
    {
        histoDebitMax.add(d);
    }
}
