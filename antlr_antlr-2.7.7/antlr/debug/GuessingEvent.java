/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.Event;

public abstract class GuessingEvent
extends Event {
    private int guessing;

    public GuessingEvent(Object object) {
        super(object);
    }

    public GuessingEvent(Object object, int n) {
        super(object, n);
    }

    public int getGuessing() {
        return this.guessing;
    }

    void setGuessing(int n) {
        this.guessing = n;
    }

    void setValues(int n, int n2) {
        super.setValues(n);
        this.setGuessing(n2);
    }
}

