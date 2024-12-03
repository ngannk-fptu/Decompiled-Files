/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.PagesWithRestrictionsQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class PagesWithRestrictionsQuery
implements GrQuery<PagesWithRestrictionsQueryResult>,
L1AssessmentQuery<PagesWithRestrictionsQueryResult> {
    private final EntityManagerTemplate tmpl;

    public PagesWithRestrictionsQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.PAGES_WITH_RESTRICTIONS.name();
    }

    @Override
    public PagesWithRestrictionsQueryResult execute() {
        String query = "select count(distinct(cps.contentId)) from ContentPermSet as cps inner join Content as c on c.id = cps.contentId where c.status in ('current', 'draft')";
        Long size = this.tmpl.query(Long.class, query).single();
        return new PagesWithRestrictionsQueryResult(size);
    }
}

