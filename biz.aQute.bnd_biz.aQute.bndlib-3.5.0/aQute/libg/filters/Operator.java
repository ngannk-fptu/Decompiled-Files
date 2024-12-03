/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

public enum Operator {
    Equals("="),
    LessThanOrEqual("<="),
    GreaterThanOrEqual(">="),
    ApproxEqual("~=");

    private final String symbol;

    private Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}

