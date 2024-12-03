/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.Lookahead;
import antlr.Token;

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

