/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.pats.events.token;

import com.atlassian.pats.db.TokenDTO;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public abstract class TokenEvent {
    protected final String triggeredBy;
    protected final String tokenOwnerId;
    protected final String tokenId;
    protected final String tokenName;
    protected final int expiryDays;
    protected final Source triggerSource;

    public TokenEvent(@Nullable String triggeredBy, String tokenOwnerId, String tokenId, String tokenName, int expiryDays) {
        this.triggeredBy = triggeredBy;
        this.tokenOwnerId = tokenOwnerId;
        this.tokenId = tokenId;
        this.tokenName = tokenName;
        this.expiryDays = expiryDays;
        this.triggerSource = StringUtils.isEmpty((CharSequence)triggeredBy) ? Source.SYSTEM : Source.USER;
    }

    public TokenEvent(TokenDTO token, Instant now, String triggeredBy) {
        this(triggeredBy, token.getUserKey(), token.getTokenId(), token.getName(), TokenEvent.expiryDifference(token, now));
    }

    private static int expiryDifference(TokenDTO token, Instant now) {
        Instant instant = token.getExpiringAt().toInstant();
        return (int)Duration.between(now, instant).toDays();
    }

    public Source getTriggerSource() {
        return this.triggerSource;
    }

    public String getTriggeredBy() {
        return this.triggeredBy;
    }

    public String getTokenOwnerId() {
        return this.tokenOwnerId;
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public String getTokenName() {
        return this.tokenName;
    }

    public int getExpiryDays() {
        return this.expiryDays;
    }

    public String toString() {
        return "TokenEvent(triggeredBy=" + this.getTriggeredBy() + ", tokenOwnerId=" + this.getTokenOwnerId() + ", tokenId=" + this.getTokenId() + ", tokenName=" + this.getTokenName() + ", expiryDays=" + this.getExpiryDays() + ", triggerSource=" + (Object)((Object)this.getTriggerSource()) + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenEvent)) {
            return false;
        }
        TokenEvent other = (TokenEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getExpiryDays() != other.getExpiryDays()) {
            return false;
        }
        String this$triggeredBy = this.getTriggeredBy();
        String other$triggeredBy = other.getTriggeredBy();
        if (this$triggeredBy == null ? other$triggeredBy != null : !this$triggeredBy.equals(other$triggeredBy)) {
            return false;
        }
        String this$tokenOwnerId = this.getTokenOwnerId();
        String other$tokenOwnerId = other.getTokenOwnerId();
        if (this$tokenOwnerId == null ? other$tokenOwnerId != null : !this$tokenOwnerId.equals(other$tokenOwnerId)) {
            return false;
        }
        String this$tokenId = this.getTokenId();
        String other$tokenId = other.getTokenId();
        if (this$tokenId == null ? other$tokenId != null : !this$tokenId.equals(other$tokenId)) {
            return false;
        }
        String this$tokenName = this.getTokenName();
        String other$tokenName = other.getTokenName();
        if (this$tokenName == null ? other$tokenName != null : !this$tokenName.equals(other$tokenName)) {
            return false;
        }
        Source this$triggerSource = this.getTriggerSource();
        Source other$triggerSource = other.getTriggerSource();
        return !(this$triggerSource == null ? other$triggerSource != null : !((Object)((Object)this$triggerSource)).equals((Object)other$triggerSource));
    }

    protected boolean canEqual(Object other) {
        return other instanceof TokenEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getExpiryDays();
        String $triggeredBy = this.getTriggeredBy();
        result = result * 59 + ($triggeredBy == null ? 43 : $triggeredBy.hashCode());
        String $tokenOwnerId = this.getTokenOwnerId();
        result = result * 59 + ($tokenOwnerId == null ? 43 : $tokenOwnerId.hashCode());
        String $tokenId = this.getTokenId();
        result = result * 59 + ($tokenId == null ? 43 : $tokenId.hashCode());
        String $tokenName = this.getTokenName();
        result = result * 59 + ($tokenName == null ? 43 : $tokenName.hashCode());
        Source $triggerSource = this.getTriggerSource();
        result = result * 59 + ($triggerSource == null ? 43 : ((Object)((Object)$triggerSource)).hashCode());
        return result;
    }

    static enum Source {
        SYSTEM,
        USER;

    }
}

