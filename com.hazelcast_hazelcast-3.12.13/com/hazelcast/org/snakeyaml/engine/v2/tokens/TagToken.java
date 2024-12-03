/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.TagTuple;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Objects;
import java.util.Optional;

public final class TagToken
extends Token {
    private final TagTuple value;

    public TagToken(TagTuple value, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(value);
        this.value = value;
    }

    public TagTuple getValue() {
        return this.value;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Tag;
    }
}

