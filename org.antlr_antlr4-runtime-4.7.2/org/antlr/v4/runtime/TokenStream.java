/*
 * Decompiled with CFR 0.152.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Interval;

public interface TokenStream
extends IntStream {
    public Token LT(int var1);

    public Token get(int var1);

    public TokenSource getTokenSource();

    public String getText(Interval var1);

    public String getText();

    public String getText(RuleContext var1);

    public String getText(Token var1, Token var2);
}

