/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.GuessingEvent;

public class SyntacticPredicateEvent
extends GuessingEvent {
    public SyntacticPredicateEvent(Object object) {
        super(object);
    }

    public SyntacticPredicateEvent(Object object, int n) {
        super(object, n);
    }

    void setValues(int n, int n2) {
        super.setValues(n, n2);
    }

    public String toString() {
        return "SyntacticPredicateEvent [" + this.getGuessing() + "]";
    }
}

