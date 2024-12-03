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
import com.atlassian.migration.agent.store.guardrails.results.MaxLikeInPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public class MaxLikeInPageQuery
implements GrQuery<MaxLikeInPageQueryResult>,
L1AssessmentQuery<MaxLikeInPageQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxLikeInPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_LIKE_IN_PAGE.name();
    }

    @Override
    public MaxLikeInPageQueryResult execute() {
        String query = "select contentId as page_id, count(id) as like_count from Likes where contentId in (select c.id from Content c where c.type in ('PAGE', 'BLOGPOST') and c.status = 'current' and c.previousVersion is null) group by contentId order by 2 desc";
        Optional<Tuple> pair = this.tmpl.query(Tuple.class, query).first();
        return new MaxLikeInPageQueryResult(pair);
    }
}

