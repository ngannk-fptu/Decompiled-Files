/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.pagination;

public enum CursorType {
    SPACE("space"),
    CONTENT("content");

    private final String type;

    private CursorType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

