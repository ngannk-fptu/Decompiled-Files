/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.UserDomainRule;
import com.atlassian.migration.agent.store.UserDomainRuleStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserDomainRuleStoreImpl
implements UserDomainRuleStore {
    private static final int BATCH_SIZE = 50;
    private final EntityManagerTemplate template;

    public UserDomainRuleStoreImpl(EntityManagerTemplate template) {
        this.template = template;
    }

    @Override
    public Optional<UserDomainRule> getRule(String domain) {
        return this.template.query(UserDomainRule.class, "select userDomainRule from UserDomainRule userDomainRule where userDomainRule.domainName = :domain").param("domain", (Object)domain).first();
    }

    @Override
    public Set<UserDomainRule> getRules() {
        return new HashSet<UserDomainRule>(this.template.query(UserDomainRule.class, "select userDomainRule from UserDomainRule userDomainRule").list());
    }

    @Override
    public Set<UserDomainRule> getDomainsWithBlockedRuleBehaviour() {
        return new HashSet<UserDomainRule>(this.template.query(UserDomainRule.class, "select userDomainRule from UserDomainRule userDomainRule where userDomainRule.ruleBehaviour = 'BLOCKED'").list());
    }

    @Override
    public void createRule(UserDomainRule userDomainRule) {
        this.template.persist(userDomainRule);
    }

    @Override
    public void updateRule(UserDomainRule userDomainRule) {
        this.template.merge(userDomainRule);
    }

    @Override
    public boolean deleteRule(String domain) {
        return this.template.query("delete from UserDomainRule userDomainRule where userDomainRule.domainName = :domain").param("domain", (Object)domain).update() > 0;
    }

    @Override
    public int deleteRules(List<String> userDomainRules) {
        return this.template.query("delete from UserDomainRule where domainName in :domainNamesToDelete").param("domainNamesToDelete", userDomainRules).update();
    }

    @Override
    public int deleteAllRules() {
        return this.template.query("delete from UserDomainRule userDomainRule").update();
    }

    @Override
    public int deleteUserModifiedDomainRules() {
        return this.template.query("delete from UserDomainRule userDomainRule where userDomainRule.ruleBehaviour != 'BLOCKED'").update();
    }

    @Override
    public void batchCreateAllRules(List<UserDomainRule> userDomainRules) {
        this.deleteUserModifiedDomainRules();
        int userDomainRulesSize = userDomainRules.size();
        for (int i = 0; i < userDomainRulesSize; ++i) {
            this.template.persist(userDomainRules.get(i));
            if ((i + 1) % 50 != 0) continue;
            this.template.flush();
            this.template.clear();
        }
    }
}

