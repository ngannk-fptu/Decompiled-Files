/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.BlockWithImpliedExitPath;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

class OneOrMoreBlock
extends BlockWithImpliedExitPath {
    public OneOrMoreBlock(Grammar grammar) {
        super(grammar);
    }

    public OneOrMoreBlock(Grammar grammar, Token token) {
        super(grammar, token);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return super.toString() + "+";
    }
}

