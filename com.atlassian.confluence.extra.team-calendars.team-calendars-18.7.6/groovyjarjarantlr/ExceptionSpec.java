/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ExceptionHandler;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.impl.Vector;

class ExceptionSpec {
    protected Token label;
    protected Vector handlers;

    public ExceptionSpec(Token token) {
        this.label = token;
        this.handlers = new Vector();
    }

    public void addHandler(ExceptionHandler exceptionHandler) {
        this.handlers.appendElement(exceptionHandler);
    }
}

