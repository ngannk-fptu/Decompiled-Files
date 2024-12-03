/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Optional;

public final class FlowSequenceStartToken
extends Token {
    public FlowSequenceStartToken(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.FlowSequenceStart;
    }
}

