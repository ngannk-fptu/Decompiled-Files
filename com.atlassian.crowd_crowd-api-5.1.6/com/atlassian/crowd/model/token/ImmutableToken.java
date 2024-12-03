/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package com.atlassian.crowd.model.token;

import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.model.token.TokenLifetime;
import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;

public class ImmutableToken
implements AuthenticationToken {
    private final Long id;
    private final String identifierHash;
    private final String randomHash;
    private final long randomNumber;
    private final Date createdDate;
    private final long lastAccessedTime;
    private final Long duration;
    private final String name;
    private final long directoryId;
    private final String unaliasedUsername;
    private final TokenLifetime lifetime;

    protected ImmutableToken(Long id, String identifierHash, String randomHash, long randomNumber, Date createdDate, long lastAccessedTime, Long duration, String name, long directoryId, String unaliasedUsername) {
        this.id = id;
        this.identifierHash = identifierHash;
        this.randomHash = randomHash;
        this.randomNumber = randomNumber;
        this.createdDate = createdDate;
        this.lastAccessedTime = lastAccessedTime;
        this.duration = duration;
        this.name = name;
        this.directoryId = directoryId;
        this.unaliasedUsername = unaliasedUsername;
        this.lifetime = this.getDuration() == null ? TokenLifetime.USE_DEFAULT : TokenLifetime.inSeconds(duration);
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getIdentifierHash() {
        return this.identifierHash;
    }

    @Override
    public String getRandomHash() {
        return this.randomHash;
    }

    @Override
    public long getRandomNumber() {
        return this.randomNumber;
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    public Long getDuration() {
        return this.duration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    @Override
    public String getUnaliasedUsername() {
        return this.unaliasedUsername;
    }

    @Override
    public TokenLifetime getLifetime() {
        return this.lifetime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ImmutableToken data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableToken that = (ImmutableToken)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getIdentifierHash(), that.getIdentifierHash()) && Objects.equals(this.getRandomHash(), that.getRandomHash()) && Objects.equals(this.getRandomNumber(), that.getRandomNumber()) && Objects.equals(this.getCreatedDate(), that.getCreatedDate()) && Objects.equals(this.getLastAccessedTime(), that.getLastAccessedTime()) && Objects.equals(this.getDuration(), that.getDuration()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getDirectoryId(), that.getDirectoryId()) && Objects.equals(this.getUnaliasedUsername(), that.getUnaliasedUsername());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getIdentifierHash(), this.getRandomHash(), this.getRandomNumber(), this.getCreatedDate(), this.getLastAccessedTime(), this.getDuration(), this.getName(), this.getDirectoryId(), this.getUnaliasedUsername());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("identifierHash", (Object)this.getIdentifierHash()).add("randomHash", (Object)this.getRandomHash()).add("randomNumber", this.getRandomNumber()).add("createdDate", (Object)this.getCreatedDate()).add("lastAccessedTime", this.getLastAccessedTime()).add("duration", (Object)this.getDuration()).add("name", (Object)this.getName()).add("directoryId", this.getDirectoryId()).add("unaliasedUsername", (Object)this.getUnaliasedUsername()).toString();
    }

    public static final class Builder {
        private Long id;
        private String identifierHash;
        private String randomHash;
        private long randomNumber;
        private Date createdDate;
        private long lastAccessedTime;
        private Long duration;
        private String name;
        private long directoryId;
        private String unaliasedUsername;

        private Builder() {
        }

        private Builder(ImmutableToken initialData) {
            this.id = initialData.getId();
            this.identifierHash = initialData.getIdentifierHash();
            this.randomHash = initialData.getRandomHash();
            this.randomNumber = initialData.getRandomNumber();
            this.createdDate = initialData.getCreatedDate();
            this.lastAccessedTime = initialData.getLastAccessedTime();
            this.duration = initialData.getDuration();
            this.name = initialData.getName();
            this.directoryId = initialData.getDirectoryId();
            this.unaliasedUsername = initialData.getUnaliasedUsername();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setIdentifierHash(String identifierHash) {
            this.identifierHash = identifierHash;
            return this;
        }

        public Builder setRandomHash(String randomHash) {
            this.randomHash = randomHash;
            return this;
        }

        public Builder setRandomNumber(long randomNumber) {
            this.randomNumber = randomNumber;
            return this;
        }

        public Builder setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
            return this;
        }

        public Builder setDuration(Long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDirectoryId(long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder setUnaliasedUsername(String unaliasedUsername) {
            this.unaliasedUsername = unaliasedUsername;
            return this;
        }

        public ImmutableToken build() {
            return new ImmutableToken(this.id, this.identifierHash, this.randomHash, this.randomNumber, this.createdDate, this.lastAccessedTime, this.duration, this.name, this.directoryId, this.unaliasedUsername);
        }
    }
}

