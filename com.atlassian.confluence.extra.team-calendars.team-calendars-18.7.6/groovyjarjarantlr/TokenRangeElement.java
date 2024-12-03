/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

class TokenRangeElement
extends AlternativeElement {
    String label;
    protected int begin = 0;
    protected int end = 0;
    protected String beginText;
    protected String endText;

    public TokenRangeElement(Grammar grammar, Token token, Token token2, int n) {
        super(grammar, token, n);
        this.begin = this.grammar.tokenManager.getTokenSymbol(token.getText()).getTokenType();
        this.beginText = token.getText();
        this.end = this.grammar.tokenManager.getTokenSymbol(token2.getText()).getTokenType();
        this.endText = token2.getText();
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
        if (this.label != null) {
            return " " + this.label + ":" + this.beginText + ".." + this.endText;
        }
        return " " + this.beginText + ".." + this.endText;
    }
}

