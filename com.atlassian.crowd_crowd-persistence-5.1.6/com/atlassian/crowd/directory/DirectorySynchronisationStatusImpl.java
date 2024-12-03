/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.model.directory.DirectorySynchronisationStatus
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.model.directory.DirectorySynchronisationStatus;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectorySynchronisationStatusImpl
implements DirectorySynchronisationStatus {
    private Integer id;
    private Directory directory;
    private Long startTimestamp;
    private Long endTimestamp;
    private SynchronisationStatusKey status;
    private String statusParameters;
    private String incrementalSyncError;
    private String fullSyncError;
    private String nodeId;
    private String nodeName;

    public DirectorySynchronisationStatusImpl() {
    }

    protected DirectorySynchronisationStatusImpl(Builder builder) {
        this.id = builder.id;
        this.directory = builder.directory;
        this.startTimestamp = builder.startTimestamp;
        this.endTimestamp = builder.endTimestamp;
        this.status = builder.status;
        this.statusParameters = builder.statusParameters;
        this.incrementalSyncError = builder.incrementalSyncError;
        this.fullSyncError = builder.fullSyncError;
        this.nodeId = builder.nodeId;
        this.nodeName = builder.nodeName;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public void setStartTimestamp(long start) {
        this.startTimestamp = start;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public void setEndTimestamp(Long end) {
        this.endTimestamp = end;
    }

    public SynchronisationStatusKey getStatus() {
        return this.status;
    }

    public void setStatus(SynchronisationStatusKey status) {
        this.status = status;
    }

    public String getStatusParameters() {
        return this.statusParameters;
    }

    public void setStatusParameters(String statusParameters) {
        this.statusParameters = statusParameters;
    }

    public String getIncrementalSyncError() {
        return this.incrementalSyncError;
    }

    public void setIncrementalSyncError(String incrementalSyncError) {
        this.incrementalSyncError = incrementalSyncError;
    }

    public String getFullSyncError() {
        return this.fullSyncError;
    }

    public void setFullSyncError(String fullSyncError) {
        this.fullSyncError = fullSyncError;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectorySynchronisationStatusImpl that = (DirectorySynchronisationStatusImpl)o;
        return Objects.equal((Object)this.id, (Object)that.id) && Objects.equal((Object)this.directory, (Object)that.directory) && Objects.equal((Object)this.startTimestamp, (Object)that.startTimestamp) && Objects.equal((Object)this.endTimestamp, (Object)that.endTimestamp) && this.status == that.status && Objects.equal((Object)this.statusParameters, (Object)that.statusParameters) && Objects.equal((Object)this.incrementalSyncError, (Object)that.incrementalSyncError) && Objects.equal((Object)this.fullSyncError, (Object)that.fullSyncError) && Objects.equal((Object)this.nodeId, (Object)that.nodeId) && Objects.equal((Object)this.nodeName, (Object)that.nodeName);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.id, this.directory, this.startTimestamp, this.endTimestamp, this.status, this.statusParameters, this.incrementalSyncError, this.fullSyncError, this.nodeId, this.nodeName});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("directory", (Object)this.directory.getId()).add("startTimestamp", (Object)this.startTimestamp).add("endTimestamp", (Object)this.endTimestamp).add("status", (Object)this.status).add("statusParameters", (Object)this.statusParameters).add("incrementalSyncError", (Object)this.incrementalSyncError).add("fullSyncError", (Object)this.fullSyncError).add("nodeId", (Object)this.nodeId).add("nodeName", (Object)this.nodeName).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DirectorySynchronisationStatus status) {
        return new Builder(status);
    }

    public static class Builder {
        private static final Logger logger = LoggerFactory.getLogger(Builder.class);
        private Integer id;
        private Directory directory;
        private Long startTimestamp;
        private Long endTimestamp;
        private SynchronisationStatusKey status;
        private String statusParameters;
        private String incrementalSyncError;
        private String fullSyncError;
        private String nodeId;
        private String nodeName;

        public Builder() {
        }

        public Builder(DirectorySynchronisationStatus status) {
            this.id = status.getId();
            this.directory = status.getDirectory();
            this.startTimestamp = status.getStartTimestamp();
            this.endTimestamp = status.getEndTimestamp();
            this.status = status.getStatus();
            this.statusParameters = status.getStatusParameters();
            this.incrementalSyncError = status.getIncrementalSyncError();
            this.fullSyncError = status.getFullSyncError();
            this.nodeId = status.getNodeId();
            this.nodeName = status.getNodeName();
        }

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setDirectory(Directory directory) {
            this.directory = directory;
            return this;
        }

        public Builder setStartTimestamp(Long startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Builder setEndTimestamp(Long endTimestamp) {
            this.endTimestamp = endTimestamp;
            return this;
        }

        public Builder setStatus(SynchronisationStatusKey status, List<Serializable> statusParameters) {
            this.status = status;
            this.statusParameters = status.marshallParams(statusParameters);
            return this;
        }

        public Builder setStatus(SynchronisationStatusKey status, String statusParameters) {
            this.status = status;
            this.statusParameters = statusParameters;
            return this;
        }

        public Builder setSyncError(SynchronisationMode syncMode, String errorMessage) {
            switch (syncMode) {
                case FULL: {
                    this.fullSyncError = errorMessage;
                    break;
                }
                case INCREMENTAL: {
                    this.incrementalSyncError = errorMessage;
                    break;
                }
                default: {
                    logger.warn("Got synchronization error for mode {} that is not supported. Skipping: {}", (Object)syncMode, (Object)errorMessage);
                }
            }
            return this;
        }

        public Builder setNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder setNodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public DirectorySynchronisationStatusImpl build() {
            return new DirectorySynchronisationStatusImpl(this);
        }
    }
}

