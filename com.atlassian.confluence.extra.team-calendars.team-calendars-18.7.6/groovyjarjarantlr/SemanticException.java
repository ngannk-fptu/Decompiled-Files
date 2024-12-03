/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.RecognitionException;

public class SemanticException
extends RecognitionException {
    public SemanticException(String string) {
        super(string);
    }

    public SemanticException(String string, String string2, int n) {
        this(string, string2, n, -1);
    }

    public SemanticException(String string, String string2, int n, int n2) {
        super(string, string2, n, n2);
    }
}

