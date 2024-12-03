/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Token;

class ExceptionHandler {
    protected Token exceptionTypeAndName;
    protected Token action;

    public ExceptionHandler(Token token, Token token2) {
        this.exceptionTypeAndName = token;
        this.action = token2;
    }
}

