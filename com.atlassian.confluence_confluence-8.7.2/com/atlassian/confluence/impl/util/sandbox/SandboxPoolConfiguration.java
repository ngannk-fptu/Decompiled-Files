/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxErrorConsumer
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.util.sandbox.SandboxErrorConsumer;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class SandboxPoolConfiguration {
    private final int concurrencyLevel;
    private final Duration startupTimeLimit;
    private final int memoryInMegabytes;
    private final int stackInMegabytes;
    private final List<Class<?>> bootstrapClasses;
    private final SandboxErrorConsumer errorConsumer;
    private final Level logLevel;
    private final List<String> javaOptions;
    private final Integer debugPortOffset;

    private SandboxPoolConfiguration(Builder builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.startupTimeLimit = builder.startupTimeLimit;
        this.memoryInMegabytes = builder.memoryInMegabytes;
        this.stackInMegabytes = builder.stackInMegabytes;
        this.bootstrapClasses = builder.bootstrapClasses;
        this.errorConsumer = builder.errorConsumer;
        this.logLevel = builder.logLevel;
        this.javaOptions = ImmutableList.copyOf(builder.javaOptions);
        this.debugPortOffset = builder.debugPortOffset;
    }

    public int getConcurrencyLevel() {
        return this.concurrencyLevel;
    }

    Duration getStartupTimeLimit() {
        return this.startupTimeLimit;
    }

    public int getMemoryInMegabytes() {
        return this.memoryInMegabytes;
    }

    public int getStackInMegabytes() {
        return this.stackInMegabytes;
    }

    List<Class<?>> getBootstrapClasses() {
        return this.bootstrapClasses;
    }

    SandboxErrorConsumer getErrorConsumer() {
        return this.errorConsumer;
    }

    Level getLogLevel() {
        return this.logLevel;
    }

    List<String> getJavaOptions() {
        return this.javaOptions;
    }

    Integer getDebugPortOffset() {
        return this.debugPortOffset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int concurrencyLevel = 1;
        private Duration startupTimeLimit = Duration.ofSeconds(30L);
        private int memoryInMegabytes = 512;
        private int stackInMegabytes = 2;
        private List<Class<?>> bootstrapClasses = Collections.emptyList();
        private SandboxErrorConsumer errorConsumer = (name, line) -> {};
        private Level logLevel = Level.INFO;
        private List<String> javaOptions = Collections.emptyList();
        private Integer debugPortOffset = null;

        public Builder withConcurrencyLevel(int concurrencyLevel) {
            if (concurrencyLevel <= 0) {
                throw new IllegalArgumentException();
            }
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public Builder withStartupTimeLimit(Duration timeLimit) {
            this.startupTimeLimit = Objects.requireNonNull(timeLimit);
            return this;
        }

        public Builder withMemoryInMegabytes(int memoryLimitInMegabytes) {
            this.memoryInMegabytes = memoryLimitInMegabytes;
            return this;
        }

        public Builder withStackInMegabytes(int stackInMegabytes) {
            this.stackInMegabytes = stackInMegabytes;
            return this;
        }

        public Builder withBootstrapClasses(List<Class<?>> bootstrapClasses) {
            this.bootstrapClasses = Objects.requireNonNull(bootstrapClasses);
            return this;
        }

        public Builder withErrorConsumer(SandboxErrorConsumer errorConsumer) {
            this.errorConsumer = Objects.requireNonNull(errorConsumer);
            return this;
        }

        public Builder withLogLevel(Level logLevel) {
            this.logLevel = Objects.requireNonNull(logLevel);
            return this;
        }

        public Builder withJavaOptions(String ... options) {
            this.javaOptions = ImmutableList.copyOf((Object[])options);
            return this;
        }

        public Builder withDebugPortOffset(Integer offset) {
            this.debugPortOffset = offset;
            return this;
        }

        public SandboxPoolConfiguration build() {
            return new SandboxPoolConfiguration(this);
        }
    }
}

