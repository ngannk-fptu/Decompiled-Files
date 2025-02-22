/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.tokens;

import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Optional;

public final class ScalarToken
extends Token {
    private final String value;
    private final boolean plain;
    private final ScalarStyle style;

    public ScalarToken(String value, boolean plain, Optional<Mark> startMark, Optional<Mark> endMark) {
        this(value, plain, ScalarStyle.PLAIN, startMark, endMark);
    }

    public ScalarToken(String value, boolean plain, ScalarStyle style, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        if (style == null) {
            throw new NullPointerException("Style must be provided.");
        }
        this.style = style;
    }

    public boolean isPlain() {
        return this.plain;
    }

    public String getValue() {
        return this.value;
    }

    public ScalarStyle getStyle() {
        return this.style;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Scalar;
    }
}

