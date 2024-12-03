/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRException;

public class TokenStreamException
extends ANTLRException {
    public TokenStreamException() {
    }

    public TokenStreamException(String string) {
        super(string);
    }

    public TokenStreamException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public TokenStreamException(Throwable throwable) {
        super(throwable);
    }
}

