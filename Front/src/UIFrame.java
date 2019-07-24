import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;

public class UIFrame extends JFrame implements KeyListener {
    int tX;
    int tY;
    int tGameX;
    int tGameY;
    float tBase;
    float tInter;
    JPanel paneMain;
    JPanel paneGame;
    BufferedImage game;
    JPanel paneControl;
    Graphics buffer;
    double xc=0;
    double yc=0;


    //JButton rButton;
    //JButton lButton;

    int [][] matAdj;
    List< Node > graph;
    List< Player> players;
    Color[] colors = {Color.WHITE,Color.RED,Color.BLUE};
    List < List < Transaction >> histoTrans;
    private int turn = 0;
    private int nbTurn = 0;


    private JLabel descTurn;
    private List <JLabel> descPlayers;
    public UIFrame(String nameFile) {
        graph = new LinkedList< Node >();
        histoTrans = new LinkedList<>();
        loadData(nameFile);

        Toolkit t = Toolkit.getDefaultToolkit();
        this.tX = (int) (t.getScreenSize().height * 1.2);
        this.tY = (int) (t.getScreenSize().height * 0.8);
        paneMain = new JPanel();
        paneMain.setPreferredSize(new Dimension(tX,tY));
        paneMain.setLayout(new BoxLayout(paneMain,BoxLayout.X_AXIS));
        tGameX = tY;
        tGameY = tY;
        tBase = 0.03f;
        tInter = tBase*0.7f;
        paneGame = new JPanel();
        paneGame.setPreferredSize(new Dimension(tGameX, tGameY));
        game = new BufferedImage(tGameX,tGameY,BufferedImage.TYPE_INT_ARGB);
        paneControl = new JPanel();
        paneControl.setPreferredSize(new Dimension(tX-tGameX,tY));
        //rButton = new JButton(">");
        //lButton = new JButton("<");
        //int tButtonX = (int)((tX-tGameX)*1);
        //int tButtonY = (int)(tY*0.08);
        descTurn = new JLabel();
        descTurn.setBounds((int)(paneControl.getX()*0.1)
                ,(int)(paneControl.getY()*0.2)
                ,(int)(paneControl.getX()*0.8)
                ,(int)(paneControl.getY()*0.2));
        Font font = new Font("Arial", Font.BOLD,18);
        descTurn.setFont(font);
        paneControl.add(descTurn);
        descPlayers = new LinkedList<JLabel>();
        for(int i = 0 ; i < players.size() ; i++){
            descPlayers.add(new JLabel());
            descPlayers.get(i).setBounds((int)(paneControl.getX()*0.1)
                        ,(int)(paneControl.getY()*(0.3+i*0.1))
                        ,(int)(paneControl.getX()*0.8)
                        ,(int)(paneControl.getY()*0.2));
            paneControl.add(descPlayers.get(i));
        }
        paneMain.add(paneGame);
        paneMain.add(paneControl);
        /*rButton.setBounds((int)(0.6*paneControl.getWidth())
                ,(int)(0.2*paneControl.getHeight())
                ,tButtonX,tButtonY);
        lButton.setBounds((int)(0.1*paneControl.getWidth())
                ,(int)(0.4*paneControl.getHeight())
                ,tButtonX,tButtonY);
        paneControl.add(rButton);
        paneControl.add(lButton);*/
        this.setContentPane(paneMain);
        this.pack();
        this.setVisible(true);
        update(this.getGraphics());

        //this.dispose();
        this.addKeyListener(this);
        this.repaint();

    }

    public void loadData(String nameFile) {
        Scanner input = new Scanner(System.in);
        try {
            File file = new File(nameFile);
            input = new Scanner(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int n = input.nextInt();
        matAdj = new int[n][n];
        int p = input.nextInt();
        players = new ArrayList<Player>();
        for(int i = 0 ; i<p ; i++)
        {
            String name = "Player"+Integer.toString(i);
            players.add(new Player(name,colors[i]));
        }
        for(int i = 0 ; i < n ; i++)
        {
            float cx = input.nextFloat()*0.8f+0.1f;
            float cy = input.nextFloat()*0.8f+0.1f;
            graph.add(new Node(cx,cy,input.nextInt()));
        }
        for(int i = 0 ; i < n ; i++)
        {
            for (int j = 0 ; j < n; j++)
            {
                matAdj[i][j] = input.nextInt();
            }
        }
        int winner = 0;
        while(winner == 0) // recupere pour un tour
        {
            nbTurn++;
            int nbTrans = input.nextInt();
            if(nbTurn!=1)
            {histoTrans.add(new LinkedList<Transaction>());}//offset du premier tour
            for(int i = 0 ; i < nbTrans ; i++)
            {
                histoTrans.get(nbTurn-2).add(
                        new Transaction(
                                players.get(input.nextInt()),
                                graph.get(input.nextInt()),
                                graph.get(input.nextInt()),
                                input.nextInt()));
            }
            histoTrans.add(new LinkedList<Transaction>());
            for( int i = 0 ; i < n ; i++)
            {
                Integer[] tour = {input.nextInt(),input.nextInt()};
                graph.get(i).ajouterHistorique(tour);
            }
            winner = input.nextInt();
        }

        input.close();
    }

    public void paint(Graphics g) {
        game = new BufferedImage(tGameX,tGameY,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gGame = (Graphics2D)(game.getGraphics());
        gGame.setColor(Color.white);
        gGame.fillRect(0,0,tX,tY);
        gGame.setColor(Color.black);
        float [] dash = {10.0f};
        gGame.setStroke(new BasicStroke(2.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                5.0f, dash , 0.0f));
        //liens
        for(int i = 0 ; i < graph.size() ; i++)
        {
            for(int j = i+1 ; j < graph.size() ; j++)
            {
                if(matAdj[i][j] != 0 )
                {
                    gGame.drawLine(toGameSize(graph.get(i).getCoordX())
                            ,toGameSize(graph.get(i).getCoordY())
                            ,toGameSize(graph.get(j).getCoordX())
                            ,toGameSize(graph.get(j).getCoordY()));
                }
            }
        }
        //attaques
        gGame.setStroke(new BasicStroke(3.0f));
        for(Transaction trans : histoTrans.get(turn))
        {
            gGame.setColor(trans.getOwner().getCouleur());
            gGame.drawLine(toGameSize(trans.getFrom().getCoordX()),
                            toGameSize(trans.getFrom().getCoordY()),
                            toGameSize((trans.getTo().getCoordX()+trans.getFrom().getCoordX())*0.5f),
                            toGameSize((trans.getTo().getCoordY()+trans.getFrom().getCoordY())*0.5f));
            float tCircle = 0.05f;
            gGame.setColor(Color.white);
            gGame.fillOval(toGameSize((trans.getTo().getCoordX()+trans.getFrom().getCoordX()*3)*0.25f)-toGameSize(tCircle/2.0f),
                    toGameSize((trans.getTo().getCoordY()+trans.getFrom().getCoordY()*3)*0.25f)-toGameSize(tCircle/2.0f),
                    toGameSize(tCircle),
                    toGameSize(tCircle));
            gGame.setColor(trans.getOwner().getCouleur());
            gGame.drawString(Integer.toString(trans.getQtCode()),
                    toGameSize((trans.getTo().getCoordX()+trans.getFrom().getCoordX()*2)*0.33f)-toGameSize(0.005f),
                    toGameSize((trans.getTo().getCoordY()+trans.getFrom().getCoordY()*2)*0.33f)+toGameSize(0.005f));
        }
        gGame.setStroke(new BasicStroke(1.0f));
        for(int i = 0 ; i < graph.size(); i++)
        {
            //outer
            int oxOut = toGameSize(graph.get(i).getCoordX())-toGameSize(tBase);
            int oyOut = toGameSize(graph.get(i).getCoordY())-toGameSize(tBase);
            int txOut = toGameSize(tBase*2);
            int tyOut = toGameSize(tBase*2);
            gGame.setColor(players.get(graph.get(i).getOwner(turn)).getCouleur());
            fillShape(oxOut,oyOut,txOut,tyOut,gGame,graph.get(i).type);

            gGame.setColor(Color.black);
            drawShape(oxOut,oyOut,txOut,tyOut,gGame,graph.get(i).type);

            //inner
            int oxIn = toGameSize(graph.get(i).getCoordX())-toGameSize(tInter);
            int oyIn = toGameSize(graph.get(i).getCoordY())-toGameSize(tInter);
            int txIn = toGameSize(tInter*2);
            int tyIn = toGameSize(tInter*2);


            gGame.setColor(Color.white);
            fillShape(oxIn,oyIn,txIn,tyIn,gGame,graph.get(i).type);

            gGame.setColor(Color.black);
            drawShape(oxIn,oyIn,txIn,tyIn,gGame,graph.get(i).type);

            gGame.setColor(Color.black);
            gGame.drawString(Integer.toString(graph.get(i).getQt(turn))
                    ,toGameSize(graph.get(i).getCoordX())-5
                    ,toGameSize(graph.get(i).getCoordY())+5);
        }
        String dt = "Tour : "+Integer.toString(turn+1)+" / "+nbTurn;
        descTurn.setText(dt);
        paneGame.getGraphics().drawImage(game,0,0,tGameX,tGameY,this);
    }
    public void drawShape(int oX,int oY, int tX, int tY, Graphics2D g, int type)
    {
        if(type==0)
        {
            g.drawOval(oX,oY,tX,tY);
        }
        else
        {
            g.drawRect(oX,oY,tX,tY);
        }
    }
    public void fillShape(int oX,int oY, int tX, int tY, Graphics2D g, int type)
    {
        if(type==0)
        {
            g.fillOval(oX,oY,tX,tY);
        }
        else
        {
            g.fillRect(oX,oY,tX,tY);
        }
    }
    public int toGameSize(float f)
    {
        return (int)(f*tGameX);
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {
        this.xc=(double)(e.getX())/(double)(this.tX);
        this.yc=(double)(e.getY())/(double)(this.tX);
    }
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}



    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==39)
        {
            if( turn != nbTurn -1)
            {
                turn++;
            }
        }
        if(e.getKeyCode()==37)
        {
            if(turn!=0)
            {
                turn--;
            }
        }
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    public static void main(String[] args)
    {

        UIFrame baseFrame = new UIFrame("RENNES_TOULOUSE.txt");

    }
}
