/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeBlock;
import antlr.Grammar;
import antlr.Lookahead;
import antlr.Token;

class SynPredBlock
extends AlternativeBlock {
    public SynPredBlock(Grammar grammar) {
        super(grammar);
    }

    public SynPredBlock(Grammar grammar, Token token) {
        super(grammar, token, false);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return super.toString() + "=>";
    }
}

