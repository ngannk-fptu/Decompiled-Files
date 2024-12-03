/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.BlockWithImpliedExitPath;
import antlr.Grammar;
import antlr.Lookahead;
import antlr.Token;

class ZeroOrMoreBlock
extends BlockWithImpliedExitPath {
    public ZeroOrMoreBlock(Grammar grammar) {
        super(grammar);
    }

    public ZeroOrMoreBlock(Grammar grammar, Token token) {
        super(grammar, token);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return super.toString() + "*";
    }
}

