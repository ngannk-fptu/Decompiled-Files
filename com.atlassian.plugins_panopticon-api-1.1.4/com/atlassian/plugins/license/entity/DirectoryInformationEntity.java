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

public class DirectoryInformationEntity {
    @JsonProperty(value="localDirectoryId")
    private final Long localDirectoryId;
    @JsonProperty(value="crowdDirectoryId")
    private final Long crowdDirectoryId;
    @JsonProperty(value="localDirectoryName")
    private final String localDirectoryName;
    @JsonProperty(value="remoteCrowdDirectory")
    private final boolean remoteCrowdDirectory;

    @JsonCreator
    public DirectoryInformationEntity(@JsonProperty(value="localDirectoryId") Long localDirectoryId, @JsonProperty(value="crowdDirectoryId") Long crowdDirectoryId, @JsonProperty(value="localDirectoryName") String localDirectoryName, @JsonProperty(value="remoteCrowdDirectory") boolean remoteCrowdDirectory) {
        this.localDirectoryId = localDirectoryId;
        this.crowdDirectoryId = crowdDirectoryId;
        this.localDirectoryName = localDirectoryName;
        this.remoteCrowdDirectory = remoteCrowdDirectory;
    }

    public Long getLocalDirectoryId() {
        return this.localDirectoryId;
    }

    public Long getCrowdDirectoryId() {
        return this.crowdDirectoryId;
    }

    public String getLocalDirectoryName() {
        return this.localDirectoryName;
    }

    public boolean isRemoteCrowdDirectory() {
        return this.remoteCrowdDirectory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DirectoryInformationEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryInformationEntity that = (DirectoryInformationEntity)o;
        return Objects.equals(this.getLocalDirectoryId(), that.getLocalDirectoryId()) && Objects.equals(this.getCrowdDirectoryId(), that.getCrowdDirectoryId()) && Objects.equals(this.getLocalDirectoryName(), that.getLocalDirectoryName()) && Objects.equals(this.isRemoteCrowdDirectory(), that.isRemoteCrowdDirectory());
    }

    public int hashCode() {
        return Objects.hash(this.getLocalDirectoryId(), this.getCrowdDirectoryId(), this.getLocalDirectoryName(), this.isRemoteCrowdDirectory());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("localDirectoryId=" + this.getLocalDirectoryId()).add("crowdDirectoryId=" + this.getCrowdDirectoryId()).add("localDirectoryName=" + this.getLocalDirectoryName()).add("remoteCrowdDirectory=" + this.isRemoteCrowdDirectory()).toString();
    }

    public static final class Builder {
        private Long localDirectoryId;
        private Long crowdDirectoryId;
        private String localDirectoryName;
        private boolean remoteCrowdDirectory;

        private Builder() {
        }

        private Builder(DirectoryInformationEntity initialData) {
            this.localDirectoryId = initialData.getLocalDirectoryId();
            this.crowdDirectoryId = initialData.getCrowdDirectoryId();
            this.localDirectoryName = initialData.getLocalDirectoryName();
            this.remoteCrowdDirectory = initialData.isRemoteCrowdDirectory();
        }

        public Builder setLocalDirectoryId(Long localDirectoryId) {
            this.localDirectoryId = localDirectoryId;
            return this;
        }

        public Builder setCrowdDirectoryId(Long crowdDirectoryId) {
            this.crowdDirectoryId = crowdDirectoryId;
            return this;
        }

        public Builder setLocalDirectoryName(String localDirectoryName) {
            this.localDirectoryName = localDirectoryName;
            return this;
        }

        public Builder setRemoteCrowdDirectory(boolean remoteCrowdDirectory) {
            this.remoteCrowdDirectory = remoteCrowdDirectory;
            return this;
        }

        public DirectoryInformationEntity build() {
            return new DirectoryInformationEntity(this.localDirectoryId, this.crowdDirectoryId, this.localDirectoryName, this.remoteCrowdDirectory);
        }
    }
}

