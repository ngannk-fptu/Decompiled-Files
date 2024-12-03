/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.MaxNumberOfPagesInASpaceQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;

public class MaxNumberOfPagesInASpaceQuery
implements GrQuery<MaxNumberOfPagesInASpaceQueryResult>,
L1AssessmentQuery<MaxNumberOfPagesInASpaceQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxNumberOfPagesInASpaceQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_NUMBER_OF_PAGES_IN_A_SPACE.name();
    }

    @Override
    public MaxNumberOfPagesInASpaceQueryResult execute() {
        String query = "select count(*) from Content where type in ('PAGE', 'BLOGPOST') and status in ('current', 'draft') and previousVersion is null group by spaceId order by count(*) desc";
        Optional<Long> maxNumberOfPagesInSpace = this.tmpl.query(Long.class, query).first();
        return new MaxNumberOfPagesInASpaceQueryResult(maxNumberOfPagesInSpace);
    }
}

