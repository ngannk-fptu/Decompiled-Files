/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.matcher;

import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;

public class WildcardConfigPatternMatcher
implements ConfigPatternMatcher {
    @Override
    public String matches(Iterable<String> configPatterns, String itemName) throws ConfigurationException {
        String candidate = null;
        for (String pattern : configPatterns) {
            if (!this.matches(pattern, itemName)) continue;
            if (candidate != null) {
                throw new ConfigurationException(itemName, candidate, pattern);
            }
            candidate = pattern;
        }
        return candidate;
    }

    public boolean matches(String pattern, String itemName) {
        int index = pattern.indexOf(42);
        if (index == -1) {
            return itemName.equals(pattern);
        }
        String firstPart = pattern.substring(0, index);
        if (!itemName.startsWith(firstPart)) {
            return false;
        }
        String secondPart = pattern.substring(index + 1);
        return itemName.endsWith(secondPart);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return super.hashCode();
    }
}

