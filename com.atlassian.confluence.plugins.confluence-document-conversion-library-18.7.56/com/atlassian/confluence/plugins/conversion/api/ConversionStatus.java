/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.api;

public enum ConversionStatus {
    CONVERTED(200),
    ERROR(415),
    IN_PROGRESS(202),
    BUSY(429);

    private final int status;

    private ConversionStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}

