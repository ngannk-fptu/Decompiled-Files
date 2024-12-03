/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.AllUsersQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public final class AllUsersQuery
implements GrQuery<AllUsersQueryResult>,
L1AssessmentQuery<AllUsersQueryResult> {
    private final EntityManagerTemplate tmpl;

    public AllUsersQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.ALL_USERS.name();
    }

    @Override
    public AllUsersQueryResult execute() {
        String query = "select count(cu.id) from CrowdUser cu";
        Long size = this.tmpl.query(Long.class, query).single();
        return new AllUsersQueryResult(size);
    }
}

