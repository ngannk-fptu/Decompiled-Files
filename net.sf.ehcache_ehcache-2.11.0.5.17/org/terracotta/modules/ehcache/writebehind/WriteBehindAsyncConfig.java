/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.writebehind;

import org.terracotta.modules.ehcache.async.AsyncConfig;

public class WriteBehindAsyncConfig
implements AsyncConfig {
    private final long workDelay;
    private final long maxFallBehind;
    private final boolean batchingEnabled;
    private final int batchSize;
    private final boolean synchronousWrite;
    private final int retryAttempts;
    private final long retryAttemptDelay;
    private final int rateLimit;
    private final int maxQueueSize;

    public WriteBehindAsyncConfig(long workDelay, long maxAllowedFallBehind, boolean batchingEnabled, int batchSize, boolean synchronousWrite, int retryAttempts, long retryAttemptDelay, int rateLimit, int maxQueueSize) {
        this.workDelay = workDelay;
        this.maxFallBehind = maxAllowedFallBehind;
        this.batchingEnabled = batchingEnabled;
        this.batchSize = batchSize;
        this.synchronousWrite = synchronousWrite;
        this.retryAttempts = retryAttempts;
        this.retryAttemptDelay = retryAttemptDelay;
        this.rateLimit = rateLimit;
        this.maxQueueSize = maxQueueSize;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.batchSize;
        result = 31 * result + (this.batchingEnabled ? 1231 : 1237);
        result = 31 * result + (int)(this.maxFallBehind ^ this.maxFallBehind >>> 32);
        result = 31 * result + this.maxQueueSize;
        result = 31 * result + this.rateLimit;
        result = 31 * result + (int)(this.retryAttemptDelay ^ this.retryAttemptDelay >>> 32);
        result = 31 * result + this.retryAttempts;
        result = 31 * result + (this.synchronousWrite ? 1231 : 1237);
        result = 31 * result + (int)(this.workDelay ^ this.workDelay >>> 32);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        WriteBehindAsyncConfig other = (WriteBehindAsyncConfig)obj;
        if (this.batchSize != other.batchSize) {
            return false;
        }
        if (this.batchingEnabled != other.batchingEnabled) {
            return false;
        }
        if (this.maxFallBehind != other.maxFallBehind) {
            return false;
        }
        if (this.maxQueueSize != other.maxQueueSize) {
            return false;
        }
        if (this.rateLimit != other.rateLimit) {
            return false;
        }
        if (this.retryAttemptDelay != other.retryAttemptDelay) {
            return false;
        }
        if (this.retryAttempts != other.retryAttempts) {
            return false;
        }
        if (this.synchronousWrite != other.synchronousWrite) {
            return false;
        }
        return this.workDelay == other.workDelay;
    }

    public String toString() {
        return "WriteBehindAsyncConfig [workDelay=" + this.workDelay + ", maxFallBehind=" + this.maxFallBehind + ", batchingEnabled=" + this.batchingEnabled + ", batchSize=" + this.batchSize + ", synchronousWrite=" + this.synchronousWrite + ", retryAttempts=" + this.retryAttempts + ", retryAttemptDelay=" + this.retryAttemptDelay + ", rateLimit=" + this.rateLimit + ", maxQueueSize=" + this.maxQueueSize + "]";
    }

    @Override
    public long getWorkDelay() {
        return this.workDelay;
    }

    @Override
    public long getMaxAllowedFallBehind() {
        return this.maxFallBehind;
    }

    public boolean isStealingEnabled() {
        return false;
    }

    @Override
    public boolean isBatchingEnabled() {
        return this.batchingEnabled;
    }

    @Override
    public int getBatchSize() {
        return this.batchSize;
    }

    @Override
    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

    @Override
    public boolean isSynchronousWrite() {
        return this.synchronousWrite;
    }

    @Override
    public int getRetryAttempts() {
        return this.retryAttempts;
    }

    @Override
    public long getRetryAttemptDelay() {
        return this.retryAttemptDelay;
    }

    @Override
    public int getRateLimit() {
        return this.rateLimit;
    }
}

