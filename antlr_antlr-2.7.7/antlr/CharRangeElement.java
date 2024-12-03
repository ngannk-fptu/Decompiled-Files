/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRLexer;
import antlr.AlternativeElement;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.Token;

class CharRangeElement
extends AlternativeElement {
    String label;
    protected char begin = '\u0000';
    protected char end = '\u0000';
    protected String beginText;
    protected String endText;

    public CharRangeElement(LexerGrammar lexerGrammar, Token token, Token token2, int n) {
        super(lexerGrammar);
        this.begin = (char)ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        this.beginText = token.getText();
        this.end = (char)ANTLRLexer.tokenTypeForCharLiteral(token2.getText());
        this.endText = token2.getText();
        this.line = token.getLine();
        for (int i = this.begin; i <= this.end; ++i) {
            lexerGrammar.charVocabulary.add(i);
        }
        this.autoGenType = n;
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

