/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;

public interface TokenSource {
    public Token nextToken();

    public int getLine();

    public int getCharPositionInLine();

    public CharStream getInputStream();

    public String getSourceName();

    public void setTokenFactory(TokenFactory<?> var1);

    public TokenFactory<?> getTokenFactory();
}

