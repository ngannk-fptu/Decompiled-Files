/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.Event;

public class InputBufferEvent
extends Event {
    char c;
    int lookaheadAmount;
    public static final int CONSUME = 0;
    public static final int LA = 1;
    public static final int MARK = 2;
    public static final int REWIND = 3;

    public InputBufferEvent(Object object) {
        super(object);
    }

    public InputBufferEvent(Object object, int n, char c, int n2) {
        super(object);
        this.setValues(n, c, n2);
    }

    public char getChar() {
        return this.c;
    }

    public int getLookaheadAmount() {
        return this.lookaheadAmount;
    }

    void setChar(char c) {
        this.c = c;
    }

    void setLookaheadAmount(int n) {
        this.lookaheadAmount = n;
    }

    void setValues(int n, char c, int n2) {
        super.setValues(n);
        this.setChar(c);
        this.setLookaheadAmount(n2);
    }

    public String toString() {
        return "CharBufferEvent [" + (this.getType() == 0 ? "CONSUME, " : "LA, ") + this.getChar() + "," + this.getLookaheadAmount() + "]";
    }
}

