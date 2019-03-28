package com.codinsa.finale.Model;

public class ActionJson {
    private int owner;
    private int from;
    private int to;
    private int qtCode;
    public ActionJson(int owner, int from, int to, int qtCode) {
        this.owner = owner;
        this.from = from;
        this.to = to;
        this.qtCode = qtCode;
    }

    public int getOwner() {
        return owner;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getQtCode() {
        return qtCode;
    }
    public void addQtt( int qtt ) {
        this.qtCode += qtt;
    }

    public void setQtCode(int qtCode) {
        this.qtCode = qtCode;
    }

}
