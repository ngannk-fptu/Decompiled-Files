/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.copyspace.api.event;

import java.util.Objects;

public enum ExecutionStage {
    UNKNOWN("Unknown"),
    COPY_PAGE("Page copy"),
    COPY_BLOG_POST("Blog posts copy"),
    COPY_ATTACHMENT("Attachment copy"),
    RELINK("Links rewriting");

    private final String stringValue;

    private ExecutionStage(String stringValue) {
        this.stringValue = Objects.requireNonNull(stringValue);
    }

    public String toString() {
        return this.stringValue;
    }
}

