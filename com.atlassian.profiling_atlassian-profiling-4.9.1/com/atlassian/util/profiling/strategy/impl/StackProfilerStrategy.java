/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling.strategy.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.strategy.ProfilerStrategy;
import com.atlassian.util.profiling.strategy.impl.ProfilingFrame;
import com.atlassian.util.profiling.strategy.impl.ProfilingTrace;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class StackProfilerStrategy
implements ProfilerStrategy {
    private static final Logger log = LoggerFactory.getLogger(Timers.class);
    private final ThreadLocal<ProfilingTrace> current = new ThreadLocal();
    private ProfilerConfiguration config;
    private Logger logger = log;

    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void onRequestEnd() {
        try {
            ProfilingTrace trace = this.current.get();
            if (trace != null) {
                trace.closeAbnormally();
            }
        }
        finally {
            this.current.remove();
        }
    }

    @Override
    public void setConfiguration(@Nonnull ProfilerConfiguration configuration) {
        this.config = Objects.requireNonNull(configuration, "configuration");
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    @Nonnull
    public Ticker start(@Nonnull String name) {
        Objects.requireNonNull(name, "name");
        ProfilingTrace trace = this.current.get();
        if (trace == null || trace.isClosed()) {
            trace = new ProfilingTrace(this);
            this.current.set(trace);
        } else if (trace.getFrameCount() >= this.config.getMaxFramesPerTrace()) {
            return Ticker.NO_OP;
        }
        return trace.startFrame(this.sanitizeName(name), this.config.isMemoryProfilingEnabled());
    }

    ProfilerConfiguration getConfiguration() {
        return this.config;
    }

    @Deprecated
    @Nullable
    Ticker getTicker(String name) {
        ProfilingTrace trace = this.current.get();
        if (trace == null) {
            return null;
        }
        ProfilingFrame frame = trace.getCurrentFrame();
        if (frame == null) {
            return null;
        }
        String sanitedName = this.sanitizeName(name);
        while (frame != null && !sanitedName.equals(frame.getName())) {
            frame = frame.getParent();
        }
        return frame;
    }

    void onClose(ProfilingTrace trace, ProfilingFrame rootFrame) {
        if (this.logger.isDebugEnabled() && !rootFrame.isPruned() && rootFrame.getDurationNanos() >= this.config.getMinTraceTime(TimeUnit.NANOSECONDS)) {
            StringBuilder builder = new StringBuilder();
            rootFrame.append("", builder);
            if (builder.length() > 0) {
                this.logger.debug(builder.toString());
            }
        }
        if (this.current.get() == trace) {
            this.current.remove();
        }
    }

    private String sanitizeName(String name) {
        int max = this.config.getMaxFrameNameLength();
        if (name == null || name.length() <= max) {
            return name;
        }
        return name.substring(0, max) + "...";
    }
}

