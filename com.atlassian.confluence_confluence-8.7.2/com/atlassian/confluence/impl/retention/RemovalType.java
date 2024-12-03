/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention;

public enum RemovalType {
    SOFT("soft"),
    HARD("hard");

    private final String type;

    private RemovalType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.type;
    }
}

