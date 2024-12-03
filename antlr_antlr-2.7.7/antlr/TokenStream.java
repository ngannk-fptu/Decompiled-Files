/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Token;
import antlr.TokenStreamException;

public interface TokenStream {
    public Token nextToken() throws TokenStreamException;
}

