/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.Event;

public class ParserTokenEvent
extends Event {
    private int value;
    private int amount;
    public static int LA = 0;
    public static int CONSUME = 1;

    public ParserTokenEvent(Object object) {
        super(object);
    }

    public ParserTokenEvent(Object object, int n, int n2, int n3) {
        super(object);
        this.setValues(n, n2, n3);
    }

    public int getAmount() {
        return this.amount;
    }

    public int getValue() {
        return this.value;
    }

    void setAmount(int n) {
        this.amount = n;
    }

    void setValue(int n) {
        this.value = n;
    }

    void setValues(int n, int n2, int n3) {
        super.setValues(n);
        this.setAmount(n2);
        this.setValue(n3);
    }

    public String toString() {
        if (this.getType() == LA) {
            return "ParserTokenEvent [LA," + this.getAmount() + "," + this.getValue() + "]";
        }
        return "ParserTokenEvent [consume,1," + this.getValue() + "]";
    }
}

