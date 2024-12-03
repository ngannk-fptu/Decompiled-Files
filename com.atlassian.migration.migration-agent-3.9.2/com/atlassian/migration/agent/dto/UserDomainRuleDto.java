/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import javax.annotation.Nonnull;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserDomainRuleDto
implements Comparable<UserDomainRuleDto> {
    @JsonProperty
    private String domainName;
    @JsonProperty
    private DomainRuleBehaviour rule;

    @JsonCreator
    public UserDomainRuleDto(@JsonProperty(value="domainName") String domainName, @JsonProperty(value="rule") DomainRuleBehaviour rule) {
        this.domainName = domainName;
        this.rule = rule;
    }

    @Nonnull
    public String getDomainName() {
        return this.domainName;
    }

    @Nonnull
    public DomainRuleBehaviour getRule() {
        return this.rule;
    }

    @Override
    public int compareTo(UserDomainRuleDto other) {
        int ruleComparison = this.ruleOrder(this.rule).compareTo(this.ruleOrder(other.rule));
        if (ruleComparison != 0) {
            return ruleComparison;
        }
        return this.domainName.compareToIgnoreCase(other.domainName);
    }

    private Integer ruleOrder(DomainRuleBehaviour rule) {
        switch (rule) {
            case NO_DECISION_MADE: {
                return 1;
            }
            case NOT_TRUSTED: {
                return 2;
            }
            case TRUSTED: {
                return 3;
            }
            case BLOCKED: {
                return 4;
            }
        }
        throw new IllegalArgumentException("Unexpected rule: " + (Object)((Object)rule));
    }

    @Generated
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Generated
    public void setRule(DomainRuleBehaviour rule) {
        this.rule = rule;
    }

    @Generated
    public String toString() {
        return "UserDomainRuleDto(domainName=" + this.getDomainName() + ", rule=" + (Object)((Object)this.getRule()) + ")";
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainRuleDto)) {
            return false;
        }
        UserDomainRuleDto other = (UserDomainRuleDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$domainName = this.getDomainName();
        String other$domainName = other.getDomainName();
        if (this$domainName == null ? other$domainName != null : !this$domainName.equals(other$domainName)) {
            return false;
        }
        DomainRuleBehaviour this$rule = this.getRule();
        DomainRuleBehaviour other$rule = other.getRule();
        return !(this$rule == null ? other$rule != null : !((Object)((Object)this$rule)).equals((Object)other$rule));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainRuleDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $domainName = this.getDomainName();
        result = result * 59 + ($domainName == null ? 43 : $domainName.hashCode());
        DomainRuleBehaviour $rule = this.getRule();
        result = result * 59 + ($rule == null ? 43 : ((Object)((Object)$rule)).hashCode());
        return result;
    }
}

