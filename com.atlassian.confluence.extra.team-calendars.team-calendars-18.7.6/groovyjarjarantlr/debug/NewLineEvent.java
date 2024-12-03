/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.Event;

public class NewLineEvent
extends Event {
    private int line;

    public NewLineEvent(Object object) {
        super(object);
    }

    public NewLineEvent(Object object, int n) {
        super(object);
        this.setValues(n);
    }

    public int getLine() {
        return this.line;
    }

    void setLine(int n) {
        this.line = n;
    }

    void setValues(int n) {
        this.setLine(n);
    }

    public String toString() {
        return "NewLineEvent [" + this.line + "]";
    }
}

