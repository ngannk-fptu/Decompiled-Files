/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserDomainCountDto {
    @JsonProperty
    private final String domainName;
    @JsonProperty
    private final long userCount;

    public UserDomainCountDto(String domainName, long userCount) {
        this.domainName = domainName;
        this.userCount = userCount;
    }

    @Generated
    public String toString() {
        return "UserDomainCountDto(domainName=" + this.getDomainName() + ", userCount=" + this.getUserCount() + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainCountDto)) {
            return false;
        }
        UserDomainCountDto other = (UserDomainCountDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$domainName = this.getDomainName();
        String other$domainName = other.getDomainName();
        if (this$domainName == null ? other$domainName != null : !this$domainName.equals(other$domainName)) {
            return false;
        }
        return this.getUserCount() == other.getUserCount();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainCountDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $domainName = this.getDomainName();
        result = result * 59 + ($domainName == null ? 43 : $domainName.hashCode());
        long $userCount = this.getUserCount();
        result = result * 59 + (int)($userCount >>> 32 ^ $userCount);
        return result;
    }

    @Generated
    public String getDomainName() {
        return this.domainName;
    }

    @Generated
    public long getUserCount() {
        return this.userCount;
    }
}

