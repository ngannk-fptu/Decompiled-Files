/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.scanner;

import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Iterator;

public interface Scanner
extends Iterator<Token> {
    public boolean checkToken(Token.ID ... var1);

    public Token peekToken();

    @Override
    public Token next();
}

