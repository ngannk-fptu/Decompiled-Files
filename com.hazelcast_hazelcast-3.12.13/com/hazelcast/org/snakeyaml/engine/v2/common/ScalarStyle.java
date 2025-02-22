/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.common;

import java.util.Optional;

public enum ScalarStyle {
    DOUBLE_QUOTED(Optional.of(Character.valueOf('\"'))),
    SINGLE_QUOTED(Optional.of(Character.valueOf('\''))),
    LITERAL(Optional.of(Character.valueOf('|'))),
    FOLDED(Optional.of(Character.valueOf('>'))),
    PLAIN(Optional.empty());

    private Optional<Character> styleOpt;

    private ScalarStyle(Optional<Character> style) {
        this.styleOpt = style;
    }

    public String toString() {
        if (this.styleOpt.isPresent()) {
            return String.valueOf(this.styleOpt.get());
        }
        return ":";
    }
}

