/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.TotalAttachmentsQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;

public class TotalAttachmentsQuery
implements GrQuery<TotalAttachmentsQueryResult>,
L1AssessmentQuery<TotalAttachmentsQueryResult> {
    private static final String TYPE = "type";
    private static final String ATTACHMENT_KEY = "ATTACHMENT";
    private final EntityManagerTemplate tmpl;

    public TotalAttachmentsQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_ATTACHMENTS.name();
    }

    @Override
    public TotalAttachmentsQueryResult execute() {
        String query = "select count(co.id) from Content co where co.type=:type";
        Long size = this.tmpl.query(Long.class, query).param(TYPE, (Object)ATTACHMENT_KEY).single();
        return new TotalAttachmentsQueryResult(size);
    }
}

