/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.GrammarSymbol;

class TokenSymbol
extends GrammarSymbol {
    protected int ttype = 0;
    protected String paraphrase = null;
    protected String ASTNodeType;

    public TokenSymbol(String string) {
        super(string);
    }

    public String getASTNodeType() {
        return this.ASTNodeType;
    }

    public void setASTNodeType(String string) {
        this.ASTNodeType = string;
    }

    public String getParaphrase() {
        return this.paraphrase;
    }

    public int getTokenType() {
        return this.ttype;
    }

    public void setParaphrase(String string) {
        this.paraphrase = string;
    }

    public void setTokenType(int n) {
        this.ttype = n;
    }
}

