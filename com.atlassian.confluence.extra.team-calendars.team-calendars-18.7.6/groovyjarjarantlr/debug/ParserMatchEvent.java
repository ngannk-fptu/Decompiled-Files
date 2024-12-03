/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.GuessingEvent;

public class ParserMatchEvent
extends GuessingEvent {
    public static int TOKEN = 0;
    public static int BITSET = 1;
    public static int CHAR = 2;
    public static int CHAR_BITSET = 3;
    public static int STRING = 4;
    public static int CHAR_RANGE = 5;
    private boolean inverse;
    private boolean matched;
    private Object target;
    private int value;
    private String text;

    public ParserMatchEvent(Object object) {
        super(object);
    }

    public ParserMatchEvent(Object object, int n, int n2, Object object2, String string, int n3, boolean bl, boolean bl2) {
        super(object);
        this.setValues(n, n2, object2, string, n3, bl, bl2);
    }

    public Object getTarget() {
        return this.target;
    }

    public String getText() {
        return this.text;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public boolean isMatched() {
        return this.matched;
    }

    void setInverse(boolean bl) {
        this.inverse = bl;
    }

    void setMatched(boolean bl) {
        this.matched = bl;
    }

    void setTarget(Object object) {
        this.target = object;
    }

    void setText(String string) {
        this.text = string;
    }

    void setValue(int n) {
        this.value = n;
    }

    void setValues(int n, int n2, Object object, String string, int n3, boolean bl, boolean bl2) {
        super.setValues(n, n3);
        this.setValue(n2);
        this.setTarget(object);
        this.setInverse(bl);
        this.setMatched(bl2);
        this.setText(string);
    }

    public String toString() {
        return "ParserMatchEvent [" + (this.isMatched() ? "ok," : "bad,") + (this.isInverse() ? "NOT " : "") + (this.getType() == TOKEN ? "token," : "bitset,") + this.getValue() + "," + this.getTarget() + "," + this.getGuessing() + "]";
    }
}

