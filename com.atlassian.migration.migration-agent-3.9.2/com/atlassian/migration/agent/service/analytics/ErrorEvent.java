/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  lombok.NonNull
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.analytics.ErrorContainerType;
import com.atlassian.migration.agent.service.impl.StepType;
import lombok.Generated;
import lombok.NonNull;

public class ErrorEvent {
    private MigrationErrorCode errorCode;
    private String reason;
    private ErrorContainerType containerType;
    private String containerId;
    private String cloudId;
    private StepType type;
    private String spaceKey;

    private ErrorEvent(@NonNull MigrationErrorCode errorCode, String reason, @NonNull ErrorContainerType containerType, @NonNull String containerId, String cloudId, @NonNull StepType type, String spaceKey) {
        if (errorCode == null) {
            throw new NullPointerException("errorCode is marked non-null but is null");
        }
        if (containerType == null) {
            throw new NullPointerException("containerType is marked non-null but is null");
        }
        if (containerId == null) {
            throw new NullPointerException("containerId is marked non-null but is null");
        }
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        this.errorCode = errorCode;
        this.reason = reason;
        this.containerType = containerType;
        this.containerId = containerId;
        this.cloudId = cloudId;
        this.type = type;
        this.spaceKey = spaceKey;
    }

    @Generated
    public MigrationErrorCode getErrorCode() {
        return this.errorCode;
    }

    @Generated
    public String getReason() {
        return this.reason;
    }

    @Generated
    public ErrorContainerType getContainerType() {
        return this.containerType;
    }

    @Generated
    public String getContainerId() {
        return this.containerId;
    }

    @Generated
    public String getCloudId() {
        return this.cloudId;
    }

    @Generated
    public StepType getType() {
        return this.type;
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    public static class ErrorEventBuilder {
        private MigrationErrorCode errorCode;
        private String reason;
        private ErrorContainerType containerType;
        private String containerId;
        private String cloudId;
        private StepType type;
        private String spaceKey;

        public ErrorEventBuilder(@NonNull MigrationErrorCode errorCode, @NonNull ErrorContainerType containerType, @NonNull String containerId, @NonNull StepType type) {
            if (errorCode == null) {
                throw new NullPointerException("errorCode is marked non-null but is null");
            }
            if (containerType == null) {
                throw new NullPointerException("containerType is marked non-null but is null");
            }
            if (containerId == null) {
                throw new NullPointerException("containerId is marked non-null but is null");
            }
            if (type == null) {
                throw new NullPointerException("type is marked non-null but is null");
            }
            this.errorCode = errorCode;
            this.containerType = containerType;
            this.containerId = containerId;
            this.type = type;
        }

        public ErrorEventBuilder setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public ErrorEventBuilder setCloudid(String cloudId) {
            this.cloudId = cloudId;
            return this;
        }

        public ErrorEventBuilder setSpaceKey(String spaceKey) {
            this.spaceKey = spaceKey;
            return this;
        }

        public ErrorEvent build() {
            return new ErrorEvent(this.errorCode, this.reason, this.containerType, this.containerId, this.cloudId, this.type, this.spaceKey);
        }
    }
}

