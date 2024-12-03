/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.LexerGrammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenSymbol;

class StringLiteralElement
extends GrammarAtom {
    protected String processedAtomText;

    public StringLiteralElement(Grammar grammar, Token token, int n) {
        super(grammar, token, n);
        if (!(grammar instanceof LexerGrammar)) {
            TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.atomText);
            if (tokenSymbol == null) {
                grammar.antlrTool.error("Undefined literal: " + this.atomText, this.grammar.getFilename(), token.getLine(), token.getColumn());
            } else {
                this.tokenType = tokenSymbol.getTokenType();
            }
        }
        this.line = token.getLine();
        this.processedAtomText = new String();
        for (int i = 1; i < this.atomText.length() - 1; ++i) {
            int n2 = this.atomText.charAt(i);
            if (n2 == 92 && i + 1 < this.atomText.length() - 1) {
                n2 = this.atomText.charAt(++i);
                switch (n2) {
                    case 110: {
                        n2 = 10;
                        break;
                    }
                    case 114: {
                        n2 = 13;
                        break;
                    }
                    case 116: {
                        n2 = 9;
                    }
                }
            }
            if (grammar instanceof LexerGrammar) {
                ((LexerGrammar)grammar).charVocabulary.add(n2);
            }
            this.processedAtomText = this.processedAtomText + (char)n2;
        }
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}

