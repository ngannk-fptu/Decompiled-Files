/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.NumberOfGroupsQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class NumberOfGroupsQuery
implements GrQuery<NumberOfGroupsQueryResult>,
L1AssessmentQuery<NumberOfGroupsQueryResult> {
    private static final String ACTIVE_KEY = "active";
    private final EntityManagerTemplate tmpl;

    public NumberOfGroupsQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.NUMBER_OF_GROUPS.name();
    }

    @Override
    public NumberOfGroupsQueryResult execute() {
        String query = "select count(*) from CrowdGroup where active =:active";
        Long size = this.tmpl.query(Long.class, query).param(ACTIVE_KEY, (Object)true).single();
        return new NumberOfGroupsQueryResult(size);
    }
}

