/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Grammar;
import antlr.GrammarElement;
import antlr.Token;

abstract class AlternativeElement
extends GrammarElement {
    AlternativeElement next;
    protected int autoGenType = 1;
    protected String enclosingRuleName;

    public AlternativeElement(Grammar grammar) {
        super(grammar);
    }

    public AlternativeElement(Grammar grammar, Token token) {
        super(grammar, token);
    }

    public AlternativeElement(Grammar grammar, Token token, int n) {
        super(grammar, token);
        this.autoGenType = n;
    }

    public int getAutoGenType() {
        return this.autoGenType;
    }

    public void setAutoGenType(int n) {
        this.autoGenType = n;
    }

    public String getLabel() {
        return null;
    }

    public void setLabel(String string) {
    }
}

