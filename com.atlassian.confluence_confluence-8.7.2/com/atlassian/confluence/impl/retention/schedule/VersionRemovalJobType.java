/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.schedule;

public enum VersionRemovalJobType {
    SOFT("soft"),
    HARD("hard");

    private final String label;

    private VersionRemovalJobType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}

