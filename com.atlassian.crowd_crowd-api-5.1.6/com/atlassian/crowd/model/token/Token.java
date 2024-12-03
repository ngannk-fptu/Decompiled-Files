/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.model.token;

import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.model.token.TokenLifetime;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

@Deprecated
public class Token
implements Serializable,
AuthenticationToken {
    private static final long serialVersionUID = -6200607622554600683L;
    public static final long APPLICATION_TOKEN_DIRECTORY_ID = -1L;
    private Long id;
    private String identifierHash;
    private String randomHash;
    private long randomNumber;
    private Date createdDate;
    private Date lastAccessedDate;
    private long lastAccessedTime;
    @Nullable
    private Long duration;
    private String name;
    private long directoryId = -1L;
    private String unaliasedUsername;

    private Token(long directoryId, String name, String identifierHash, long randomNumber, String randomHash, Date createdDate, long lastAccessedTime, @Nullable Long duration, String unaliasedUsername) {
        Validate.notNull((Object)directoryId, (String)"directoryId argument cannot be null", (Object[])new Object[0]);
        this.directoryId = directoryId;
        Validate.notNull((Object)name, (String)"name argument cannot be null", (Object[])new Object[0]);
        this.name = name;
        Validate.notNull((Object)identifierHash, (String)"identifierHash argument cannot be null", (Object[])new Object[0]);
        this.identifierHash = identifierHash;
        Validate.notNull((Object)randomNumber, (String)"randomNumber argument cannot be null", (Object[])new Object[0]);
        this.randomNumber = randomNumber;
        Validate.notNull((Object)randomHash, (String)"randomHash argument cannot be null", (Object[])new Object[0]);
        this.randomHash = randomHash;
        Validate.notNull((Object)createdDate, (String)"createdDate argument cannot be null", (Object[])new Object[0]);
        this.createdDate = createdDate;
        Validate.notNull((Object)lastAccessedTime, (String)"lastAccessedTime argument cannot be null", (Object[])new Object[0]);
        this.lastAccessedTime = lastAccessedTime;
        this.lastAccessedDate = new Date();
        if (duration != null) {
            Validate.isTrue((duration >= 0L ? 1 : 0) != 0, (String)"The duration cannot be negative: ", (long)duration);
        }
        this.duration = duration;
        this.unaliasedUsername = (String)Preconditions.checkNotNull((Object)unaliasedUsername);
    }

    protected Token(Token other) {
        this.id = other.getId();
        this.directoryId = other.getDirectoryId();
        this.name = other.getName();
        this.identifierHash = other.getIdentifierHash();
        this.randomNumber = other.getRandomNumber();
        this.randomHash = other.getRandomHash();
        this.createdDate = other.getCreatedDate();
        this.lastAccessedTime = other.getLastAccessedTime();
        this.lastAccessedDate = other.getLastAccessedDate();
        this.duration = other.getDuration();
        this.unaliasedUsername = other.getUnaliasedUsername();
    }

    private Token() {
    }

    @Override
    public Long getId() {
        return this.id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getRandomHash() {
        return this.randomHash;
    }

    private void setRandomHash(String randomHash) {
        this.randomHash = randomHash;
    }

    @Override
    public String getName() {
        return this.name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUnaliasedUsername() {
        if (this.unaliasedUsername != null) {
            return this.unaliasedUsername;
        }
        return this.name;
    }

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    private void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    @Override
    public long getRandomNumber() {
        return this.randomNumber;
    }

    private void setRandomNumber(long randomNumber) {
        this.randomNumber = randomNumber;
    }

    @Override
    public boolean isUserToken() {
        return !this.isApplicationToken();
    }

    @Override
    public boolean isApplicationToken() {
        return this.getDirectoryId() == -1L;
    }

    @Override
    public Date getCreatedDate() {
        return this.createdDate;
    }

    private void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    private Date getLastAccessedDate() {
        return this.lastAccessedDate;
    }

    private void setLastAccessedDate(Date lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }

    @Override
    public long getLastAccessedTime() {
        if (this.lastAccessedTime == 0L && this.lastAccessedDate != null) {
            return this.lastAccessedDate.getTime();
        }
        return this.lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Nullable
    private Long getDuration() {
        return this.duration;
    }

    private void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public TokenLifetime getLifetime() {
        if (this.getDuration() == null) {
            return TokenLifetime.USE_DEFAULT;
        }
        return TokenLifetime.inSeconds(this.duration);
    }

    @Override
    public String getIdentifierHash() {
        return this.identifierHash;
    }

    private void setIdentifierHash(String identifierHash) {
        this.identifierHash = identifierHash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Token)) {
            return false;
        }
        Token token = (Token)o;
        return !(this.getIdentifierHash() != null ? !this.getIdentifierHash().equals(token.getIdentifierHash()) : token.getIdentifierHash() != null);
    }

    public int hashCode() {
        return this.getIdentifierHash() != null ? this.getIdentifierHash().hashCode() : 0;
    }

    public String toString() {
        return "Token{identifierHash='" + this.identifierHash + '\'' + ", lastAccessedTime=" + this.lastAccessedTime + ", createdDate=" + this.createdDate + ", duration=" + this.duration + ", name='" + this.name + '\'' + ", directoryId=" + this.directoryId + '}';
    }

    public static class Builder {
        private long directoryId;
        private String name;
        private String identifierHash;
        private long randomNumber;
        private String randomHash;
        private Long duration = null;
        private Date createdDate = new Date();
        private long lastAccessedTime = System.currentTimeMillis();
        private String unaliasedUsername;

        public Builder(long directoryId, String name, String identifierHash, long randomNumber, String randomHash) {
            this.directoryId = directoryId;
            this.name = name;
            this.identifierHash = identifierHash;
            this.randomNumber = randomNumber;
            this.randomHash = randomHash;
            this.unaliasedUsername = name;
        }

        public Builder(Token prototype) {
            this.directoryId = prototype.directoryId;
            this.name = prototype.name;
            this.identifierHash = prototype.identifierHash;
            this.randomNumber = prototype.randomNumber;
            this.randomHash = prototype.randomHash;
            this.duration = prototype.duration;
            this.createdDate = prototype.createdDate;
            this.lastAccessedTime = prototype.lastAccessedTime;
            this.unaliasedUsername = prototype.getUnaliasedUsername();
        }

        public Builder setDirectoryId(long directoryId) {
            this.directoryId = directoryId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setIdentifierHash(String identifierHash) {
            this.identifierHash = identifierHash;
            return this;
        }

        public Builder setRandomNumber(long randomNumber) {
            this.randomNumber = randomNumber;
            return this;
        }

        public Builder setRandomHash(String randomHash) {
            this.randomHash = randomHash;
            return this;
        }

        public Builder setLifetime(TokenLifetime tokenLifetime) {
            this.duration = tokenLifetime.isDefault() ? null : Long.valueOf(tokenLifetime.getSeconds());
            return this;
        }

        public Builder withDefaultDuration() {
            this.setLifetime(TokenLifetime.USE_DEFAULT);
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

        public Token create() {
            return new Token(this.directoryId, this.name, this.identifierHash, this.randomNumber, this.randomHash, this.createdDate, this.lastAccessedTime, this.duration, this.unaliasedUsername);
        }
    }
}

