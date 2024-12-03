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
import com.atlassian.migration.agent.store.guardrails.results.TotalAttachmentsPerSpaceQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import javax.persistence.Tuple;

public class TotalAttachmentsPerSpaceQuery
implements GrQuery<TotalAttachmentsPerSpaceQueryResult>,
L1AssessmentQuery<TotalAttachmentsPerSpaceQueryResult> {
    private static final int LIMIT = 100;
    private final EntityManagerTemplate tmpl;

    public TotalAttachmentsPerSpaceQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_ATTACHMENTS_PER_SPACE.name();
    }

    @Override
    public TotalAttachmentsPerSpaceQueryResult execute() {
        String query = "select content.spaceId as space_id, count(content.id) as attachment_count \n from Content content \n where content.type = 'ATTACHMENT'\n group by content.spaceId \n order by attachment_count DESC";
        List<Tuple> result = this.tmpl.query(Tuple.class, query).max(100).list();
        return new TotalAttachmentsPerSpaceQueryResult(result);
    }
}

