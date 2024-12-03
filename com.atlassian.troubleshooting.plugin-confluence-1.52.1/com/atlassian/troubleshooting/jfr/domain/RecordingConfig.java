/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonProperty;

public class RecordingConfig {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final boolean toDisk;
    @JsonProperty
    private final boolean dumpOnExit;
    @JsonProperty
    private final String destination;
    @JsonProperty
    private final Long maxAge;
    @JsonProperty
    private final long maxSize;
    @JsonProperty
    private final Long threadDumpInterval;

    private RecordingConfig(Builder builder) {
        this.name = builder.name;
        this.toDisk = builder.toDisk;
        this.dumpOnExit = builder.dumpOnExit;
        this.destination = Objects.requireNonNull(builder.destination);
        this.threadDumpInterval = builder.threadDumpInterval;
        this.maxAge = builder.maxAge;
        this.maxSize = builder.maxSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return this.name;
    }

    public boolean isToDisk() {
        return this.toDisk;
    }

    public boolean isDumpOnExit() {
        return this.dumpOnExit;
    }

    public String getDestination() {
        return this.destination;
    }

    public Long getMaxAge() {
        return this.maxAge;
    }

    public long getMaxSize() {
        return this.maxSize;
    }

    public Optional<Long> getThreadDumpInterval() {
        return Optional.ofNullable(this.threadDumpInterval);
    }

    public static class Builder {
        private String name;
        private boolean toDisk;
        private boolean dumpOnExit;
        private String destination;
        private Long maxAge;
        private long maxSize;
        private Long threadDumpInterval;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withToDisk(boolean toDisk) {
            this.toDisk = toDisk;
            return this;
        }

        public Builder withDumpOnExit(boolean dumpOnExit) {
            this.dumpOnExit = dumpOnExit;
            return this;
        }

        public Builder withThreadDumpInterval(@Nullable Long threadDumpInterval) {
            this.threadDumpInterval = threadDumpInterval;
            return this;
        }

        public Builder withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder withMaxAge(Long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder withMaxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public RecordingConfig build() {
            return new RecordingConfig(this);
        }
    }
}

