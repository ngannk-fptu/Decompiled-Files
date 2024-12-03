/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Alternative;
import antlr.AlternativeBlock;
import antlr.AlternativeElement;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.Lookahead;
import antlr.Token;

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

