/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import java.util.Set;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserDomainRulesetDto {
    @JsonProperty
    private Set<UserDomainRuleDto> domains;

    @JsonCreator
    public UserDomainRulesetDto(@JsonProperty(value="domains") Set<UserDomainRuleDto> domains) {
        this.domains = domains;
    }

    @Generated
    public static UserDomainRulesetDtoBuilder builder() {
        return new UserDomainRulesetDtoBuilder();
    }

    @Generated
    public Set<UserDomainRuleDto> getDomains() {
        return this.domains;
    }

    @Generated
    public void setDomains(Set<UserDomainRuleDto> domains) {
        this.domains = domains;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainRulesetDto)) {
            return false;
        }
        UserDomainRulesetDto other = (UserDomainRulesetDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Set<UserDomainRuleDto> this$domains = this.getDomains();
        Set<UserDomainRuleDto> other$domains = other.getDomains();
        return !(this$domains == null ? other$domains != null : !((Object)this$domains).equals(other$domains));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainRulesetDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Set<UserDomainRuleDto> $domains = this.getDomains();
        result = result * 59 + ($domains == null ? 43 : ((Object)$domains).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UserDomainRulesetDto(domains=" + this.getDomains() + ")";
    }

    @Generated
    public static class UserDomainRulesetDtoBuilder {
        @Generated
        private Set<UserDomainRuleDto> domains;

        @Generated
        UserDomainRulesetDtoBuilder() {
        }

        @Generated
        public UserDomainRulesetDtoBuilder domains(Set<UserDomainRuleDto> domains) {
            this.domains = domains;
            return this;
        }

        @Generated
        public UserDomainRulesetDto build() {
            return new UserDomainRulesetDto(this.domains);
        }

        @Generated
        public String toString() {
            return "UserDomainRulesetDto.UserDomainRulesetDtoBuilder(domains=" + this.domains + ")";
        }
    }
}

