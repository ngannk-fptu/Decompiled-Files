/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.util.profiling.strategy.impl;

import com.atlassian.util.profiling.ProfilerConfiguration;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.util.profiling.UtilTimerLogger;
import com.atlassian.util.profiling.strategy.ProfilingStrategy;
import com.atlassian.util.profiling.strategy.impl.StackProfilerStrategy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class StackProfilingStrategy
implements ProfilingStrategy {
    private static final Logger log = LoggerFactory.getLogger(Timers.class);
    private static final UtilTimerLogger DEFAULT_LOGGER = arg_0 -> ((Logger)log).debug(arg_0);
    private final ProfilerConfiguration config;
    private final StackProfilerStrategy delegate;
    private UtilTimerLogger logger;

    public StackProfilingStrategy() {
        this(new StackProfilerStrategy());
    }

    public StackProfilingStrategy(@Nonnull StackProfilerStrategy delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.logger = DEFAULT_LOGGER;
        this.config = Timers.getConfiguration();
        delegate.setLogger(log);
    }

    public UtilTimerLogger getLogger() {
        return this.logger;
    }

    public int getMaxFrameCount() {
        return this.config.getMaxFramesPerTrace();
    }

    public long getMinTime() {
        return this.config.getMinFrameTime(TimeUnit.MILLISECONDS);
    }

    public long getMinTotalTime() {
        return this.config.getMinTraceTime(TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isEnabled() {
        return this.config.isEnabled();
    }

    public boolean isProfileMemory() {
        return this.config.isMemoryProfilingEnabled();
    }

    public void setConfiguredMaxFrameCount(int value) {
        this.config.setMaxFramesPerTrace(value);
    }

    public void setConfiguredMinTime(long value) {
        this.config.setMinFrameTime(value, TimeUnit.MILLISECONDS);
    }

    public void setConfiguredMinTotalTime(long value) {
        this.config.setMinTraceTime(value, TimeUnit.MILLISECONDS);
    }

    public void setEnabled(boolean value) {
        this.config.setEnabled(value);
    }

    public void setLogger(@Nonnull UtilTimerLogger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
        Logger logAdapter = (Logger)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Logger.class}, (InvocationHandler)new UtilTimerLoggerAdapter(logger));
        this.delegate.setLogger(logAdapter);
    }

    public void setProfileMemoryFlag(boolean value) {
        this.config.setMemoryProfilingEnabled(value);
    }

    @Override
    public void start(String name) {
        this.delegate.start(name);
    }

    @Override
    public void stop(String name) {
        Ticker ticker = this.delegate.getTicker(name);
        if (ticker != null) {
            ticker.close();
        }
    }

    private static class UtilTimerLoggerAdapter
    implements InvocationHandler {
        private final UtilTimerLogger logger;

        private UtilTimerLoggerAdapter(UtilTimerLogger logger) {
            this.logger = logger;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            if ("isDebugEnabled".equals(method.getName())) {
                return true;
            }
            if ("debug".equals(method.getName()) && args.length > 0) {
                this.logger.log((String)args[0]);
            }
            return null;
        }
    }
}

