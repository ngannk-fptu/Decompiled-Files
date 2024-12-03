/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugins.license.entity;

import java.util.Objects;
import java.util.StringJoiner;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SynchronizableVersionInfoEntity {
    public static SynchronizableVersionInfoEntity EMPTY = SynchronizableVersionInfoEntity.builder().build();
    @JsonProperty(value="present")
    private boolean present;
    @JsonProperty(value="version")
    private Long version;
    @JsonProperty(value="startedTime")
    private Long startedTime;
    @JsonProperty(value="finishedTime")
    private Long finishedTime;
    @JsonProperty(value="maximumNumberOfUsers")
    private Long maximumNumberOfUsers;

    @JsonCreator
    public SynchronizableVersionInfoEntity(@JsonProperty(value="present") boolean present, @JsonProperty(value="version") Long version, @JsonProperty(value="startedTime") Long startedTime, @JsonProperty(value="finishedTime") Long finishedTime, @JsonProperty(value="maximumNumberOfUsers") Long maximumNumberOfUsers) {
        this.present = present;
        this.version = version;
        this.startedTime = startedTime;
        this.finishedTime = finishedTime;
        this.maximumNumberOfUsers = maximumNumberOfUsers;
    }

    public boolean isPresent() {
        return this.present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getStartedTime() {
        return this.startedTime;
    }

    public void setStartedTime(Long startedTime) {
        this.startedTime = startedTime;
    }

    public Long getFinishedTime() {
        return this.finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Long getMaximumNumberOfUsers() {
        return this.maximumNumberOfUsers;
    }

    public void setMaximumNumberOfUsers(Long maximumNumberOfUsers) {
        this.maximumNumberOfUsers = maximumNumberOfUsers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SynchronizableVersionInfoEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SynchronizableVersionInfoEntity that = (SynchronizableVersionInfoEntity)o;
        return Objects.equals(this.isPresent(), that.isPresent()) && Objects.equals(this.getVersion(), that.getVersion()) && Objects.equals(this.getStartedTime(), that.getStartedTime()) && Objects.equals(this.getFinishedTime(), that.getFinishedTime()) && Objects.equals(this.getMaximumNumberOfUsers(), that.getMaximumNumberOfUsers());
    }

    public int hashCode() {
        return Objects.hash(this.isPresent(), this.getVersion(), this.getStartedTime(), this.getFinishedTime(), this.getMaximumNumberOfUsers());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("present=" + this.isPresent()).add("version=" + this.getVersion()).add("startedTime=" + this.getStartedTime()).add("finishedTime=" + this.getFinishedTime()).add("maximumNumberOfUsers=" + this.getMaximumNumberOfUsers()).toString();
    }

    public static final class Builder {
        private boolean present;
        private Long version;
        private Long startedTime;
        private Long finishedTime;
        private Long maximumNumberOfUsers;

        private Builder() {
        }

        private Builder(SynchronizableVersionInfoEntity initialData) {
            this.present = initialData.isPresent();
            this.version = initialData.getVersion();
            this.startedTime = initialData.getStartedTime();
            this.finishedTime = initialData.getFinishedTime();
            this.maximumNumberOfUsers = initialData.getMaximumNumberOfUsers();
        }

        public Builder setPresent(boolean present) {
            this.present = present;
            return this;
        }

        public Builder setVersion(Long version) {
            this.version = version;
            return this;
        }

        public Builder setStartedTime(Long startedTime) {
            this.startedTime = startedTime;
            return this;
        }

        public Builder setFinishedTime(Long finishedTime) {
            this.finishedTime = finishedTime;
            return this;
        }

        public Builder setMaximumNumberOfUsers(Long maximumNumberOfUsers) {
            this.maximumNumberOfUsers = maximumNumberOfUsers;
            return this;
        }

        public SynchronizableVersionInfoEntity build() {
            return new SynchronizableVersionInfoEntity(this.present, this.version, this.startedTime, this.finishedTime, this.maximumNumberOfUsers);
        }
    }
}

