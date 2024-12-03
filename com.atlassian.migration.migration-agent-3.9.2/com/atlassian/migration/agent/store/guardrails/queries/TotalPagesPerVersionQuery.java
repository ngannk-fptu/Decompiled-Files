/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.TotalPagesPerVersionQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class TotalPagesPerVersionQuery
implements GrQuery<TotalPagesPerVersionQueryResult>,
L1AssessmentQuery<TotalPagesPerVersionQueryResult> {
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;

    public TotalPagesPerVersionQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_PAGES_PER_VERSION.name();
    }

    @Override
    public TotalPagesPerVersionQueryResult execute() {
        String query = "select version as version, count(*) as page_count from Content where type in ('PAGE', 'BLOGPOST') and status in ('current', 'draft') group by version order by page_count desc";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).max(100).list();
        return new TotalPagesPerVersionQueryResult(result);
    }
}

