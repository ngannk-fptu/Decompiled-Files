/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.ActiveUsersQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public final class ActiveUsersQuery
implements GrQuery<ActiveUsersQueryResult>,
L1AssessmentQuery<ActiveUsersQueryResult> {
    private static final String ACTIVE_KEY = "active";
    private final EntityManagerTemplate tmpl;

    public ActiveUsersQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.ACTIVE_USERS.name();
    }

    @Override
    public ActiveUsersQueryResult execute() {
        String query = "select count(cu.id) from CrowdUser cu where cu.active=:active";
        Long size = this.tmpl.query(Long.class, query).param(ACTIVE_KEY, (Object)true).single();
        return new ActiveUsersQueryResult(size);
    }
}

