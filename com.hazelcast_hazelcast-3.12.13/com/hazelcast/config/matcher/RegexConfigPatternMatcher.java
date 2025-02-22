/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.matcher;

import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;
import java.util.regex.Pattern;

public class RegexConfigPatternMatcher
implements ConfigPatternMatcher {
    private final int flags;

    public RegexConfigPatternMatcher() {
        this(0);
    }

    public RegexConfigPatternMatcher(int flags) {
        this.flags = flags;
    }

    @Override
    public String matches(Iterable<String> configPatterns, String itemName) throws ConfigurationException {
        String candidate = null;
        for (String pattern : configPatterns) {
            if (!Pattern.compile(pattern, this.flags).matcher(itemName).find()) continue;
            if (candidate != null) {
                throw new ConfigurationException(itemName, candidate, pattern);
            }
            candidate = pattern;
        }
        return candidate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RegexConfigPatternMatcher that = (RegexConfigPatternMatcher)o;
        return this.flags == that.flags;
    }

    public int hashCode() {
        return this.flags;
    }
}

