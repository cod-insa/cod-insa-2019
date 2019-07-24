public class Transaction {
    private Player owner;
    private Node from;
    private Node to;
    private int qtCode;
    public Transaction(Player owner, Node from, Node to, int qtCode) {
        this.owner = owner;
        this.from = from;
        this.to = to;
        this.qtCode = qtCode;
    }

    public Player getOwner() {
        return owner;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public int getQtCode() {
        return qtCode;
    }
    public void addQtt( int qtt ) {
        this.qtCode += qtt;
    }
}
