/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.entity;

import com.atlassian.audit.entity.CoverageLevel;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EffectiveCoverageLevel {
    OFF("off", 0),
    BASE("base", 1),
    ADVANCED("advanced", 2),
    FULL("full", 3);

    private static final Map<CoverageLevel, Integer> eventLevel;
    private final String key;
    private final int effectiveLevel;

    private EffectiveCoverageLevel(String key, int effectiveLevel) {
        this.key = key;
        this.effectiveLevel = effectiveLevel;
    }

    public EffectiveCoverageLevel mostRestrictive(EffectiveCoverageLevel other) {
        if (this.effectiveLevel < other.effectiveLevel) {
            return this;
        }
        return other;
    }

    public boolean shouldAllow(CoverageLevel entityLevel) {
        return this.effectiveLevel >= eventLevel.get((Object)entityLevel);
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        return this.key;
    }

    public static EffectiveCoverageLevel fromKey(String key) {
        return Stream.of(EffectiveCoverageLevel.values()).filter(c -> c.key.equals(key)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + key));
    }

    static {
        eventLevel = Stream.of(new AbstractMap.SimpleEntry<CoverageLevel, Integer>(CoverageLevel.BASE, 1), new AbstractMap.SimpleEntry<CoverageLevel, Integer>(CoverageLevel.ADVANCED, 2), new AbstractMap.SimpleEntry<CoverageLevel, Integer>(CoverageLevel.FULL, 3)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

