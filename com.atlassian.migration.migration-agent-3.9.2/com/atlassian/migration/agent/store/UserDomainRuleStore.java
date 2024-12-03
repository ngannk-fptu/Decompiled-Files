/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.UserDomainRule;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDomainRuleStore {
    public Optional<UserDomainRule> getRule(String var1);

    public Set<UserDomainRule> getRules();

    public Set<UserDomainRule> getDomainsWithBlockedRuleBehaviour();

    public void createRule(UserDomainRule var1);

    public void updateRule(UserDomainRule var1);

    public boolean deleteRule(String var1);

    public int deleteRules(List<String> var1);

    public int deleteAllRules();

    public int deleteUserModifiedDomainRules();

    public void batchCreateAllRules(List<UserDomainRule> var1);
}

