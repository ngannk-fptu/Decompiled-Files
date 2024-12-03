/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.GuessingEvent;

public class TraceEvent
extends GuessingEvent {
    private int ruleNum;
    private int data;
    public static int ENTER = 0;
    public static int EXIT = 1;
    public static int DONE_PARSING = 2;

    public TraceEvent(Object object) {
        super(object);
    }

    public TraceEvent(Object object, int n, int n2, int n3, int n4) {
        super(object);
        this.setValues(n, n2, n3, n4);
    }

    public int getData() {
        return this.data;
    }

    public int getRuleNum() {
        return this.ruleNum;
    }

    void setData(int n) {
        this.data = n;
    }

    void setRuleNum(int n) {
        this.ruleNum = n;
    }

    void setValues(int n, int n2, int n3, int n4) {
        super.setValues(n, n3);
        this.setRuleNum(n2);
        this.setData(n4);
    }

    public String toString() {
        return "ParserTraceEvent [" + (this.getType() == ENTER ? "enter," : "exit,") + this.getRuleNum() + "," + this.getGuessing() + "]";
    }
}

