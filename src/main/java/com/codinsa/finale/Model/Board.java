package com.codinsa.finale.Model;

import com.codinsa.finale.Util.SerialiseurBoard;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//@JsonSerialize(using = SerialiseurBoard.class)
public class Board {
    private List < Node > graph;
    private Integer [] [] matAdj;
    private Player [] players;
    private List < Transaction > transactions_waiting;
    private List < Transaction > transaction_server;
    private List < Transaction > transactions_validated;
    private boolean doExport = true;
    private String nameExport = "gameExport_map.txt";
    private PrintWriter pw = null;
    public Board( String nameBoard )
    {
        try {
            importGame(nameBoard);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //geneBonus();
        transactions_waiting = new LinkedList<Transaction>();
        transaction_server = new LinkedList<Transaction>();
        transactions_validated = new LinkedList<Transaction>();
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
            p.endTurn();
        }
        for( Node n : graph )
        {
            transaction_server.addAll(n.endTurn());
        }
        int winner  = isGameEnded();
        if(doExport){
            exportTour(winner);
        }
        return (winner==0);
    }
    private void processTransactions()
    {
        // pour chaque transaction,
        transactions_validated = new LinkedList<Transaction>();
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
        transactions_waiting = new LinkedList<Transaction>();
        transaction_server = new LinkedList<Transaction>();
    }
    private int isGameEnded()
    {
        List< Integer > playerStill = new LinkedList < Integer> ();
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
            pw.println(n.getCoordX()+" "+n.getCoordY());
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
        List < Node > result = new LinkedList<Node>();
        boolean [] visited = new boolean [graph.size()];
        boolean goBFS = false;
        List <  Integer > queue = new LinkedList<Integer>();
        for( int i = 0 ; i < graph.size() && !goBFS ; i++)
        {
            if(graph.get(i).getOwner().getIdPlayer()==idPlayer) //BFS a partir de cette node la
            {
                goBFS = true;
                queue.add(i);
                visited[i] = true;
                //PAS D'ITERATEUR ICI -> ON NE PEUT PAS MODIFIER UNE LISTE QU'ON LIT EN MÊME TEMPS
                //for(Integer id : queue)
                for(int l=0; l<queue.size(); l++)
                {
                    Integer id=queue.get(0);
                    result.add(graph.get(i));
                    if(graph.get(i).getOwner().getIdPlayer() == idPlayer)
                    {
                        for(Node neighbor : graph.get(i).getNeighbors())
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
            if(n.getOwner().getIdPlayer() == 0)
            {
                n.assign_Bonus(new Bonus_Freeze());
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

}
