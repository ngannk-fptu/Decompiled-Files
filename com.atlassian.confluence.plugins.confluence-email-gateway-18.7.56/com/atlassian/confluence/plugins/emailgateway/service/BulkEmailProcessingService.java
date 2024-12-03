/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.service;

public interface BulkEmailProcessingService {
    public boolean isAvailable();

    public int processInboundEmail();
}

