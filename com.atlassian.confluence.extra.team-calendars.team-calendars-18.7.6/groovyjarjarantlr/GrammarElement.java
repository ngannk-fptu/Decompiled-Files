/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

abstract class GrammarElement {
    public static final int AUTO_GEN_NONE = 1;
    public static final int AUTO_GEN_CARET = 2;
    public static final int AUTO_GEN_BANG = 3;
    protected Grammar grammar;
    protected int line;
    protected int column;

    public GrammarElement(Grammar grammar) {
        this.grammar = grammar;
        this.line = -1;
        this.column = -1;
    }

    public GrammarElement(Grammar grammar, Token token) {
        this.grammar = grammar;
        this.line = token.getLine();
        this.column = token.getColumn();
    }

    public void generate() {
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public Lookahead look(int n) {
        return null;
    }

    public abstract String toString();
}

