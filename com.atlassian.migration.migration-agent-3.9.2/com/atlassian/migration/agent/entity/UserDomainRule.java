/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Generated;

@Entity
@Table(name="MIG_USER_DOMAIN_RULES")
public class UserDomainRule {
    @Id
    @Column(name="domain_name", nullable=false, unique=true)
    private String domainName;
    @Column(name="rule_behaviour", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private DomainRuleBehaviour ruleBehaviour;

    public UserDomainRule() {
    }

    public UserDomainRule(String domainName, DomainRuleBehaviour ruleBehaviour) {
        this.domainName = domainName;
        this.ruleBehaviour = ruleBehaviour;
    }

    @Nonnull
    public String getDomainName() {
        return this.domainName;
    }

    @Nonnull
    public DomainRuleBehaviour getRuleBehaviour() {
        return this.ruleBehaviour;
    }

    @Generated
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Generated
    public void setRuleBehaviour(DomainRuleBehaviour ruleBehaviour) {
        this.ruleBehaviour = ruleBehaviour;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserDomainRule)) {
            return false;
        }
        UserDomainRule other = (UserDomainRule)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$domainName = this.getDomainName();
        String other$domainName = other.getDomainName();
        if (this$domainName == null ? other$domainName != null : !this$domainName.equals(other$domainName)) {
            return false;
        }
        DomainRuleBehaviour this$ruleBehaviour = this.getRuleBehaviour();
        DomainRuleBehaviour other$ruleBehaviour = other.getRuleBehaviour();
        return !(this$ruleBehaviour == null ? other$ruleBehaviour != null : !((Object)((Object)this$ruleBehaviour)).equals((Object)other$ruleBehaviour));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UserDomainRule;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $domainName = this.getDomainName();
        result = result * 59 + ($domainName == null ? 43 : $domainName.hashCode());
        DomainRuleBehaviour $ruleBehaviour = this.getRuleBehaviour();
        result = result * 59 + ($ruleBehaviour == null ? 43 : ((Object)((Object)$ruleBehaviour)).hashCode());
        return result;
    }
}

