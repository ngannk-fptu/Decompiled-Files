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
import com.atlassian.migration.agent.store.guardrails.results.MaxRestrictionsInPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public class MaxRestrictionsInPageQuery
implements GrQuery<MaxRestrictionsInPageQueryResult>,
L1AssessmentQuery<MaxRestrictionsInPageQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxRestrictionsInPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_RESTRICTIONS_IN_PAGE.name();
    }

    @Override
    public MaxRestrictionsInPageQueryResult execute() {
        String query = "select cps.contentId as page_id, count(cp.id) as restriction_count from ContentPerm cp, ContentPermSet cps, Content c where cps.id = cp.cpsId and cps.contentId = c.id and c.status in ('current', 'draft') group by cps.contentId order by restriction_count desc";
        Optional<Tuple> result = this.tmpl.query(Tuple.class, query).first();
        return new MaxRestrictionsInPageQueryResult(result);
    }
}

