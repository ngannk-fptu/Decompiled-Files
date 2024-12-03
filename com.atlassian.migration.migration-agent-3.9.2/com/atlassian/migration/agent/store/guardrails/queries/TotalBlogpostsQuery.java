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
import com.atlassian.migration.agent.store.guardrails.results.TotalPagesQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class TotalBlogpostsQuery
implements GrQuery<TotalPagesQueryResult>,
L1AssessmentQuery<TotalPagesQueryResult> {
    private final EntityManagerTemplate tmpl;

    public TotalBlogpostsQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_BLOGPOSTS.name();
    }

    @Override
    public TotalPagesQueryResult execute() {
        String query = "select status as status, count(*) as blogpost_count from Content where previousVersion is null and type = 'BLOGPOST' group by status";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new TotalPagesQueryResult(result);
    }
}

