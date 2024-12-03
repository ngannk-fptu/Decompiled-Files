/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.util;

import com.atlassian.migration.agent.dto.UserDomainCountDto;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserDomainsDto {
    @JsonProperty
    private final List<UserDomainCountDto> availableDomains;

    public UserDomainsDto(List<UserDomainCountDto> availableDomains) {
        this.availableDomains = availableDomains;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainsDto)) {
            return false;
        }
        UserDomainsDto other = (UserDomainsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<UserDomainCountDto> this$availableDomains = this.availableDomains;
        List<UserDomainCountDto> other$availableDomains = other.availableDomains;
        return !(this$availableDomains == null ? other$availableDomains != null : !((Object)this$availableDomains).equals(other$availableDomains));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainsDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<UserDomainCountDto> $availableDomains = this.availableDomains;
        result = result * 59 + ($availableDomains == null ? 43 : ((Object)$availableDomains).hashCode());
        return result;
    }
}

