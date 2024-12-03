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
import com.atlassian.migration.agent.store.guardrails.results.NumberOfSpacesQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class NumberOfSpacesQuery
implements GrQuery<NumberOfSpacesQueryResult>,
L1AssessmentQuery<NumberOfSpacesQueryResult> {
    private final EntityManagerTemplate tmpl;

    public NumberOfSpacesQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.NUMBER_OF_SPACES.name();
    }

    @Override
    public NumberOfSpacesQueryResult execute() {
        String query = "select status as status, count(*) as space_count from Space group by status";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new NumberOfSpacesQueryResult(result);
    }
}

