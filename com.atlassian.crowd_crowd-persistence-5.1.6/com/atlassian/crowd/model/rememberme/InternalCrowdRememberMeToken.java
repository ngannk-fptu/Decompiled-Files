/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.rememberme.CrowdRememberMeToken
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.model.rememberme;

import com.atlassian.crowd.model.rememberme.CrowdRememberMeToken;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;

public class InternalCrowdRememberMeToken
implements CrowdRememberMeToken {
    private Long id;
    @Nonnull
    private String token;
    @Nonnull
    private String username;
    @Nonnull
    private Long directoryId;
    @Nonnull
    private LocalDateTime createdTime;
    private LocalDateTime usedTime;
    @Nonnull
    private String series;
    private String remoteAddress;

    public InternalCrowdRememberMeToken() {
    }

    public InternalCrowdRememberMeToken(Long id, String token, String username, Long directoryId, LocalDateTime createdTime, LocalDateTime usedTime, String series, String remoteAddress) {
        this.id = id;
        this.token = (String)Preconditions.checkNotNull((Object)token);
        this.username = username;
        this.directoryId = directoryId;
        this.createdTime = createdTime;
        this.usedTime = usedTime;
        this.series = (String)Preconditions.checkNotNull((Object)series);
        this.remoteAddress = remoteAddress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(@Nonnull String token) {
        this.token = token;
    }

    public void setDirectoryId(@Nonnull Long directoryId) {
        this.directoryId = directoryId;
    }

    public void setUsedTime(LocalDateTime usedTime) {
        this.usedTime = usedTime;
    }

    public void setCreatedTime(@Nonnull LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void setSeries(@Nonnull String series) {
        this.series = series;
    }

    public void setUsername(@Nonnull String username) {
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public LocalDateTime getUsedTime() {
        return this.usedTime;
    }

    public String getSeries() {
        return this.series;
    }

    public String getRemoteAddress() {
        return this.remoteAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(InternalCrowdRememberMeToken data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        InternalCrowdRememberMeToken that = (InternalCrowdRememberMeToken)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getToken(), that.getToken()) && Objects.equals(this.getUsername(), that.getUsername()) && Objects.equals(this.getDirectoryId(), that.getDirectoryId()) && Objects.equals(this.getCreatedTime(), that.getCreatedTime()) && Objects.equals(this.getUsedTime(), that.getUsedTime()) && Objects.equals(this.getSeries(), that.getSeries()) && Objects.equals(this.getRemoteAddress(), that.getRemoteAddress());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getToken(), this.getUsername(), this.getDirectoryId(), this.getCreatedTime(), this.getUsedTime(), this.getSeries(), this.getRemoteAddress());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("token", (Object)this.getToken()).add("username", (Object)this.getUsername()).add("directoryId", (Object)this.getDirectoryId()).add("createdTime", (Object)this.getCreatedTime()).add("usedTime", (Object)this.getUsedTime()).add("series", (Object)this.getSeries()).add("remoteAddress", (Object)this.getRemoteAddress()).toString();
    }

    public static final class Builder {
        private Long id;
        private String token;
        private String username;
        private Long directoryId;
        private LocalDateTime createdTime;
        private LocalDateTime usedTime;
        private String series;
        private String remoteAddress;

        private Builder() {
        }

        private Builder(InternalCrowdRememberMeToken initialData) {
            this.id = initialData.getId();
            this.token = initialData.getToken();
            this.username = initialData.getUsername();
            this.directoryId = initialData.getDirectoryId();
            this.createdTime = initialData.getCreatedTime();
            this.usedTime = initialData.getUsedTime();
            this.series = initialData.getSeries();
            this.remoteAddress = initialData.getRemoteAddress();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setDirectoryId(Long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder setCreatedTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder setUsedTime(LocalDateTime usedTime) {
            this.usedTime = usedTime;
            return this;
        }

        public Builder setSeries(String series) {
            this.series = series;
            return this;
        }

        public Builder setRemoteAddress(String remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        public InternalCrowdRememberMeToken build() {
            return new InternalCrowdRememberMeToken(this.id, this.token, this.username, this.directoryId, this.createdTime, this.usedTime, this.series, this.remoteAddress);
        }
    }
}

