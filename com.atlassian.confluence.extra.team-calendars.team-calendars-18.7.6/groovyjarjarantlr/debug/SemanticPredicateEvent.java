/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.GuessingEvent;

public class SemanticPredicateEvent
extends GuessingEvent {
    public static final int VALIDATING = 0;
    public static final int PREDICTING = 1;
    private int condition;
    private boolean result;

    public SemanticPredicateEvent(Object object) {
        super(object);
    }

    public SemanticPredicateEvent(Object object, int n) {
        super(object, n);
    }

    public int getCondition() {
        return this.condition;
    }

    public boolean getResult() {
        return this.result;
    }

    void setCondition(int n) {
        this.condition = n;
    }

    void setResult(boolean bl) {
        this.result = bl;
    }

    void setValues(int n, int n2, boolean bl, int n3) {
        super.setValues(n, n3);
        this.setCondition(n2);
        this.setResult(bl);
    }

    public String toString() {
        return "SemanticPredicateEvent [" + this.getCondition() + "," + this.getResult() + "," + this.getGuessing() + "]";
    }
}

