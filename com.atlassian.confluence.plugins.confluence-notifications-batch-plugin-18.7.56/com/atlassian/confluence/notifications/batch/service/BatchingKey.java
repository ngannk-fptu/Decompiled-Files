/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.service;

import java.util.Objects;

public class BatchingKey {
    public static final BatchingKey NO_BATCHING = new BatchingKey(null, null);
    private final String key;
    private final String contentType;

    public BatchingKey(String key, String contentType) {
        this.key = key;
        this.contentType = contentType;
    }

    public String getKey() {
        return this.key;
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BatchingKey that = (BatchingKey)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.contentType, that.contentType);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.contentType);
    }
}

