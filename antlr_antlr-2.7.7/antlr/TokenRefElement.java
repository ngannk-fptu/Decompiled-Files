/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.Lookahead;
import antlr.Token;
import antlr.TokenSymbol;

class TokenRefElement
extends GrammarAtom {
    public TokenRefElement(Grammar grammar, Token token, boolean bl, int n) {
        super(grammar, token, n);
        this.not = bl;
        TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.atomText);
        if (tokenSymbol == null) {
            grammar.antlrTool.error("Undefined token symbol: " + this.atomText, this.grammar.getFilename(), token.getLine(), token.getColumn());
        } else {
            this.tokenType = tokenSymbol.getTokenType();
            this.setASTNodeType(tokenSymbol.getASTNodeType());
        }
        this.line = token.getLine();
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}

