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
import com.atlassian.migration.agent.store.guardrails.results.SizeOfNonPersonalSpacesQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;

public class SizeOfNonPersonalSpacesQuery
implements GrQuery<SizeOfNonPersonalSpacesQueryResult>,
L1AssessmentQuery<SizeOfNonPersonalSpacesQueryResult> {
    private static final int LIMIT = 10;
    private final EntityManagerTemplate tmpl;

    public SizeOfNonPersonalSpacesQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.SIZE_OF_NON_PERSONAL_SPACES.name();
    }

    @Override
    public SizeOfNonPersonalSpacesQueryResult execute() {
        String query = "select cast(sum(cp.longval)/1024/1024/1024 as big_decimal) as space_size, max(s.id) as space_id, s.type as space_type, s.status as space_status from Space as s inner join Content as c on s.id = c.space.id left outer join ContentProperty as cp on c.id = cp.content where s.type = 'global' and s.status = 'CURRENT' and s.key is not null and s.name is not null and s.type is not null and s.status is not null group by s.key, s.name, s.type, s.status order by space_size desc";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).stream().filter(t -> t.get(0) != null).limit(10L).collect(Collectors.toList());
        return new SizeOfNonPersonalSpacesQueryResult(result);
    }
}

