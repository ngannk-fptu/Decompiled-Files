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

public class LicensedUserInfoEntity {
    @JsonProperty(value="username")
    private String username;
    @JsonProperty(value="email")
    private String email;
    @JsonProperty(value="displayName")
    private String displayName;
    @JsonProperty(value="externalId")
    private String externalId;
    @JsonProperty(value="crowdDirectoryId")
    private Long crowdDirectoryId;
    @JsonProperty(value="directoryId")
    private Long directoryId;
    @JsonProperty(value="remoteCrowdUser")
    private boolean remoteCrowdUser;
    @JsonProperty(value="lastLoginTime")
    private Long lastLoginTime;
    @JsonProperty(value="applicationKey")
    private String applicationKey;

    @JsonCreator
    public LicensedUserInfoEntity(@JsonProperty(value="username") String username, @JsonProperty(value="email") String email, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="externalId") String externalId, @JsonProperty(value="crowdDirectoryId") Long crowdDirectoryId, @JsonProperty(value="directoryId") Long directoryId, @JsonProperty(value="remoteCrowdUser") boolean remoteCrowdUser, @JsonProperty(value="lastLoginTime") Long lastLoginTime, @JsonProperty(value="applicationKey") String applicationKey) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.externalId = externalId;
        this.crowdDirectoryId = crowdDirectoryId;
        this.directoryId = directoryId;
        this.remoteCrowdUser = remoteCrowdUser;
        this.lastLoginTime = lastLoginTime;
        this.applicationKey = applicationKey;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getCrowdDirectoryId() {
        return this.crowdDirectoryId;
    }

    public void setCrowdDirectoryId(Long crowdDirectoryId) {
        this.crowdDirectoryId = crowdDirectoryId;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public boolean isRemoteCrowdUser() {
        return this.remoteCrowdUser;
    }

    public void setRemoteCrowdUser(boolean remoteCrowdUser) {
        this.remoteCrowdUser = remoteCrowdUser;
    }

    public Long getLastLoginTime() {
        return this.lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getApplicationKey() {
        return this.applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LicensedUserInfoEntity data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LicensedUserInfoEntity that = (LicensedUserInfoEntity)o;
        return Objects.equals(this.getUsername(), that.getUsername()) && Objects.equals(this.getEmail(), that.getEmail()) && Objects.equals(this.getDisplayName(), that.getDisplayName()) && Objects.equals(this.getExternalId(), that.getExternalId()) && Objects.equals(this.getCrowdDirectoryId(), that.getCrowdDirectoryId()) && Objects.equals(this.getDirectoryId(), that.getDirectoryId()) && Objects.equals(this.isRemoteCrowdUser(), that.isRemoteCrowdUser()) && Objects.equals(this.getLastLoginTime(), that.getLastLoginTime()) && Objects.equals(this.getApplicationKey(), that.getApplicationKey());
    }

    public int hashCode() {
        return Objects.hash(this.getUsername(), this.getEmail(), this.getDisplayName(), this.getExternalId(), this.getCrowdDirectoryId(), this.getDirectoryId(), this.isRemoteCrowdUser(), this.getLastLoginTime(), this.getApplicationKey());
    }

    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]").add("username=" + this.getUsername()).add("email=" + this.getEmail()).add("displayName=" + this.getDisplayName()).add("externalId=" + this.getExternalId()).add("crowdDirectoryId=" + this.getCrowdDirectoryId()).add("directoryId=" + this.getDirectoryId()).add("remoteCrowdUser=" + this.isRemoteCrowdUser()).add("lastLoginTime=" + this.getLastLoginTime()).add("applicationKey=" + this.getApplicationKey()).toString();
    }

    public static final class Builder {
        private String username;
        private String email;
        private String displayName;
        private String externalId;
        private Long crowdDirectoryId;
        private Long directoryId;
        private boolean remoteCrowdUser;
        private Long lastLoginTime;
        private String applicationKey;

        private Builder() {
        }

        private Builder(LicensedUserInfoEntity initialData) {
            this.username = initialData.getUsername();
            this.email = initialData.getEmail();
            this.displayName = initialData.getDisplayName();
            this.externalId = initialData.getExternalId();
            this.crowdDirectoryId = initialData.getCrowdDirectoryId();
            this.directoryId = initialData.getDirectoryId();
            this.remoteCrowdUser = initialData.isRemoteCrowdUser();
            this.lastLoginTime = initialData.getLastLoginTime();
            this.applicationKey = initialData.getApplicationKey();
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder setCrowdDirectoryId(Long crowdDirectoryId) {
            this.crowdDirectoryId = crowdDirectoryId;
            return this;
        }

        public Builder setDirectoryId(Long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder setRemoteCrowdUser(boolean remoteCrowdUser) {
            this.remoteCrowdUser = remoteCrowdUser;
            return this;
        }

        public Builder setLastLoginTime(Long lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
            return this;
        }

        public Builder setApplicationKey(String applicationKey) {
            this.applicationKey = applicationKey;
            return this;
        }

        public LicensedUserInfoEntity build() {
            return new LicensedUserInfoEntity(this.username, this.email, this.displayName, this.externalId, this.crowdDirectoryId, this.directoryId, this.remoteCrowdUser, this.lastLoginTime, this.applicationKey);
        }
    }
}

