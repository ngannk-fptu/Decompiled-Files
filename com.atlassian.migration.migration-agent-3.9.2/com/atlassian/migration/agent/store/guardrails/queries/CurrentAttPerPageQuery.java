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
import com.atlassian.migration.agent.store.guardrails.results.CurrentAttPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class CurrentAttPerPageQuery
implements GrQuery<CurrentAttPerPageQueryResult>,
L1AssessmentQuery<CurrentAttPerPageQueryResult> {
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;

    public CurrentAttPerPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.CURRENT_ATT_PER_PAGE.name();
    }

    @Override
    public CurrentAttPerPageQueryResult execute() {
        String query = "select content.container.id as page_id, count(*) as attachment_count from Content content where content.type = 'ATTACHMENT'  and content.container.id in (select c.id from Content c where c.previousVersion is null and c.status in ('current', 'draft'))    and content.previousVersion is null    and content.status = 'current' group by content.container.id order by attachment_count DESC";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).max(100).list();
        return new CurrentAttPerPageQueryResult(result);
    }
}

