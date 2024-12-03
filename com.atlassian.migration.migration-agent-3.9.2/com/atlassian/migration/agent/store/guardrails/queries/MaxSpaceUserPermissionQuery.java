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
import com.atlassian.migration.agent.store.guardrails.results.MaxSpaceUserPermissionQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.Optional;
import javax.persistence.Tuple;

public class MaxSpaceUserPermissionQuery
implements GrQuery<MaxSpaceUserPermissionQueryResult>,
L1AssessmentQuery<MaxSpaceUserPermissionQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MaxSpaceUserPermissionQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MAX_SPACE_USER_PERMISSION.name();
    }

    @Override
    public MaxSpaceUserPermissionQueryResult execute() {
        String query = "select space.id as space_id, count(id) as space_permission_count from SpacePermission where permUsername is not null group by space.id order by 2 desc";
        Optional<Tuple> pair = this.tmpl.query(Tuple.class, query).first();
        return new MaxSpaceUserPermissionQueryResult(pair);
    }
}

