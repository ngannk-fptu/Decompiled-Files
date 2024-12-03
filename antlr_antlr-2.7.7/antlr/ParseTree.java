/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.BaseAST;
import antlr.Token;
import antlr.collections.AST;

public abstract class ParseTree
extends BaseAST {
    public String getLeftmostDerivationStep(int n) {
        if (n <= 0) {
            return this.toString();
        }
        StringBuffer stringBuffer = new StringBuffer(2000);
        this.getLeftmostDerivation(stringBuffer, n);
        return stringBuffer.toString();
    }

    public String getLeftmostDerivation(int n) {
        StringBuffer stringBuffer = new StringBuffer(2000);
        stringBuffer.append("    " + this.toString());
        stringBuffer.append("\n");
        for (int i = 1; i < n; ++i) {
            stringBuffer.append(" =>");
            stringBuffer.append(this.getLeftmostDerivationStep(i));
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }

    protected abstract int getLeftmostDerivation(StringBuffer var1, int var2);

    public void initialize(int n, String string) {
    }

    public void initialize(AST aST) {
    }

    public void initialize(Token token) {
    }
}

