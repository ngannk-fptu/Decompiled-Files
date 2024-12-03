/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.matcher;

import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.ConfigurationException;

@Deprecated
public class LegacyMatchingPointConfigPatternMatcher
implements ConfigPatternMatcher {
    @Override
    public String matches(Iterable<String> configPatterns, String itemName) throws ConfigurationException {
        String key = null;
        int lastMatchingPoint = -1;
        for (String pattern : configPatterns) {
            int matchingPoint = this.getMatchingPoint(pattern, itemName);
            if (matchingPoint <= lastMatchingPoint) continue;
            lastMatchingPoint = matchingPoint;
            key = pattern;
        }
        return key;
    }

    private int getMatchingPoint(String pattern, String itemName) {
        int index = pattern.indexOf(42);
        if (index == -1) {
            return -1;
        }
        String firstPart = pattern.substring(0, index);
        int indexFirstPart = itemName.indexOf(firstPart, 0);
        if (indexFirstPart == -1) {
            return -1;
        }
        String secondPart = pattern.substring(index + 1);
        int indexSecondPart = itemName.indexOf(secondPart, index + 1);
        if (indexSecondPart == -1) {
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

