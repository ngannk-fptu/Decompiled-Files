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
import com.atlassian.migration.agent.store.guardrails.results.MaxNumberOfSpacePermissionsInASpaceQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public final class MaxNumberOfSpacePermissionsInASpaceQuery
implements GrQuery<MaxNumberOfSpacePermissionsInASpaceQueryResult>,
L1AssessmentQuery<MaxNumberOfSpacePermissionsInASpaceQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxNumberOfSpacePermissionsInASpaceQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_SPACE_PERMISSION_IN_SPACE.name();
    }

    @Override
    public MaxNumberOfSpacePermissionsInASpaceQueryResult execute() {
        String query = "select sp.space.id as space_id, count(sp.id) as space_permission_count from SpacePermission as sp group by sp.space order by 2 desc";
        Optional<Tuple> result = this.tmpl.query(Tuple.class, query).first();
        return new MaxNumberOfSpacePermissionsInASpaceQueryResult(result);
    }
}

