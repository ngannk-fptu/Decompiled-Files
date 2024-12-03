/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.AllGroupsQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class AllGroupsQuery
implements GrQuery<AllGroupsQueryResult>,
L1AssessmentQuery<AllGroupsQueryResult> {
    private final EntityManagerTemplate tmpl;

    public AllGroupsQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.GROUP.name();
    }

    @Override
    public AllGroupsQueryResult execute() {
        String query = "select count(distinct cg.id) from CrowdGroup as cg";
        Long count = this.tmpl.query(Long.class, query).single();
        return new AllGroupsQueryResult(count);
    }
}

