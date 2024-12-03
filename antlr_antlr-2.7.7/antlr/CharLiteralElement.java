/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRLexer;
import antlr.GrammarAtom;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.Token;

class CharLiteralElement
extends GrammarAtom {
    public CharLiteralElement(LexerGrammar lexerGrammar, Token token, boolean bl, int n) {
        super(lexerGrammar, token, 1);
        this.tokenType = ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        lexerGrammar.charVocabulary.add(this.tokenType);
        this.line = token.getLine();
        this.not = bl;
        this.autoGenType = n;
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}

