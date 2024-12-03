/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStreamException;

public interface TokenStream {
    public Token nextToken() throws TokenStreamException;
}

