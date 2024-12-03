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
import com.atlassian.migration.agent.store.guardrails.results.TotalAttachmentsPerPageQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class TotalAttachmentsPerPageQuery
implements GrQuery<TotalAttachmentsPerPageQueryResult>,
L1AssessmentQuery<TotalAttachmentsPerPageQueryResult> {
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;

    public TotalAttachmentsPerPageQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_ATTACHMENTS_PER_PAGE.name();
    }

    @Override
    public TotalAttachmentsPerPageQueryResult execute() {
        String query = "select content.container.id as page_id, count(*) as attachment_count from Content content where content.type = 'ATTACHMENT' and content.container.id in (select c.id from Content c where c.type in ('PAGE', 'BLOGPOST') and c.status in ('current', 'draft') and c.previousVersion is null) group by content.container.id order by attachment_count desc";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).max(100).list();
        return new TotalAttachmentsPerPageQueryResult(result);
    }
}

