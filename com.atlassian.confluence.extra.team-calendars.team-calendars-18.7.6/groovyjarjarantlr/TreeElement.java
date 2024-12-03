/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Alternative;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

class TreeElement
extends AlternativeBlock {
    GrammarAtom root;

    public TreeElement(Grammar grammar, Token token) {
        super(grammar, token, false);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public String toString() {
        String string = " #(" + this.root;
        Alternative alternative = (Alternative)this.alternatives.elementAt(0);
        AlternativeElement alternativeElement = alternative.head;
        while (alternativeElement != null) {
            string = string + alternativeElement;
            alternativeElement = alternativeElement.next;
        }
        return string + " )";
    }
}

