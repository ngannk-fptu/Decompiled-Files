/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package com.atlassian.pats.db;

import com.atlassian.pats.db.NotificationState;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.NonNull;

public class TokenDTO {
    public static final Date NON_EXPIRING_DATE = Date.from(LocalDate.of(9999, 12, 31).atStartOfDay(ZoneOffset.UTC).toInstant());
    private Long id;
    @NonNull
    private String userKey;
    @NonNull
    private String hashedToken;
    @NonNull
    private String tokenId;
    private String name;
    private Date createdAt;
    private Date lastAccessedAt;
    @NonNull
    private Date expiringAt;
    private String rawToken;
    @NonNull
    private NotificationState notificationState = NotificationState.NOT_SENT;

    public void setNonExpiring() {
        this.expiringAt = NON_EXPIRING_DATE;
    }

    public boolean isNonExpiring() {
        return NON_EXPIRING_DATE.equals(this.expiringAt);
    }

    public static TokenDTOBuilder builder() {
        return new TokenDTOBuilder();
    }

    public TokenDTOBuilder toBuilder() {
        return new TokenDTOBuilder().id(this.id).userKey(this.userKey).hashedToken(this.hashedToken).tokenId(this.tokenId).name(this.name).createdAt(this.createdAt).lastAccessedAt(this.lastAccessedAt).expiringAt(this.expiringAt).rawToken(this.rawToken).notificationState(this.notificationState);
    }

    public Long getId() {
        return this.id;
    }

    @NonNull
    public String getUserKey() {
        return this.userKey;
    }

    @NonNull
    public String getHashedToken() {
        return this.hashedToken;
    }

    @NonNull
    public String getTokenId() {
        return this.tokenId;
    }

    public String getName() {
        return this.name;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public Date getLastAccessedAt() {
        return this.lastAccessedAt;
    }

    @NonNull
    public Date getExpiringAt() {
        return this.expiringAt;
    }

    public String getRawToken() {
        return this.rawToken;
    }

    @NonNull
    public NotificationState getNotificationState() {
        return this.notificationState;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserKey(@NonNull String userKey) {
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        this.userKey = userKey;
    }

    public void setHashedToken(@NonNull String hashedToken) {
        if (hashedToken == null) {
            throw new NullPointerException("hashedToken is marked non-null but is null");
        }
        this.hashedToken = hashedToken;
    }

    public void setTokenId(@NonNull String tokenId) {
        if (tokenId == null) {
            throw new NullPointerException("tokenId is marked non-null but is null");
        }
        this.tokenId = tokenId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public void setExpiringAt(@NonNull Date expiringAt) {
        if (expiringAt == null) {
            throw new NullPointerException("expiringAt is marked non-null but is null");
        }
        this.expiringAt = expiringAt;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }

    public void setNotificationState(@NonNull NotificationState notificationState) {
        if (notificationState == null) {
            throw new NullPointerException("notificationState is marked non-null but is null");
        }
        this.notificationState = notificationState;
    }

    public TokenDTO() {
    }

    public TokenDTO(Long id, @NonNull String userKey, @NonNull String hashedToken, @NonNull String tokenId, String name, Date createdAt, Date lastAccessedAt, @NonNull Date expiringAt, String rawToken, @NonNull NotificationState notificationState) {
        if (userKey == null) {
            throw new NullPointerException("userKey is marked non-null but is null");
        }
        if (hashedToken == null) {
            throw new NullPointerException("hashedToken is marked non-null but is null");
        }
        if (tokenId == null) {
            throw new NullPointerException("tokenId is marked non-null but is null");
        }
        if (expiringAt == null) {
            throw new NullPointerException("expiringAt is marked non-null but is null");
        }
        if (notificationState == null) {
            throw new NullPointerException("notificationState is marked non-null but is null");
        }
        this.id = id;
        this.userKey = userKey;
        this.hashedToken = hashedToken;
        this.tokenId = tokenId;
        this.name = name;
        this.createdAt = createdAt;
        this.lastAccessedAt = lastAccessedAt;
        this.expiringAt = expiringAt;
        this.rawToken = rawToken;
        this.notificationState = notificationState;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenDTO)) {
            return false;
        }
        TokenDTO other = (TokenDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$userKey = this.getUserKey();
        String other$userKey = other.getUserKey();
        if (this$userKey == null ? other$userKey != null : !this$userKey.equals(other$userKey)) {
            return false;
        }
        String this$hashedToken = this.getHashedToken();
        String other$hashedToken = other.getHashedToken();
        if (this$hashedToken == null ? other$hashedToken != null : !this$hashedToken.equals(other$hashedToken)) {
            return false;
        }
        String this$tokenId = this.getTokenId();
        String other$tokenId = other.getTokenId();
        if (this$tokenId == null ? other$tokenId != null : !this$tokenId.equals(other$tokenId)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$rawToken = this.getRawToken();
        String other$rawToken = other.getRawToken();
        if (this$rawToken == null ? other$rawToken != null : !this$rawToken.equals(other$rawToken)) {
            return false;
        }
        NotificationState this$notificationState = this.getNotificationState();
        NotificationState other$notificationState = other.getNotificationState();
        return !(this$notificationState == null ? other$notificationState != null : !((Object)((Object)this$notificationState)).equals((Object)other$notificationState));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $userKey = this.getUserKey();
        result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
        String $hashedToken = this.getHashedToken();
        result = result * 59 + ($hashedToken == null ? 43 : $hashedToken.hashCode());
        String $tokenId = this.getTokenId();
        result = result * 59 + ($tokenId == null ? 43 : $tokenId.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $rawToken = this.getRawToken();
        result = result * 59 + ($rawToken == null ? 43 : $rawToken.hashCode());
        NotificationState $notificationState = this.getNotificationState();
        result = result * 59 + ($notificationState == null ? 43 : ((Object)((Object)$notificationState)).hashCode());
        return result;
    }

    public String toString() {
        return "TokenDTO(id=" + this.getId() + ", userKey=" + this.getUserKey() + ", hashedToken=" + this.getHashedToken() + ", tokenId=" + this.getTokenId() + ", name=" + this.getName() + ", createdAt=" + this.getCreatedAt() + ", lastAccessedAt=" + this.getLastAccessedAt() + ", expiringAt=" + this.getExpiringAt() + ", notificationState=" + (Object)((Object)this.getNotificationState()) + ")";
    }

    public static class TokenDTOBuilder {
        private Long id;
        private String userKey;
        private String hashedToken;
        private String tokenId;
        private String name;
        private Date createdAt;
        private Date lastAccessedAt;
        private Date expiringAt;
        private String rawToken;
        private NotificationState notificationState;

        TokenDTOBuilder() {
        }

        public TokenDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TokenDTOBuilder userKey(@NonNull String userKey) {
            if (userKey == null) {
                throw new NullPointerException("userKey is marked non-null but is null");
            }
            this.userKey = userKey;
            return this;
        }

        public TokenDTOBuilder hashedToken(@NonNull String hashedToken) {
            if (hashedToken == null) {
                throw new NullPointerException("hashedToken is marked non-null but is null");
            }
            this.hashedToken = hashedToken;
            return this;
        }

        public TokenDTOBuilder tokenId(@NonNull String tokenId) {
            if (tokenId == null) {
                throw new NullPointerException("tokenId is marked non-null but is null");
            }
            this.tokenId = tokenId;
            return this;
        }

        public TokenDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TokenDTOBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TokenDTOBuilder lastAccessedAt(Date lastAccessedAt) {
            this.lastAccessedAt = lastAccessedAt;
            return this;
        }

        public TokenDTOBuilder expiringAt(@NonNull Date expiringAt) {
            if (expiringAt == null) {
                throw new NullPointerException("expiringAt is marked non-null but is null");
            }
            this.expiringAt = expiringAt;
            return this;
        }

        public TokenDTOBuilder rawToken(String rawToken) {
            this.rawToken = rawToken;
            return this;
        }

        public TokenDTOBuilder notificationState(@NonNull NotificationState notificationState) {
            if (notificationState == null) {
                throw new NullPointerException("notificationState is marked non-null but is null");
            }
            this.notificationState = notificationState;
            return this;
        }

        public TokenDTO build() {
            return new TokenDTO(this.id, this.userKey, this.hashedToken, this.tokenId, this.name, this.createdAt, this.lastAccessedAt, this.expiringAt, this.rawToken, this.notificationState);
        }

        public String toString() {
            return "TokenDTO.TokenDTOBuilder(id=" + this.id + ", userKey=" + this.userKey + ", hashedToken=" + this.hashedToken + ", tokenId=" + this.tokenId + ", name=" + this.name + ", createdAt=" + this.createdAt + ", lastAccessedAt=" + this.lastAccessedAt + ", expiringAt=" + this.expiringAt + ", rawToken=" + this.rawToken + ", notificationState=" + (Object)((Object)this.notificationState) + ")";
        }
    }
}

