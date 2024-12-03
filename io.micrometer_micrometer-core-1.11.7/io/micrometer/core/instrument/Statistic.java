/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument;

public enum Statistic {
    TOTAL("total"),
    TOTAL_TIME("total"),
    COUNT("count"),
    MAX("max"),
    VALUE("value"),
    UNKNOWN("unknown"),
    ACTIVE_TASKS("active"),
    DURATION("duration");

    private final String tagValueRepresentation;

    private Statistic(String tagValueRepresentation) {
        this.tagValueRepresentation = tagValueRepresentation;
    }

    public String getTagValueRepresentation() {
        return this.tagValueRepresentation;
    }
}

