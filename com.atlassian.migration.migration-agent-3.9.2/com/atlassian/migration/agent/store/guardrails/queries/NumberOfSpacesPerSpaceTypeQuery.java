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
import com.atlassian.migration.agent.store.guardrails.results.NumberOfSpacesPerSpaceTypeQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class NumberOfSpacesPerSpaceTypeQuery
implements GrQuery<NumberOfSpacesPerSpaceTypeQueryResult>,
L1AssessmentQuery<NumberOfSpacesPerSpaceTypeQueryResult> {
    private final EntityManagerTemplate tmpl;
    public static final String TOTAL_SPACE_TYPE = "TOTAL_SPACE_TYPE";

    public NumberOfSpacesPerSpaceTypeQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.NUMBER_OF_SPACES_PER_SPACETYPE.name();
    }

    @Override
    public NumberOfSpacesPerSpaceTypeQueryResult execute() {
        String query = "select s.type as type, count(*) as TOTAL_SPACE_TYPE from Space s where s.type is not null group by s.type";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new NumberOfSpacesPerSpaceTypeQueryResult(result);
    }
}

