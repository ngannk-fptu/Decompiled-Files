/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.matcher;

import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;

public class MatchingPointConfigPatternMatcher
implements ConfigPatternMatcher {
    @Override
    public String matches(Iterable<String> configPatterns, String itemName) throws ConfigurationException {
        String candidate = null;
        String duplicate = null;
        int lastMatchingPoint = -1;
        for (String pattern : configPatterns) {
            int matchingPoint = this.getMatchingPoint(pattern, itemName);
            if (matchingPoint <= -1 || matchingPoint < lastMatchingPoint) continue;
            duplicate = matchingPoint == lastMatchingPoint ? candidate : null;
            lastMatchingPoint = matchingPoint;
            candidate = pattern;
        }
        if (duplicate != null) {
            throw new ConfigurationException(itemName, candidate, duplicate);
        }
        return candidate;
    }

    private int getMatchingPoint(String pattern, String itemName) {
        int index = pattern.indexOf(42);
        if (index == -1) {
            return -1;
        }
        String firstPart = pattern.substring(0, index);
        if (!itemName.startsWith(firstPart)) {
            return -1;
        }
        String secondPart = pattern.substring(index + 1);
        if (!itemName.endsWith(secondPart)) {
            return -1;
        }
        return firstPart.length() + secondPart.length();
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

