/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeBlock;
import antlr.Grammar;
import antlr.Lookahead;
import antlr.Token;

abstract class BlockWithImpliedExitPath
extends AlternativeBlock {
    protected int exitLookaheadDepth;
    protected Lookahead[] exitCache;

    public BlockWithImpliedExitPath(Grammar grammar) {
        super(grammar);
        this.exitCache = new Lookahead[this.grammar.maxk + 1];
    }

    public BlockWithImpliedExitPath(Grammar grammar, Token token) {
        super(grammar, token, false);
        this.exitCache = new Lookahead[this.grammar.maxk + 1];
    }
}

