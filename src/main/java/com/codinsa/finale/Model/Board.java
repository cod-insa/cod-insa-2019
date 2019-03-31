package com.codinsa.finale.Model;

import com.codinsa.finale.Util.SerialiseurBoard;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@JsonSerialize(using = SerialiseurBoard.class)
public class Board {
    private List < Node > graph;
    private Integer [] [] matAdj;
    private Player [] players;
    private List < Transaction > transactions_waiting;
    private List < Transaction > transaction_server;
    private List < Transaction > transactions_validated;
    private boolean doExport = true;
    private String nameExport = "./gameExport_map.txt";
    private PrintWriter pw = null;
    public Board( String nameBoard )
    {
        try {
            //importGame(nameBoard);
            importHard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        geneBonus();
        transactions_waiting = new LinkedList<>();
        transaction_server = new LinkedList<>();
        transactions_validated = new LinkedList<>();
        if(doExport)
        {
            startExport();
        }
    }

    public Board( BufferedReader nameBoard )
    {
        try {
            importGame("TODOTODO");
        } catch (Exception e) {
            e.printStackTrace();
        }
        geneBonus();
        transactions_waiting = new LinkedList<>();
        transaction_server = new LinkedList<>();
        transactions_validated = new LinkedList<>();
        if(doExport)
        {
            startExport();
        }
    }
    public boolean move( int idPlayer, int idNodeFrom, int idNodeTo,int qtte)
    {
        // verifie que la quantité est bien positive et que les coord sont cohérents par rapport au graphe
        if(graph.get(idNodeFrom).getOwner().getIdPlayer()!=idPlayer || qtte <= 0 || !graph.get(idNodeFrom).getNeighbors().contains(graph.get(idNodeTo))){
            return false;
        }
        // verifie si il y a déjà une transaction en cours pour ces infos
        boolean foundSame = false;
        for (Transaction trans : transactions_waiting){
            if(trans.getOwner().getIdPlayer()==idPlayer
            && trans.getFrom().getId()==idNodeFrom
            && trans.getTo().getId()==idNodeTo){
                trans.addQtt(qtte);
                foundSame = true;
            }
        }
        if(!foundSame) {
            transactions_waiting.add(new Transaction(players[idPlayer], graph.get(idNodeFrom), graph.get(idNodeTo), qtte));
        }
        return true;
    }

    public boolean endTurn()
    {
        processTransactions();
        for(Player p: players)
        {
            p.endTurn(graph);
        }
        for( Node n : graph )
        {
            transaction_server.addAll(n.endTurn());
        }
        int winner  = isGameEnded();
        if(doExport){
            exportTour(winner);
        }
        return (winner!=0);
    }
    private void processTransactions()
    {
        // pour chaque transaction,
        transactions_validated = new LinkedList<>();
        for (Transaction trans:transactions_waiting)
        {
            // System.out.println("avant coherence : "+ Integer.toString(trans.getOwner().getIdPlayer()) + " " +Integer.toString(trans.getFrom().getOwner().getIdPlayer()));
            if(trans.getOwner().getIdPlayer() == trans.getFrom().getOwner().getIdPlayer()) {
                // si elle excede le débit, bridée
                if (trans.getQtCode() > matAdj[trans.getFrom().getId()][trans.getTo().getId()]) {
                    trans.setQtCode(matAdj[trans.getFrom().getId()][trans.getTo().getId()]);
                }

                // si elle depasse le débit limite entre les nodes
                // ou si le joueur n'a pas assez pour l'enoyer
                // ou i elle fait dépasser le quota du joueur,, annuler
                // System.out.println(Integer.toString(trans.getQtCode())+" "+Integer.toString(matAdj[trans.getFrom().getId()][trans.getTo().getId()]));
                if (trans.getQtCode() <= matAdj[trans.getFrom().getId()][trans.getTo().getId()]
                        && trans.getFrom().getQtCode() >= trans.getQtCode()
                        && trans.getOwner().addToCurrentDebit(trans.getQtCode())) {

                    transactions_validated.add(trans);
                    trans.getFrom().removeCode(trans.getQtCode());
                }
            }
        }
        //merge les transactions_server avec les validated
        for( Transaction trans : transaction_server) {
            boolean merged = false;
            for (Transaction trVal : transactions_validated) {
                if (!merged && (trVal.getFrom().getId() == trans.getFrom().getId()) &&
                        (trVal.getTo().getId()) == trans.getTo().getId()) {
                    trVal.addQtt(trans.getQtCode());
                    merged = true;
                }
            }
            if (!merged) {
                transactions_validated.add(trans);
            }
        }
        // distribuer les transactions sur chaque node, qui les gèrent ensuite
        for( Transaction trans: transactions_validated)
        {
            trans.getTo().add_transaction(trans);
        }
        for( Node noeud : graph)
        {
            noeud.process_transactions();
        }
        transactions_waiting = new LinkedList<>();
        transaction_server = new LinkedList<>();
    }
    private int isGameEnded()
    {
        List< Integer > playerStill = new LinkedList<>();
        for( Node n : graph)
        {
            if( n.getOwner().getIdPlayer()!=0 && !playerStill.contains(n.getOwner().getIdPlayer()))
            {
                playerStill.add(n.getOwner().getIdPlayer());
            }
        }
        if(playerStill.size()!=1)
        {
            return 0;
        }
        else {
            return playerStill.get(0);
        }
    }
    // retourne la matrice d'ajdacence avec débits sur les arcs
    private void importGame(String nameBoard) throws Exception// ,Integer [][] matAdj, List < com.codinsa.finale.Model.Node > graph, com.codinsa.finale.Model.Player [] players)
    {
        List < Node > graphImport = new ArrayList<Node>();
        Scanner input = new Scanner(System.in);
        try {
            File file = new File(nameBoard);
            input = new Scanner(file);
            input.useLocale(Locale.US);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(nameBoard+" was not found !");
        }
        int n = input.nextInt();
        matAdj = new Integer[n][n];
        for(int i = 0 ; i < n ; i++)
        {
            for( int j = 0 ; j < n ; j++)
            {
                matAdj[i][j] = 0;
            }
        }
        graph = new ArrayList<Node>();
        // nodes
        for(int i = 0 ; i < n ; i++)
        {
            int type = input.nextInt();
            float cX = input.nextFloat();
            float cY = input.nextFloat();
            int prod = input.nextInt();
            int qtInit = input.nextInt();
            if(type == 1)
            {
                graph.add(new Serveur(i,cX,cY,prod,qtInit));
            }
            else{
                graph.add(new Node(i,cX,cY,prod,qtInit));
            }
        }
        // links
        int l = input.nextInt();
        for( int i = 0 ; i < l ; i++)
        {
            int nodeFrom = input.nextInt();
            int nodeTo = input.nextInt();
            int debit = input.nextInt();
            matAdj[nodeFrom][nodeTo] = debit;
            matAdj[nodeTo][nodeFrom] = debit;
            graph.get(nodeTo).getNeighbors().add(graph.get(nodeFrom));
            graph.get(nodeFrom).getNeighbors().add(graph.get(nodeTo));
        }
        int p = input.nextInt();
        players = new Player[p+1];
        players[0] = new Player(0,0);
        for( Node node : graph)
        {
            node.setOwner(players[0]);
        }
        for( int i = 1 ; i < p+1 ; i++)
        {
            int max_debit =  input.nextInt();
            int nodeDepart = input.nextInt();
            players[i] = new Player(i,max_debit);
            graph.get(nodeDepart).setOwner(players[i]);
        }
    }
    public void close(){pw.close();}
    private void startExport()
    {
        try {
            pw = new PrintWriter(nameExport);
        }catch(Exception e)
        {
            System.out.println("file not found");
        }
        String l1 = Integer.toString(graph.size()) + " " + Integer.toString(players.length);
        pw.println(l1);
        for(Node n : graph)
        {
            pw.println(n.getCoordX()+" "+n.getCoordY()+" "+n.getTypeNode());
        }
        for(int i = 0 ; i < matAdj.length ; i++) {
            for (int j = 0; j < matAdj.length; j++) {
                pw.print(matAdj[i][j]);
                if (j != matAdj.length - 1) {
                    pw.print(" ");
                } else {
                    pw.println("");
                }
            }
        }
        exportTour(0);
    }
    private void exportTour( int idWinner )
    {
        pw.println(transactions_validated.size());
        for(Transaction t : transactions_validated)
        {
            pw.println(t.getOwner().getIdPlayer()+" "+t.getFrom().getId()+" "+t.getTo().getId()+" "+t.getQtCode());
        }

        for(Node n : graph)
        {
            pw.println(Integer.toString(n.getOwner().getIdPlayer())+" "+Integer.toString(n.getQtCode()));
        }

        pw.println(idWinner);
        if(idWinner!=0)
        {
            pw.close();
        }
    }

    public List<Node> getGraph()
    {
        return graph;
    }
    public List<Node> getStatusBoard( int idPlayer )
    {
        // BFS à partir de la premiere node que l'on trouve qui appartient au joueur.
        //Pas de propagation à partir des voisins non conquis -> brouillard de guerre
        List < Node > result = new LinkedList<>();
        boolean [] visited = new boolean [graph.size()];
        boolean goBFS = false;
        List <  Integer > queue = new LinkedList<>();
        for( int i = 0 ; i < graph.size() && !goBFS ; i++)
        {
            if(graph.get(i).getOwner().getIdPlayer()==idPlayer) //BFS a partir de cette node la
            {
                goBFS = true;
                queue = new LinkedList<>();
                queue.add(i);
                visited[i] = true;

                //PAS D'ITERATEUR ICI -> ON NE PEUT PAS MODIFIER UNE LISTE QU'ON LIT EN MÊME TEMPS
                //for(Integer id : queue)
                for(int l=0; l<queue.size(); l++)
                {
                    Integer id=queue.get(l);
                    result.add(graph.get(id));
                    if(graph.get(id).getOwner().getIdPlayer() == idPlayer)
                    {
                        for(Node neighbor : graph.get(id).getNeighbors())
                        {
                            if(!visited[neighbor.getId()])
                            {
                                visited[neighbor.getId()] = true;
                                queue.add(neighbor.getId());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    public void statusNodes()
    {
        System.out.println("status : ");
        for( Node n : graph)
        {
            System.out.println(n);
        }
    }

    public void geneBonus()
    {
        for(Node n : graph)
        {
            boolean isBonus = ((Math.random()*3)<=1);
            if(isBonus && n.getOwner().getIdPlayer() == 0)
            {
                int typeBonus = (int)(Math.random()*3);
                switch(typeBonus)
                {
                    case 0:
                        n.assign_Bonus(new Bonus_Freeze());
                    break;
                    case 1 :
                        n.assign_Bonus(new Bonus_Infectation());
                    break;
                    case 2:
                        double fact = 1.0;
                        if(Math.random()*2>1)
                        {
                            fact+=1.0;
                        }
                        else{
                            fact-=0.5;
                        }
                        n.assign_Bonus(new Bonus_ProdVar(fact));
                    break;
                }

            }
        }
    }

    public int getWinner(){
        int[] scores = new int[players.length];
        for(Node n : graph)
        {
            scores[n.getOwner().getIdPlayer()]+=n.getQtCode();
        }
        int best = 1;
        for(int i = 1 ; i < scores.length ; i++)
        {
            if(scores[i]>scores[best])
            {
                best = i;
            }
        }
        if(doExport) {
            exportTour(best);
        }
        return best;
    }

    public int getDebitLink(int id1,int id2){
        return matAdj[id1][id2];
    }

    public void importHard() {
        List<Node> graphImport = new ArrayList<Node>();
        matAdj = new Integer[35][35];
        for (int i = 0; i < 35; i++) {
            for (int j = 0; j < 35; j++) {
                matAdj[i][j] = 0;
            }
        }
        graph = new ArrayList<Node>();
        // nodes
        graph.add(new Node(0, 0.0f, 0.5f, 15, 20));
        graph.add(new Node(1, 0.125f, 0.125f, 5, 5));
        graph.add(new Node(2, 0.125f, 0.5f, 5, 5));
        graph.add(new Node(3, 0.125f, 0.875f, 5, 5));
        graph.add(new Node(4, 0.25f, 0.0f, 5, 10));
        graph.add(new Node(5, 0.25f, 0.25f, 5, 10));
        graph.add(new Node(6, 0.25f, 0.375f, 5, 10));
        graph.add(new Node(7, 0.25f, 0.625f, 5, 10));
        graph.add(new Node(8, 0.25f, 0.75f, 5, 10));
        graph.add(new Node(9, 0.25f, 1.0f, 5, 10));
        graph.add(new Node(10, 0.375f, 0.125f, 10, 10));
        graph.add(new Node(11, 0.375f, 0.5f, 10, 10));
        graph.add(new Node(12, 0.375f, 0.875f, 10, 10));
        graph.add(new Serveur(13, 0.5f, 0.5f, 10, 40));
        graph.add(new Node(14, 0.625f, 0.125f, 10, 10));
        graph.add(new Node(15, 0.625f, 0.5f, 10, 10));
        graph.add(new Node(16, 0.625f, 0.875f, 10, 10));
        graph.add(new Node(17, 0.75f, 0.0f, 5, 10));
        graph.add(new Node(18, 0.75f, 0.25f, 5, 10));
        graph.add(new Node(19, 0.75f, 0.375f, 5, 10));
        graph.add(new Node(20, 0.75f, 0.625f, 5, 10));
        graph.add(new Node(21, 0.75f, 0.75f, 5, 10));
        graph.add(new Node(22, 0.75f, 1.0f, 5, 10));
        graph.add(new Node(23, 0.875f, 0.125f, 5, 5));
        graph.add(new Node(24, 0.875f, 0.5f, 5, 5));
        graph.add(new Node(25, 0.875f, 0.875f, 5, 5));
        graph.add(new Node(26, 1.0f, 0.5f, 15, 20));
        graph.add(new Node(27, 0.0625f, 0.3125f, 5, 5));
        graph.add(new Node(28, 0.9375f, 0.6875f, 5, 5));
        graph.add(new Node(29, 0.0625f, 0.6875f, 5, 5));
        graph.add(new Node(30, 0.9375f, 0.3125f, 5, 5));
        graph.add(new Serveur(31, 0.25f, 0.125f, 5, 5));
        graph.add(new Serveur(32, 0.25f, 0.875f, 5, 5));
        graph.add(new Serveur(33, 0.75f, 0.125f, 5, 5));
        graph.add(new Serveur(34, 0.75f, 0.875f, 5, 5));
        // links
        matAdj[0][2] = 100;
        matAdj[2][0] = 100;
        graph.get(2).getNeighbors().add(graph.get(0));
        graph.get(0).getNeighbors().add(graph.get(2));
        matAdj[1][4] = 80;
        matAdj[4][1] = 80;
        graph.get(4).getNeighbors().add(graph.get(1));
        graph.get(1).getNeighbors().add(graph.get(4));
        matAdj[1][5] = 80;
        matAdj[5][1] = 80;
        graph.get(5).getNeighbors().add(graph.get(1));
        graph.get(1).getNeighbors().add(graph.get(5));
        matAdj[2][6] = 80;
        matAdj[6][2] = 80;
        graph.get(6).getNeighbors().add(graph.get(2));
        graph.get(2).getNeighbors().add(graph.get(6));
        matAdj[2][7] = 80;
        matAdj[7][2] = 80;
        graph.get(7).getNeighbors().add(graph.get(2));
        graph.get(2).getNeighbors().add(graph.get(7));
        matAdj[3][8] = 80;
        matAdj[8][3] = 80;
        graph.get(8).getNeighbors().add(graph.get(3));
        graph.get(3).getNeighbors().add(graph.get(8));
        matAdj[3][9] = 80;
        matAdj[9][3] = 80;
        graph.get(9).getNeighbors().add(graph.get(3));
        graph.get(3).getNeighbors().add(graph.get(9));
        matAdj[4][17] = 10;
        matAdj[17][4] = 10;
        graph.get(17).getNeighbors().add(graph.get(4));
        graph.get(4).getNeighbors().add(graph.get(17));
        matAdj[4][10] = 60;
        matAdj[10][4] = 60;
        graph.get(10).getNeighbors().add(graph.get(4));
        graph.get(4).getNeighbors().add(graph.get(10));
        matAdj[5][10] = 60;
        matAdj[10][5] = 60;
        graph.get(10).getNeighbors().add(graph.get(5));
        graph.get(5).getNeighbors().add(graph.get(10));
        matAdj[5][6] = 80;
        matAdj[6][5] = 80;
        graph.get(6).getNeighbors().add(graph.get(5));
        graph.get(5).getNeighbors().add(graph.get(6));
        matAdj[6][11] = 60;
        matAdj[11][6] = 60;
        graph.get(11).getNeighbors().add(graph.get(6));
        graph.get(6).getNeighbors().add(graph.get(11));
        matAdj[7][8] = 80;
        matAdj[8][7] = 80;
        graph.get(8).getNeighbors().add(graph.get(7));
        graph.get(7).getNeighbors().add(graph.get(8));
        matAdj[7][11] = 60;
        matAdj[11][7] = 60;
        graph.get(11).getNeighbors().add(graph.get(7));
        graph.get(7).getNeighbors().add(graph.get(11));
        matAdj[8][12] = 60;
        matAdj[12][8] = 60;
        graph.get(12).getNeighbors().add(graph.get(8));
        graph.get(8).getNeighbors().add(graph.get(12));
        matAdj[9][12] = 60;
        matAdj[12][9] = 60;
        graph.get(12).getNeighbors().add(graph.get(9));
        graph.get(9).getNeighbors().add(graph.get(12));
        matAdj[9][22] = 10;
        matAdj[22][9] = 10;
        graph.get(22).getNeighbors().add(graph.get(9));
        graph.get(9).getNeighbors().add(graph.get(22));
        matAdj[10][14] = 20;
        matAdj[14][10] = 20;
        graph.get(14).getNeighbors().add(graph.get(10));
        graph.get(10).getNeighbors().add(graph.get(14));
        matAdj[10][13] = 40;
        matAdj[13][10] = 40;
        graph.get(13).getNeighbors().add(graph.get(10));
        graph.get(10).getNeighbors().add(graph.get(13));
        matAdj[11][13] = 40;
        matAdj[13][11] = 40;
        graph.get(13).getNeighbors().add(graph.get(11));
        graph.get(11).getNeighbors().add(graph.get(13));
        matAdj[12][13] = 40;
        matAdj[13][12] = 40;
        graph.get(13).getNeighbors().add(graph.get(12));
        graph.get(12).getNeighbors().add(graph.get(13));
        matAdj[12][16] = 20;
        matAdj[16][12] = 20;
        graph.get(16).getNeighbors().add(graph.get(12));
        graph.get(12).getNeighbors().add(graph.get(16));
        matAdj[13][14] = 40;
        matAdj[14][13] = 40;
        graph.get(14).getNeighbors().add(graph.get(13));
        graph.get(13).getNeighbors().add(graph.get(14));
        matAdj[13][15] = 40;
        matAdj[15][13] = 40;
        graph.get(15).getNeighbors().add(graph.get(13));
        graph.get(13).getNeighbors().add(graph.get(15));
        matAdj[13][16] = 40;
        matAdj[16][13] = 40;
        graph.get(16).getNeighbors().add(graph.get(13));
        graph.get(13).getNeighbors().add(graph.get(16));
        matAdj[14][17] = 60;
        matAdj[17][14] = 60;
        graph.get(17).getNeighbors().add(graph.get(14));
        graph.get(14).getNeighbors().add(graph.get(17));
        matAdj[14][18] = 60;
        matAdj[18][14] = 60;
        graph.get(18).getNeighbors().add(graph.get(14));
        graph.get(14).getNeighbors().add(graph.get(18));
        matAdj[15][19] = 60;
        matAdj[19][15] = 60;
        graph.get(19).getNeighbors().add(graph.get(15));
        graph.get(15).getNeighbors().add(graph.get(19));
        matAdj[15][20] = 60;
        matAdj[20][15] = 60;
        graph.get(20).getNeighbors().add(graph.get(15));
        graph.get(15).getNeighbors().add(graph.get(20));
        matAdj[16][21] = 60;
        matAdj[21][16] = 60;
        graph.get(21).getNeighbors().add(graph.get(16));
        graph.get(16).getNeighbors().add(graph.get(21));
        matAdj[16][22] = 60;
        matAdj[22][16] = 60;
        graph.get(22).getNeighbors().add(graph.get(16));
        graph.get(16).getNeighbors().add(graph.get(22));
        matAdj[17][23] = 80;
        matAdj[23][17] = 80;
        graph.get(23).getNeighbors().add(graph.get(17));
        graph.get(17).getNeighbors().add(graph.get(23));
        matAdj[18][23] = 80;
        matAdj[23][18] = 80;
        graph.get(23).getNeighbors().add(graph.get(18));
        graph.get(18).getNeighbors().add(graph.get(23));
        matAdj[18][19] = 80;
        matAdj[19][18] = 80;
        graph.get(19).getNeighbors().add(graph.get(18));
        graph.get(18).getNeighbors().add(graph.get(19));
        matAdj[19][24] = 80;
        matAdj[24][19] = 80;
        graph.get(24).getNeighbors().add(graph.get(19));
        graph.get(19).getNeighbors().add(graph.get(24));
        matAdj[20][24] = 80;
        matAdj[24][20] = 80;
        graph.get(24).getNeighbors().add(graph.get(20));
        graph.get(20).getNeighbors().add(graph.get(24));
        matAdj[20][21] = 80;
        matAdj[21][20] = 80;
        graph.get(21).getNeighbors().add(graph.get(20));
        graph.get(20).getNeighbors().add(graph.get(21));
        matAdj[21][25] = 80;
        matAdj[25][21] = 80;
        graph.get(25).getNeighbors().add(graph.get(21));
        graph.get(21).getNeighbors().add(graph.get(25));
        matAdj[22][25] = 80;
        matAdj[25][22] = 80;
        graph.get(25).getNeighbors().add(graph.get(22));
        graph.get(22).getNeighbors().add(graph.get(25));
        matAdj[24][26] = 100;
        matAdj[26][24] = 100;
        graph.get(26).getNeighbors().add(graph.get(24));
        graph.get(24).getNeighbors().add(graph.get(26));
        matAdj[0][27] = 100;
        matAdj[27][0] = 100;
        graph.get(27).getNeighbors().add(graph.get(0));
        graph.get(0).getNeighbors().add(graph.get(27));
        matAdj[1][27] = 100;
        matAdj[27][1] = 100;
        graph.get(27).getNeighbors().add(graph.get(1));
        graph.get(1).getNeighbors().add(graph.get(27));
        matAdj[25][28] = 100;
        matAdj[28][25] = 100;
        graph.get(28).getNeighbors().add(graph.get(25));
        graph.get(25).getNeighbors().add(graph.get(28));
        matAdj[26][28] = 100;
        matAdj[28][26] = 100;
        graph.get(28).getNeighbors().add(graph.get(26));
        graph.get(26).getNeighbors().add(graph.get(28));
        matAdj[0][29] = 100;
        matAdj[29][0] = 100;
        graph.get(29).getNeighbors().add(graph.get(0));
        graph.get(0).getNeighbors().add(graph.get(29));
        matAdj[3][29] = 100;
        matAdj[29][3] = 100;
        graph.get(29).getNeighbors().add(graph.get(3));
        graph.get(3).getNeighbors().add(graph.get(29));
        matAdj[23][30] = 100;
        matAdj[30][23] = 100;
        graph.get(30).getNeighbors().add(graph.get(23));
        graph.get(23).getNeighbors().add(graph.get(30));
        matAdj[26][30] = 100;
        matAdj[30][26] = 100;
        graph.get(30).getNeighbors().add(graph.get(26));
        graph.get(26).getNeighbors().add(graph.get(30));
        matAdj[1][31] = 100;
        matAdj[31][1] = 100;
        graph.get(31).getNeighbors().add(graph.get(1));
        graph.get(1).getNeighbors().add(graph.get(31));
        matAdj[4][31] = 100;
        matAdj[31][4] = 100;
        graph.get(31).getNeighbors().add(graph.get(4));
        graph.get(4).getNeighbors().add(graph.get(31));
        matAdj[5][31] = 100;
        matAdj[31][5] = 100;
        graph.get(31).getNeighbors().add(graph.get(5));
        graph.get(5).getNeighbors().add(graph.get(31));
        matAdj[10][31] = 100;
        matAdj[31][10] = 100;
        graph.get(31).getNeighbors().add(graph.get(10));
        graph.get(10).getNeighbors().add(graph.get(31));
        matAdj[3][32] = 100;
        matAdj[32][3] = 100;
        graph.get(32).getNeighbors().add(graph.get(3));
        graph.get(3).getNeighbors().add(graph.get(32));
        matAdj[8][32] = 100;
        matAdj[32][8] = 100;
        graph.get(32).getNeighbors().add(graph.get(8));
        graph.get(8).getNeighbors().add(graph.get(32));
        matAdj[9][32] = 100;
        matAdj[32][9] = 100;
        graph.get(32).getNeighbors().add(graph.get(9));
        graph.get(9).getNeighbors().add(graph.get(32));
        matAdj[12][32] = 100;
        matAdj[32][12] = 100;
        graph.get(32).getNeighbors().add(graph.get(12));
        graph.get(12).getNeighbors().add(graph.get(32));
        matAdj[14][33] = 100;
        matAdj[33][14] = 100;
        graph.get(33).getNeighbors().add(graph.get(14));
        graph.get(14).getNeighbors().add(graph.get(33));
        matAdj[17][33] = 100;
        matAdj[33][17] = 100;
        graph.get(33).getNeighbors().add(graph.get(17));
        graph.get(17).getNeighbors().add(graph.get(33));
        matAdj[18][33] = 100;
        matAdj[33][18] = 100;
        graph.get(33).getNeighbors().add(graph.get(18));
        graph.get(18).getNeighbors().add(graph.get(33));
        matAdj[23][33] = 100;
        matAdj[33][23] = 100;
        graph.get(33).getNeighbors().add(graph.get(23));
        graph.get(23).getNeighbors().add(graph.get(33));
        matAdj[16][34] = 100;
        matAdj[34][16] = 100;
        graph.get(34).getNeighbors().add(graph.get(16));
        graph.get(16).getNeighbors().add(graph.get(34));
        matAdj[21][34] = 100;
        matAdj[34][21] = 100;
        graph.get(34).getNeighbors().add(graph.get(21));
        graph.get(21).getNeighbors().add(graph.get(34));
        matAdj[22][34] = 100;
        matAdj[34][22] = 100;
        graph.get(34).getNeighbors().add(graph.get(22));
        graph.get(22).getNeighbors().add(graph.get(34));
        matAdj[25][34] = 100;
        matAdj[34][25] = 100;
        graph.get(34).getNeighbors().add(graph.get(25));
        graph.get(25).getNeighbors().add(graph.get(34));
        players = new Player[3];
        players[0] = new Player(0, 0);
        for (Node node : graph) {
            node.setOwner(players[0]);
        }
        players[1] = new Player(1, 100);
        players[2] = new Player(2, 100);
        graph.get(0).setOwner(players[1]);
        graph.get(26).setOwner(players[2]);
    }
}
