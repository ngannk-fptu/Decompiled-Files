/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.TokenSymbol;

class StringLiteralSymbol
extends TokenSymbol {
    protected String label;

    public StringLiteralSymbol(String string) {
        super(string);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String string) {
        this.label = string;
    }
}

