/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.entity.EffectiveCoverageLevel;
import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;

public enum EffectiveCoverageLevelJson {
    OFF("off", EffectiveCoverageLevel.OFF),
    BASE("base", EffectiveCoverageLevel.BASE),
    ADVANCED("advanced", EffectiveCoverageLevel.ADVANCED),
    FULL("full", EffectiveCoverageLevel.FULL);

    private final String key;
    private final EffectiveCoverageLevel correspondingLevel;

    private EffectiveCoverageLevelJson(String key, EffectiveCoverageLevel correspondingLevel) {
        this.key = key;
        this.correspondingLevel = correspondingLevel;
    }

    @JsonValue
    public String toString() {
        return this.key;
    }

    @JsonIgnore
    public EffectiveCoverageLevel toEffectiveCoverageLevel() {
        return this.correspondingLevel;
    }

    @JsonCreator
    public static EffectiveCoverageLevelJson fromKey(String key) {
        return Stream.of(EffectiveCoverageLevelJson.values()).filter(c -> c.key.equals(key)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + key));
    }

    public static EffectiveCoverageLevelJson fromCoverageLevel(EffectiveCoverageLevel level) {
        return Stream.of(EffectiveCoverageLevelJson.values()).filter(c -> c.correspondingLevel.equals((Object)level)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + level));
    }
}

