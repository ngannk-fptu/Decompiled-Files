/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.api;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class DirectorySynchronisationRoundInformation
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final long startTime;
    private final long durationMs;
    @Nullable
    private final String statusKey;
    @Nullable
    private final List<Serializable> statusParameters;
    @Nullable
    private final String nodeId;
    @Nullable
    private final String nodeName;
    @Nullable
    private final String incrementalSyncError;
    @Nullable
    private final String fullSyncError;

    @Deprecated
    public DirectorySynchronisationRoundInformation(long startTime, long durationMs, @Nullable String statusKey, @Nullable List<Serializable> statusParameters) {
        this(DirectorySynchronisationRoundInformation.builder().setStartTime(startTime).setDurationMs(durationMs).setStatusKey(statusKey).setStatusParameters(statusParameters));
    }

    protected DirectorySynchronisationRoundInformation(Builder builder) {
        this.startTime = builder.startTime;
        this.durationMs = builder.durationMs;
        this.statusKey = builder.statusKey;
        this.statusParameters = builder.statusParameters != null ? ImmutableList.copyOf((Collection)builder.statusParameters) : null;
        this.nodeId = StringUtils.stripToNull((String)builder.nodeId);
        this.nodeName = StringUtils.stripToNull((String)builder.nodeName);
        this.incrementalSyncError = StringUtils.stripToNull((String)builder.incrementalSyncError);
        this.fullSyncError = StringUtils.stripToNull((String)builder.fullSyncError);
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getDurationMs() {
        return this.durationMs;
    }

    @Nullable
    public String getStatusKey() {
        return this.statusKey;
    }

    @Nullable
    public List<Serializable> getStatusParameters() {
        return this.statusParameters;
    }

    public Optional<String> getNodeId() {
        return Optional.ofNullable(this.nodeId);
    }

    public Optional<String> getNodeName() {
        return Optional.ofNullable(this.nodeName);
    }

    public Optional<String> getIncrementalSyncError() {
        return Optional.ofNullable(this.incrementalSyncError);
    }

    public Optional<String> getFullSyncError() {
        return Optional.ofNullable(this.fullSyncError);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DirectorySynchronisationRoundInformation data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectorySynchronisationRoundInformation that = (DirectorySynchronisationRoundInformation)o;
        return Objects.equals(this.getStartTime(), that.getStartTime()) && Objects.equals(this.getDurationMs(), that.getDurationMs()) && Objects.equals(this.getStatusKey(), that.getStatusKey()) && Objects.equals(this.getStatusParameters(), that.getStatusParameters()) && Objects.equals(this.getNodeId(), that.getNodeId()) && Objects.equals(this.getNodeName(), that.getNodeName()) && Objects.equals(this.getIncrementalSyncError(), that.getIncrementalSyncError()) && Objects.equals(this.getFullSyncError(), that.getFullSyncError());
    }

    public int hashCode() {
        return Objects.hash(this.getStartTime(), this.getDurationMs(), this.getStatusKey(), this.getStatusParameters(), this.getNodeId(), this.getNodeName(), this.getIncrementalSyncError(), this.getFullSyncError());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("startTime", this.getStartTime()).add("durationMs", this.getDurationMs()).add("statusKey", (Object)this.getStatusKey()).add("statusParameters", this.getStatusParameters()).add("nodeId", this.getNodeId()).add("nodeName", this.getNodeName()).add("incrementalSyncError", this.getIncrementalSyncError()).add("fullSyncError", this.getFullSyncError()).toString();
    }

    public static final class Builder {
        private long startTime;
        private long durationMs;
        private String statusKey;
        private List<Serializable> statusParameters = null;
        private String nodeId;
        private String nodeName;
        private String incrementalSyncError;
        private String fullSyncError;

        private Builder() {
        }

        private Builder(DirectorySynchronisationRoundInformation initialData) {
            this.startTime = initialData.getStartTime();
            this.durationMs = initialData.getDurationMs();
            this.statusKey = initialData.getStatusKey();
            this.statusParameters = initialData.getStatusParameters() != null ? new ArrayList<Serializable>(initialData.getStatusParameters()) : null;
            this.nodeId = initialData.getNodeId().orElse(null);
            this.nodeName = initialData.getNodeName().orElse(null);
            this.incrementalSyncError = initialData.getIncrementalSyncError().orElse(null);
            this.fullSyncError = initialData.getFullSyncError().orElse(null);
        }

        public Builder setStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder setDurationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder setStatusKey(@Nullable String statusKey) {
            this.statusKey = statusKey;
            return this;
        }

        public Builder setStatusParameters(@Nullable List<Serializable> statusParameters) {
            this.statusParameters = statusParameters;
            return this;
        }

        public Builder addStatusParameter(Serializable statusParameter) {
            this.statusParameters.add(statusParameter);
            return this;
        }

        public Builder addStatusParameters(Iterable<Serializable> statusParameters) {
            for (Serializable statusParameter : statusParameters) {
                this.addStatusParameter(statusParameter);
            }
            return this;
        }

        public Builder setNodeId(@Nullable String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder setNodeName(@Nullable String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder setIncrementalSyncError(@Nullable String incrementalSyncError) {
            this.incrementalSyncError = incrementalSyncError;
            return this;
        }

        public Builder setFullSyncError(@Nullable String fullSyncError) {
            this.fullSyncError = fullSyncError;
            return this;
        }

        public DirectorySynchronisationRoundInformation build() {
            return new DirectorySynchronisationRoundInformation(this);
        }
    }
}

