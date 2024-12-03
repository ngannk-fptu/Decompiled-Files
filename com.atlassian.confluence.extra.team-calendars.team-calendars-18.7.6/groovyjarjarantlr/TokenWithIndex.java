/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.CommonToken;

public class TokenWithIndex
extends CommonToken {
    int index;

    public TokenWithIndex() {
    }

    public TokenWithIndex(int n, String string) {
        super(n, string);
    }

    public void setIndex(int n) {
        this.index = n;
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return "[" + this.index + ":\"" + this.getText() + "\",<" + this.getType() + ">,line=" + this.line + ",col=" + this.col + "]\n";
    }
}

