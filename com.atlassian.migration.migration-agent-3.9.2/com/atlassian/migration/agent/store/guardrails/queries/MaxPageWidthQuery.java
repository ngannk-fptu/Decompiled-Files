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
import com.atlassian.migration.agent.store.guardrails.results.MaxPageWidthQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public class MaxPageWidthQuery
implements GrQuery<MaxPageWidthQueryResult>,
L1AssessmentQuery<MaxPageWidthQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxPageWidthQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_PAGE_WIDTH.name();
    }

    @Override
    public MaxPageWidthQueryResult execute() {
        String query = "select parentId as page_id, count(*) as children_count from Content where parentId is not null and type='PAGE' and status='current' group by parentId order by 2 desc";
        Optional<Tuple> pair = this.tmpl.query(Tuple.class, query).first();
        return new MaxPageWidthQueryResult(pair);
    }
}

