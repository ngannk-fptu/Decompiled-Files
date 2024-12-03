/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async;

import java.util.concurrent.TimeUnit;
import org.terracotta.modules.ehcache.async.AsyncConfig;

public class DefaultAsyncConfig
implements AsyncConfig {
    private static final AsyncConfig INSTANCE = new DefaultAsyncConfig();
    public static final long WORK_DELAY = TimeUnit.SECONDS.toMillis(1L);
    public static final long MAX_ALLOWED_FALLBEHIND = TimeUnit.SECONDS.toMillis(2L);
    public static final int BATCH_SIZE = 1;
    public static final boolean BATCHING_ENABLED = false;
    public static final boolean SYNCHRONOUS_WRITE = false;
    public static final int RETRY_ATTEMPTS = 0;
    public static final long RETRY_ATTEMPT_DELAY = TimeUnit.SECONDS.toMillis(1L);
    public static final int RATE_LIMIT = 0;
    public static final int MAX_QUEUE_SIZE = 0;

    public static AsyncConfig getInstance() {
        return INSTANCE;
    }

    protected DefaultAsyncConfig() {
    }

    @Override
    public long getWorkDelay() {
        return WORK_DELAY;
    }

    @Override
    public long getMaxAllowedFallBehind() {
        return MAX_ALLOWED_FALLBEHIND;
    }

    @Override
    public int getBatchSize() {
        return 1;
    }

    @Override
    public boolean isBatchingEnabled() {
        return false;
    }

    @Override
    public boolean isSynchronousWrite() {
        return false;
    }

    @Override
    public int getRetryAttempts() {
        return 0;
    }

    @Override
    public long getRetryAttemptDelay() {
        return RETRY_ATTEMPT_DELAY;
    }

    @Override
    public int getRateLimit() {
        return 0;
    }

    @Override
    public int getMaxQueueSize() {
        return 0;
    }
}

