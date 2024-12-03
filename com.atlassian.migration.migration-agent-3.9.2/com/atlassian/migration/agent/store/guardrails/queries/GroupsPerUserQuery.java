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
import com.atlassian.migration.agent.store.guardrails.results.GroupsPerUserQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class GroupsPerUserQuery
implements GrQuery<GroupsPerUserQueryResult>,
L1AssessmentQuery<GroupsPerUserQueryResult> {
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;

    public GroupsPerUserQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.GROUPS_PER_USER.name();
    }

    @Override
    public GroupsPerUserQueryResult execute() {
        String query = "select count(parent) as number_of_groups, child as user_id from CrowdMembership cm group by child order by number_of_groups desc";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).max(100).list();
        return new GroupsPerUserQueryResult(result);
    }
}

