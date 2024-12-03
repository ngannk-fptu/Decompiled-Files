/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.entity.UserDomainRule;
import java.util.Set;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;

public class UserDomainRuleset {
    private Set<UserDomainRule> rules;

    public UserDomainRuleset(Set<UserDomainRule> rules) {
        this.rules = rules;
    }

    public boolean isTrusted(String domainName) {
        return !CollectionUtils.isEmpty(this.rules) && this.rules.contains(new UserDomainRule(domainName, DomainRuleBehaviour.TRUSTED));
    }

    @Generated
    public Set<UserDomainRule> getRules() {
        return this.rules;
    }

    @Generated
    public void setRules(Set<UserDomainRule> rules) {
        this.rules = rules;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainRuleset)) {
            return false;
        }
        UserDomainRuleset other = (UserDomainRuleset)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Set<UserDomainRule> this$rules = this.getRules();
        Set<UserDomainRule> other$rules = other.getRules();
        return !(this$rules == null ? other$rules != null : !((Object)this$rules).equals(other$rules));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainRuleset;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Set<UserDomainRule> $rules = this.getRules();
        result = result * 59 + ($rules == null ? 43 : ((Object)$rules).hashCode());
        return result;
    }
}

