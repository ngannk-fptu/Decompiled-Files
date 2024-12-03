/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.MaxCommentsInPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;

public class MaxCommentsInPageQuery
implements GrQuery<MaxCommentsInPageQueryResult>,
L1AssessmentQuery<MaxCommentsInPageQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxCommentsInPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_COMMENTS_IN_PAGE.name();
    }

    @Override
    public MaxCommentsInPageQueryResult execute() {
        String query = "select count(*) as c from Content content where content.type = 'COMMENT' and content.container is not null and content.status = 'current' group by content.container order by c desc";
        Optional<Long> size = this.tmpl.query(Long.class, query).first();
        return new MaxCommentsInPageQueryResult(size);
    }
}

