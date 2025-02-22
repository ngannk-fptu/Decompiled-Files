/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigurationException;
import com.hazelcast.util.Preconditions;

public class IcmpFailureDetectorConfig {
    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 1000;
    private static final int DEFAULT_INTERVAL_MILLISECONDS = 1000;
    private static final int MIN_INTERVAL_MILLIS = 1000;
    private static final int DEFAULT_TTL = 255;
    private static final int DEFAULT_MAX_ATTEMPT = 2;
    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_PARALLEL_MODE = true;
    private int timeoutMilliseconds = 1000;
    private int intervalMilliseconds = 1000;
    private boolean failFastOnStartup = true;
    private int ttl = 255;
    private int maxAttempts = 2;
    private boolean enabled = false;
    private boolean parallelMode = true;

    public int getTimeoutMilliseconds() {
        return this.timeoutMilliseconds;
    }

    public IcmpFailureDetectorConfig setTimeoutMilliseconds(int timeoutMilliseconds) {
        Preconditions.checkPositive(timeoutMilliseconds, "Timeout must be a positive value");
        this.timeoutMilliseconds = timeoutMilliseconds;
        return this;
    }

    public int getIntervalMilliseconds() {
        return this.intervalMilliseconds;
    }

    public IcmpFailureDetectorConfig setIntervalMilliseconds(int intervalMilliseconds) {
        if (intervalMilliseconds < 1000) {
            throw new ConfigurationException(String.format("Interval can't be set to less than %d milliseconds.", 1000));
        }
        this.intervalMilliseconds = intervalMilliseconds;
        return this;
    }

    public boolean isFailFastOnStartup() {
        return this.failFastOnStartup;
    }

    public IcmpFailureDetectorConfig setFailFastOnStartup(boolean failFastOnStartup) {
        this.failFastOnStartup = failFastOnStartup;
        return this;
    }

    public int getTtl() {
        return this.ttl;
    }

    public IcmpFailureDetectorConfig setTtl(int ttl) {
        Preconditions.checkNotNegative(ttl, "TTL must not be a negative value");
        this.ttl = ttl;
        return this;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }

    public IcmpFailureDetectorConfig setMaxAttempts(int maxAttempts) {
        Preconditions.checkNotNegative(maxAttempts, "Max attempts must not be a negative value");
        this.maxAttempts = maxAttempts;
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public IcmpFailureDetectorConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isParallelMode() {
        return this.parallelMode;
    }

    public IcmpFailureDetectorConfig setParallelMode(boolean mode) {
        this.parallelMode = mode;
        return this;
    }

    public String toString() {
        return "IcmpFailureDetectorConfig{timeoutMilliseconds=" + this.timeoutMilliseconds + ", intervalMilliseconds=" + this.intervalMilliseconds + ", echoFailFastOnStartup=" + this.failFastOnStartup + ", ttl=" + this.ttl + ", maxAttempts=" + this.maxAttempts + ", enabled=" + this.enabled + ", parallelMode=" + this.parallelMode + '}';
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof IcmpFailureDetectorConfig)) {
            return false;
        }
        IcmpFailureDetectorConfig that = (IcmpFailureDetectorConfig)o;
        if (this.timeoutMilliseconds != that.timeoutMilliseconds) {
            return false;
        }
        if (this.intervalMilliseconds != that.intervalMilliseconds) {
            return false;
        }
        if (this.failFastOnStartup != that.failFastOnStartup) {
            return false;
        }
        if (this.ttl != that.ttl) {
            return false;
        }
        if (this.maxAttempts != that.maxAttempts) {
            return false;
        }
        if (this.enabled != that.enabled) {
            return false;
        }
        return this.parallelMode == that.parallelMode;
    }

    public final int hashCode() {
        int result = this.timeoutMilliseconds;
        result = 31 * result + this.intervalMilliseconds;
        result = 31 * result + (this.failFastOnStartup ? 1 : 0);
        result = 31 * result + this.ttl;
        result = 31 * result + this.maxAttempts;
        result = 31 * result + (this.enabled ? 1 : 0);
        result = 31 * result + (this.parallelMode ? 1 : 0);
        return result;
    }
}

