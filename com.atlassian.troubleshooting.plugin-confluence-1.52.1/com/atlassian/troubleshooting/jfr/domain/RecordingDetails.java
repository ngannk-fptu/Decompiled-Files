/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.jfr.domain;

import com.atlassian.troubleshooting.jfr.domain.RecordingWrapper;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public final class RecordingDetails {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final long id;
    @JsonProperty
    private final String state;
    @JsonProperty
    private final long size;
    @JsonProperty
    private final Long duration;
    @JsonProperty
    private final boolean toDisk;
    @JsonProperty
    private final boolean dumpOnExit;
    @JsonProperty
    private final Long startTime;
    @JsonProperty
    private final Long stopTime;
    @JsonProperty
    private final String destination;
    @JsonProperty
    private final Long maxAge;
    @JsonProperty
    private final long maxSize;
    @JsonProperty
    private final String nodeId;
    @JsonIgnore
    private final Map<String, String> settings;

    private RecordingDetails(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.state = builder.state;
        this.size = builder.size;
        this.duration = builder.duration;
        this.toDisk = builder.toDisk;
        this.dumpOnExit = builder.dumpOnExit;
        this.startTime = builder.startTime;
        this.stopTime = builder.stopTime;
        this.destination = builder.destination;
        this.maxAge = builder.maxAge;
        this.maxSize = builder.maxSize;
        this.nodeId = builder.nodeId;
        this.settings = builder.settings;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public String getState() {
        return this.state;
    }

    public long getSize() {
        return this.size;
    }

    public Long getDuration() {
        return this.duration;
    }

    public boolean isToDisk() {
        return this.toDisk;
    }

    public boolean isDumpOnExit() {
        return this.dumpOnExit;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getStopTime() {
        return this.stopTime;
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

    public String getNodeId() {
        return this.nodeId;
    }

    public Map<String, String> getSettings() {
        return this.settings;
    }

    public static RecordingDetails from(String nodeId, RecordingWrapper recordingWrapper) {
        return RecordingDetails.builder().withName(recordingWrapper.getName()).withId(recordingWrapper.getId()).withState(recordingWrapper.getState().name()).withSize(recordingWrapper.getSize()).withDuration(recordingWrapper.getDuration() != null ? Long.valueOf(recordingWrapper.getDuration().toMillis()) : null).withToDisk(recordingWrapper.isToDisk()).withDumpOnExit(recordingWrapper.getDumpOnExit()).withStartTime(recordingWrapper.getStartTime() != null ? Long.valueOf(recordingWrapper.getStartTime().toEpochMilli()) : null).withStopTime(recordingWrapper.getStopTime() != null ? Long.valueOf(recordingWrapper.getStopTime().toEpochMilli()) : null).withDestination(recordingWrapper.getDestination() != null ? recordingWrapper.getDestination().toString() : null).withMaxAge(recordingWrapper.getMaxAge() != null ? Long.valueOf(recordingWrapper.getMaxAge().toMillis()) : null).withMaxSize(recordingWrapper.getMaxSize()).withNodeId(nodeId).withSettings(recordingWrapper.getSettings()).build();
    }

    public static class Builder {
        private String name;
        private long id;
        private String state;
        private long size;
        private Long duration;
        private boolean toDisk;
        private boolean dumpOnExit;
        private Long startTime;
        private Long stopTime;
        private String destination;
        private Long maxAge;
        private long maxSize;
        private String nodeId;
        private Map<String, String> settings;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withState(String state) {
            this.state = state;
            return this;
        }

        public Builder withDuration(Long duration) {
            this.duration = duration;
            return this;
        }

        public Builder withSize(long size) {
            this.size = size;
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

        public Builder withStartTime(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withStopTime(Long stopTime) {
            this.stopTime = stopTime;
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

        public Builder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder withSettings(Map<String, String> settings) {
            this.settings = settings;
            return this;
        }

        public RecordingDetails build() {
            return new RecordingDetails(this);
        }
    }
}

