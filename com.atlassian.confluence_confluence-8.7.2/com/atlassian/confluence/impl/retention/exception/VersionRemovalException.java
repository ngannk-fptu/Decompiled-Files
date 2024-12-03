/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.exception;

public class VersionRemovalException
extends RuntimeException {
    private final long originalId;

    public VersionRemovalException(long originalId) {
        this.originalId = originalId;
    }

    public long getOriginalId() {
        return this.originalId;
    }
}

