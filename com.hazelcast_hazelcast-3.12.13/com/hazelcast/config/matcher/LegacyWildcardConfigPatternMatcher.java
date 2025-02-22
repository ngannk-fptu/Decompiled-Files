/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.matcher;

import com.hazelcast.config.ConfigPatternMatcher;

@Deprecated
public class LegacyWildcardConfigPatternMatcher
implements ConfigPatternMatcher {
    @Override
    public String matches(Iterable<String> configPatterns, String itemName) {
        for (String pattern : configPatterns) {
            if (!this.matches(pattern, itemName)) continue;
            return pattern;
        }
        return null;
    }

    public boolean matches(String pattern, String itemName) {
        int index = pattern.indexOf(42);
        if (index == -1) {
            return itemName.equals(pattern);
        }
        String firstPart = pattern.substring(0, index);
        int indexFirstPart = itemName.indexOf(firstPart, 0);
        if (indexFirstPart == -1) {
            return false;
        }
        String secondPart = pattern.substring(index + 1);
        int indexSecondPart = itemName.indexOf(secondPart, index + 1);
        return indexSecondPart != -1;
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

