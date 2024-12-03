/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.audit.rest.model;

import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum CoverageLevelJson {
    OFF("off"),
    BASIC("basic"),
    CRITICAL("critical"),
    FULL("full");

    private final String key;

    private CoverageLevelJson(String key) {
        this.key = key;
    }

    @JsonValue
    public String toString() {
        return this.key;
    }

    @JsonCreator
    public static CoverageLevelJson fromKey(String key) {
        return Stream.of(CoverageLevelJson.values()).filter(c -> c.key.equals(key)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + key));
    }
}

