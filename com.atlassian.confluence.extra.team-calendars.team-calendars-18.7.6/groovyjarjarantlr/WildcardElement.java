/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

class WildcardElement
extends GrammarAtom {
    protected String label;

    public WildcardElement(Grammar grammar, Token token, int n) {
        super(grammar, token, n);
        this.line = token.getLine();
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public String getLabel() {
        return this.label;
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public void setLabel(String string) {
        this.label = string;
    }

    public String toString() {
        String string = " ";
        if (this.label != null) {
            string = string + this.label + ":";
        }
        return string + ".";
    }
}

