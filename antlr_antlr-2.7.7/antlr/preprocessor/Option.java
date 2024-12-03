/*
 * Decompiled with CFR 0.152.
 */
package antlr.preprocessor;

import antlr.preprocessor.Grammar;

class Option {
    protected String name;
    protected String rhs;
    protected Grammar enclosingGrammar;

    public Option(String string, String string2, Grammar grammar) {
        this.name = string;
        this.rhs = string2;
        this.setEnclosingGrammar(grammar);
    }

    public Grammar getEnclosingGrammar() {
        return this.enclosingGrammar;
    }

    public String getName() {
        return this.name;
    }

    public String getRHS() {
        return this.rhs;
    }

    public void setEnclosingGrammar(Grammar grammar) {
        this.enclosingGrammar = grammar;
    }

    public void setName(String string) {
        this.name = string;
    }

    public void setRHS(String string) {
        this.rhs = string;
    }

    public String toString() {
        return "\t" + this.name + "=" + this.rhs;
    }
}

