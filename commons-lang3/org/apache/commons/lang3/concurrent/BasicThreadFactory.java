/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.concurrent;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class BasicThreadFactory
implements ThreadFactory {
    private final AtomicLong threadCounter;
    private final ThreadFactory wrappedFactory;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final String namingPattern;
    private final Integer priority;
    private final Boolean daemon;

    private BasicThreadFactory(Builder builder) {
        this.wrappedFactory = builder.wrappedFactory == null ? Executors.defaultThreadFactory() : builder.wrappedFactory;
        this.namingPattern = builder.namingPattern;
        this.priority = builder.priority;
        this.daemon = builder.daemon;
        this.uncaughtExceptionHandler = builder.exceptionHandler;
        this.threadCounter = new AtomicLong();
    }

    public final ThreadFactory getWrappedFactory() {
        return this.wrappedFactory;
    }

    public final String getNamingPattern() {
        return this.namingPattern;
    }

    public final Boolean getDaemonFlag() {
        return this.daemon;
    }

    public final Integer getPriority() {
        return this.priority;
    }

    public final Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.uncaughtExceptionHandler;
    }

    public long getThreadCount() {
        return this.threadCounter.get();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = this.getWrappedFactory().newThread(runnable);
        this.initializeThread(thread);
        return thread;
    }

    private void initializeThread(Thread thread) {
        if (this.getNamingPattern() != null) {
            Long count = this.threadCounter.incrementAndGet();
            thread.setName(String.format(this.getNamingPattern(), count));
        }
        if (this.getUncaughtExceptionHandler() != null) {
            thread.setUncaughtExceptionHandler(this.getUncaughtExceptionHandler());
        }
        if (this.getPriority() != null) {
            thread.setPriority(this.getPriority());
        }
        if (this.getDaemonFlag() != null) {
            thread.setDaemon(this.getDaemonFlag());
        }
    }

    public static class Builder
    implements org.apache.commons.lang3.builder.Builder<BasicThreadFactory> {
        private ThreadFactory wrappedFactory;
        private Thread.UncaughtExceptionHandler exceptionHandler;
        private String namingPattern;
        private Integer priority;
        private Boolean daemon;

        public Builder wrappedFactory(ThreadFactory factory) {
            Objects.requireNonNull(factory, "factory");
            this.wrappedFactory = factory;
            return this;
        }

        public Builder namingPattern(String pattern) {
            Objects.requireNonNull(pattern, "pattern");
            this.namingPattern = pattern;
            return this;
        }

        public Builder daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
            Objects.requireNonNull(handler, "handler");
            this.exceptionHandler = handler;
            return this;
        }

        public void reset() {
            this.wrappedFactory = null;
            this.exceptionHandler = null;
            this.namingPattern = null;
            this.priority = null;
            this.daemon = null;
        }

        @Override
        public BasicThreadFactory build() {
            BasicThreadFactory factory = new BasicThreadFactory(this);
            this.reset();
            return factory;
        }
    }
}

