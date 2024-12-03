/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CommonToken;

public class CommonHiddenStreamToken
extends CommonToken {
    protected CommonHiddenStreamToken hiddenBefore;
    protected CommonHiddenStreamToken hiddenAfter;

    public CommonHiddenStreamToken() {
    }

    public CommonHiddenStreamToken(int n, String string) {
        super(n, string);
    }

    public CommonHiddenStreamToken(String string) {
        super(string);
    }

    public CommonHiddenStreamToken getHiddenAfter() {
        return this.hiddenAfter;
    }

    public CommonHiddenStreamToken getHiddenBefore() {
        return this.hiddenBefore;
    }

    protected void setHiddenAfter(CommonHiddenStreamToken commonHiddenStreamToken) {
        this.hiddenAfter = commonHiddenStreamToken;
    }

    protected void setHiddenBefore(CommonHiddenStreamToken commonHiddenStreamToken) {
        this.hiddenBefore = commonHiddenStreamToken;
    }
}

