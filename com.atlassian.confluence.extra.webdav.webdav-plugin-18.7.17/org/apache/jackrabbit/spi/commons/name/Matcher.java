/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.name;

import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.MatchResult;
import org.apache.jackrabbit.spi.commons.name.Pattern;

public final class Matcher {
    private Matcher() {
    }

    public static Path match(Pattern pattern, Path input) {
        return pattern.match(input).getRemainder();
    }

    public static boolean matches(Pattern pattern, Path input) {
        return pattern.match(input).isFullMatch();
    }

    public static MatchResult findMatch(Pattern pattern, Path input) {
        return Matcher.findMatch(pattern, input, 0);
    }

    public static MatchResult findMatch(Pattern pattern, Path input, int pos) {
        int length = input.getLength();
        if (pos < 0 || pos >= length) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        for (int k = pos; k < length; ++k) {
            Path path = input.subPath(k, length);
            MatchResult result = pattern.match(path);
            if (!result.isMatch()) continue;
            return new MatchResult(input, k, result.getMatchLength());
        }
        return null;
    }
}

