/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

public enum BundlePriority {
    DEFAULT(0, ""),
    RECOMMENDED(1, "stp.bundle.priority.recommended"),
    HIGHLY_RECOMMENDED(2, "stp.bundle.priority.highly.recommended"),
    REQUIRED(3, "stp.bundle.priority.required");

    private final int priority;
    private final String key;

    private BundlePriority(int priority, String key) {
        this.priority = priority;
        this.key = key;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getPriorityKey() {
        return this.key;
    }
}

