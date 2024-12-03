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
import com.atlassian.migration.agent.store.guardrails.results.PersonalSpacesForInactiveUsersQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class PersonalSpacesForInactiveUsersQuery
implements GrQuery<PersonalSpacesForInactiveUsersQueryResult>,
L1AssessmentQuery<PersonalSpacesForInactiveUsersQueryResult> {
    private final EntityManagerTemplate tmpl;

    public PersonalSpacesForInactiveUsersQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.PERSONAL_SPACES_INACTIVE_USERS.name();
    }

    @Override
    public PersonalSpacesForInactiveUsersQueryResult execute() {
        String query = "select s.id as space_id from Space s join UserMapping u ON s.creator = u.userKey join CrowdUser c ON c.lowerUsername = u.lowerUsername where s.key like '%~%' AND c.active = 'F'";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new PersonalSpacesForInactiveUsersQueryResult(result);
    }
}

