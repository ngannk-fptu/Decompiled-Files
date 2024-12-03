/*
 * Decompiled with CFR 0.152.
 */
package antlr;

abstract class GrammarSymbol {
    protected String id;

    public GrammarSymbol() {
    }

    public GrammarSymbol(String string) {
        this.id = string;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String string) {
        this.id = string;
    }
}

