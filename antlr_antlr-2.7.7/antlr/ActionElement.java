/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeElement;
import antlr.Grammar;
import antlr.Lookahead;
import antlr.Token;

class ActionElement
extends AlternativeElement {
    protected String actionText;
    protected boolean isSemPred = false;

    public ActionElement(Grammar grammar, Token token) {
        super(grammar);
        this.actionText = token.getText();
        this.line = token.getLine();
        this.column = token.getColumn();
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return " " + this.actionText + (this.isSemPred ? "?" : "");
    }
}

