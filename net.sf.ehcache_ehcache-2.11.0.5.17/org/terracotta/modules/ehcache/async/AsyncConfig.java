/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import java.io.Serializable;

public interface AsyncConfig
extends Serializable {
    public long getWorkDelay();

    public long getMaxAllowedFallBehind();

    public int getBatchSize();

    public boolean isBatchingEnabled();

    public boolean isSynchronousWrite();

    public int getRetryAttempts();

    public long getRetryAttemptDelay();

    public int getRateLimit();

    public int getMaxQueueSize();
}

