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
import com.atlassian.migration.agent.store.guardrails.results.NumberOfMediaPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class NumberOfMediaPerPageQuery
implements GrQuery<NumberOfMediaPerPageQueryResult>,
L1AssessmentQuery<NumberOfMediaPerPageQueryResult> {
    private final EntityManagerTemplate tmpl;

    public NumberOfMediaPerPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.NUMBER_OF_MEDIA_PER_PAGE.name();
    }

    @Override
    public NumberOfMediaPerPageQueryResult execute() {
        String query = "select content.container.id as page_id, cast(count(DISTINCT(cp.id)) AS integer) as media_count from ContentProperty cp inner join Content content on cp.content.id = content.id where cp.name = 'FILESTORE_ID' and content.container.id is not null and content.status in ('current', 'draft') group by content.container.id";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).list();
        return new NumberOfMediaPerPageQueryResult(result);
    }
}

