/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ParseTree;
import groovyjarjarantlr.ParseTreeToken;
import groovyjarjarantlr.collections.AST;

public class ParseTreeRule
extends ParseTree {
    public static final int INVALID_ALT = -1;
    protected String ruleName;
    protected int altNumber;

    public ParseTreeRule(String string) {
        this(string, -1);
    }

    public ParseTreeRule(String string, int n) {
        this.ruleName = string;
        this.altNumber = n;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    protected int getLeftmostDerivation(StringBuffer stringBuffer, int n) {
        int n2 = 0;
        if (n <= 0) {
            stringBuffer.append(' ');
            stringBuffer.append(this.toString());
            return n2;
        }
        n2 = 1;
        for (AST aST = this.getFirstChild(); aST != null; aST = aST.getNextSibling()) {
            if (n2 >= n || aST instanceof ParseTreeToken) {
                stringBuffer.append(' ');
                stringBuffer.append(((Object)aST).toString());
                continue;
            }
            int n3 = n - n2;
            int n4 = ((ParseTree)aST).getLeftmostDerivation(stringBuffer, n3);
            n2 += n4;
        }
        return n2;
    }

    public String toString() {
        if (this.altNumber == -1) {
            return '<' + this.ruleName + '>';
        }
        return '<' + this.ruleName + "[" + this.altNumber + "]>";
    }
}

