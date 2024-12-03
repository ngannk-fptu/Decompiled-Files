/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.CommonAST;
import groovyjarjarantlr.CommonHiddenStreamToken;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.AST;

public class CommonASTWithHiddenTokens
extends CommonAST {
    protected CommonHiddenStreamToken hiddenBefore;
    protected CommonHiddenStreamToken hiddenAfter;

    public CommonASTWithHiddenTokens() {
    }

    public CommonASTWithHiddenTokens(Token token) {
        super(token);
    }

    public CommonHiddenStreamToken getHiddenAfter() {
        return this.hiddenAfter;
    }

    public CommonHiddenStreamToken getHiddenBefore() {
        return this.hiddenBefore;
    }

    public void initialize(AST aST) {
        this.hiddenBefore = ((CommonASTWithHiddenTokens)aST).getHiddenBefore();
        this.hiddenAfter = ((CommonASTWithHiddenTokens)aST).getHiddenAfter();
        super.initialize(aST);
    }

    public void initialize(Token token) {
        CommonHiddenStreamToken commonHiddenStreamToken = (CommonHiddenStreamToken)token;
        super.initialize(commonHiddenStreamToken);
        this.hiddenBefore = commonHiddenStreamToken.getHiddenBefore();
        this.hiddenAfter = commonHiddenStreamToken.getHiddenAfter();
    }
}

