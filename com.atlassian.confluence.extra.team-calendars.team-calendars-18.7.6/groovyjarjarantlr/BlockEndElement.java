/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;

class BlockEndElement
extends AlternativeElement {
    protected boolean[] lock;
    protected AlternativeBlock block;

    public BlockEndElement(Grammar grammar) {
        super(grammar);
        this.lock = new boolean[grammar.maxk + 1];
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return "";
    }
}

