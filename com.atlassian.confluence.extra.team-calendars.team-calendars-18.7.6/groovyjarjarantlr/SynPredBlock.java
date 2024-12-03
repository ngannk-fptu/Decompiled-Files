/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

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

