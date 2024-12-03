/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.InactiveUsersQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class InactiveUsersQuery
implements GrQuery<InactiveUsersQueryResult>,
L1AssessmentQuery<InactiveUsersQueryResult> {
    private static final String ACTIVE_KEY = "active";
    private final EntityManagerTemplate tmpl;

    public InactiveUsersQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.INACTIVE_USERS.name();
    }

    @Override
    public InactiveUsersQueryResult execute() {
        String query = "select count(cu.lowerUsername) from CrowdUser cu where cu.active=:active";
        Long size = this.tmpl.query(Long.class, query).param(ACTIVE_KEY, (Object)false).single();
        return new InactiveUsersQueryResult(size);
    }
}

