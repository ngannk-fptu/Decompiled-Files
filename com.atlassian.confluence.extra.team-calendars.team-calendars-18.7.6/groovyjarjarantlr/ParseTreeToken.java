/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ParseTree;
import groovyjarjarantlr.Token;

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

