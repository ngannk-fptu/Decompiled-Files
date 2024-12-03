/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.BlockEndElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;

class RuleEndElement
extends BlockEndElement {
    protected Lookahead[] cache;
    protected boolean noFOLLOW;

    public RuleEndElement(Grammar grammar) {
        super(grammar);
        this.cache = new Lookahead[grammar.maxk + 1];
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        return "";
    }
}

