/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package com.atlassian.data.activeobjects.repository.query;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;

public final class EscapeCharacter {
    public static final EscapeCharacter DEFAULT = EscapeCharacter.of('\\');
    private static final List<String> TO_REPLACE = Arrays.asList("_", "%");
    private final char escapeCharacter;

    @Nullable
    public String escape(@Nullable String value) {
        return value == null ? null : Stream.concat(Stream.of(String.valueOf(this.escapeCharacter)), TO_REPLACE.stream()).reduce(value, (it, character) -> it.replace((CharSequence)character, this.escapeCharacter + character));
    }

    private EscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public static EscapeCharacter of(char escapeCharacter) {
        return new EscapeCharacter(escapeCharacter);
    }

    public char getEscapeCharacter() {
        return this.escapeCharacter;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EscapeCharacter)) {
            return false;
        }
        EscapeCharacter other = (EscapeCharacter)o;
        return this.getEscapeCharacter() == other.getEscapeCharacter();
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getEscapeCharacter();
        return result;
    }

    public String toString() {
        return "EscapeCharacter(escapeCharacter=" + this.getEscapeCharacter() + ")";
    }
}

