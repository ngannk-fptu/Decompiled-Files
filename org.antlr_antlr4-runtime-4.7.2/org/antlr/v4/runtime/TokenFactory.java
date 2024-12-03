/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

public interface TokenFactory<Symbol extends Token> {
    public Symbol create(Pair<TokenSource, CharStream> var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8);

    public Symbol create(int var1, String var2);
}

