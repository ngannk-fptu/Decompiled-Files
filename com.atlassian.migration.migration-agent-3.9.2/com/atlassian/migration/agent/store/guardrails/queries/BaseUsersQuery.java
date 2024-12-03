/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.BaseUsersQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public final class BaseUsersQuery
implements GrQuery<BaseUsersQueryResult>,
L1AssessmentQuery<BaseUsersQueryResult> {
    private static final String ACTIVE_KEY = "active";
    private final EntityManagerTemplate tmpl;

    public BaseUsersQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.BASE_USERS.name();
    }

    @Override
    public BaseUsersQueryResult execute() {
        String query = "select count(cu.lowerUsername) from CrowdUser cu where cu.active=:active";
        Long size = this.tmpl.query(Long.class, query).param(ACTIVE_KEY, (Object)true).single();
        return new BaseUsersQueryResult(size);
    }
}

