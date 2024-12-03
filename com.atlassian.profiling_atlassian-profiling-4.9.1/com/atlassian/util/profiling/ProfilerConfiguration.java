/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 */
package com.atlassian.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.Ticker;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@Internal
public class ProfilerConfiguration {
    private final ThreadLocal<Boolean> enabledForThread = new ThreadLocal();
    private int maxFramesPerTrace;
    private int maxFrameNameLength;
    private long minFrameTimeNanos;
    private long minTraceTimeNanos;
    private boolean enabled = "true".equalsIgnoreCase(System.getProperty("atlassian.profile.activate", "false"));
    private boolean memoryProfiled;

    public ProfilerConfiguration() {
        this.maxFrameNameLength = Integer.getInteger("atlassian.profile.maxframelength", 150);
        this.maxFramesPerTrace = Integer.getInteger("atlassian.profile.maxframecount", 1000);
        this.memoryProfiled = "true".equalsIgnoreCase(System.getProperty("atlassian.profile.activate.memory", "false"));
        this.minFrameTimeNanos = TimeUnit.MILLISECONDS.toNanos(Long.getLong("atlassian.profile.mintime", 0L));
        this.minTraceTimeNanos = TimeUnit.MILLISECONDS.toNanos(Long.getLong("atlassian.profile.mintotaltime", 0L));
    }

    @Nonnull
    public Ticker enableForThread() {
        this.enabledForThread.set(true);
        return this.enabledForThread::remove;
    }

    public int getMaxFramesPerTrace() {
        return this.maxFramesPerTrace;
    }

    public int getMaxFrameNameLength() {
        return this.maxFrameNameLength;
    }

    public long getMinFrameTime(@Nonnull TimeUnit timeUnit) {
        return Objects.requireNonNull(timeUnit, "timeUnit").convert(this.minFrameTimeNanos, TimeUnit.NANOSECONDS);
    }

    public long getMinTraceTime(@Nonnull TimeUnit timeUnit) {
        return Objects.requireNonNull(timeUnit, "timeUnit").convert(this.minTraceTimeNanos, TimeUnit.NANOSECONDS);
    }

    public boolean isEnabled() {
        if (this.enabled || this.enabledForThread.get() == Boolean.TRUE) {
            return true;
        }
        this.enabledForThread.remove();
        return false;
    }

    public boolean isMemoryProfilingEnabled() {
        return this.memoryProfiled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setMaxFramesPerTrace(int value) {
        this.maxFramesPerTrace = value;
    }

    public void setMaxFrameNameLength(int value) {
        this.maxFrameNameLength = value;
    }

    public void setMemoryProfilingEnabled(boolean value) {
        this.memoryProfiled = value;
    }

    public void setMinFrameTime(long value, @Nonnull TimeUnit timeUnit) {
        this.minFrameTimeNanos = timeUnit.toNanos(value);
    }

    public void setMinTraceTime(long value, @Nonnull TimeUnit timeUnit) {
        this.minTraceTimeNanos = timeUnit.toNanos(value);
    }
}

