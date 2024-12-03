/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ParseTree;
import antlr.Token;

public class ParseTreeToken
extends ParseTree {
    protected Token token;

    public ParseTreeToken(Token token) {
        this.token = token;
    }

    protected int getLeftmostDerivation(StringBuffer stringBuffer, int n) {
        stringBuffer.append(' ');
        stringBuffer.append(this.toString());
        return n;
    }

    public String toString() {
        if (this.token != null) {
            return this.token.getText();
        }
        return "<missing token>";
    }
}

