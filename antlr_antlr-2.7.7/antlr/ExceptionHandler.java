/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Token;

class ExceptionHandler {
    protected Token exceptionTypeAndName;
    protected Token action;

    public ExceptionHandler(Token token, Token token2) {
        this.exceptionTypeAndName = token;
        this.action = token2;
    }
}

