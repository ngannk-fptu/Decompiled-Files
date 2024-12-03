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
import com.atlassian.migration.agent.store.guardrails.results.MembershipPerGroupOverLimitQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class MembershipPerGroupOverLimitQuery
implements GrQuery<MembershipPerGroupOverLimitQueryResult>,
L1AssessmentQuery<MembershipPerGroupOverLimitQueryResult> {
    private final EntityManagerTemplate tmpl;
    public static final String GROUP_ID = "group_id";
    public static final String MEMBERSHIP_COUNT = "membership_count";

    public MembershipPerGroupOverLimitQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.GROUPS_WITH_35K_OR_MORE_USER_MEMBERSHIPS.name();
    }

    @Override
    public MembershipPerGroupOverLimitQueryResult execute() {
        String query = "select cg.id as group_id, count(cm.id) as membership_count from CrowdMembership cm join CrowdGroup cg on cg.id=cm.parent group by cg.lowerGroupName, cg.id order by membership_count desc";
        List<Tuple> groups = this.tmpl.query(Tuple.class, query).list();
        return new MembershipPerGroupOverLimitQueryResult(groups);
    }
}

