/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Optional;

public final class AliasToken
extends Token {
    private final Anchor value;

    public AliasToken(Anchor value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        if (value == null) {
            throw new NullPointerException("Value is required in AliasToken");
        }
        this.value = value;
    }

    public Anchor getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Alias;
    }
}

