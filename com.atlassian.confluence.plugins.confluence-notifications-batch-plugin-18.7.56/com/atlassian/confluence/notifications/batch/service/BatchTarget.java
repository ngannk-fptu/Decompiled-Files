/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.service;

public class BatchTarget {
    public static final int CONTENT = 0;
    public static final int COMMENT = 1;
    private final String contentId;
    private final int weight;

    public BatchTarget(String contentId, int weight) {
        this.contentId = contentId;
        this.weight = weight;
    }

    public BatchTarget() {
        this(null, -1);
    }

    public String getContentId() {
        return this.contentId;
    }

    public int getWeight() {
        return this.weight;
    }
}

