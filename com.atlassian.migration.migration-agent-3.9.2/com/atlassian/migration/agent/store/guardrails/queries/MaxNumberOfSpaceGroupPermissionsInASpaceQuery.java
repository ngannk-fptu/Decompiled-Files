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
import com.atlassian.migration.agent.store.guardrails.results.MaxNumberOfSpaceGroupPermissionsInASpaceQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public final class MaxNumberOfSpaceGroupPermissionsInASpaceQuery
implements GrQuery<MaxNumberOfSpaceGroupPermissionsInASpaceQueryResult>,
L1AssessmentQuery<MaxNumberOfSpaceGroupPermissionsInASpaceQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxNumberOfSpaceGroupPermissionsInASpaceQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_SPACE_GROUP_PERMISSION_IN_SPACE.name();
    }

    @Override
    public MaxNumberOfSpaceGroupPermissionsInASpaceQueryResult execute() {
        String query = "select sp.space.id as space_id, count(sp.id) as permission_count from SpacePermission as sp where sp.permGroupName is not null group by sp.space.id order by permission_count desc";
        Optional<Tuple> result = this.tmpl.query(Tuple.class, query).first();
        return new MaxNumberOfSpaceGroupPermissionsInASpaceQueryResult(result);
    }
}

