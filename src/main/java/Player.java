public class Player {
    int idPlayer;
    int maxDebit;
    int current_debit;
    public Player(int idPlayer,int maxDebit)
    {
        this.idPlayer = idPlayer;
        this.maxDebit = maxDebit;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public int getMaxDebit() {
        return maxDebit;
    }
    public int getCurrentDebit() {
        return current_debit;
    }

    public boolean addToCurrentDebit( int qt ){
        if(current_debit + qt <= maxDebit){
            current_debit += qt;
            return true;
        }
        return false;
    }
}
