/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.TotalAttachmentsSizeQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.math.BigDecimal;

public class TotalAttachmentsSizeQuery
implements GrQuery<TotalAttachmentsSizeQueryResult>,
L1AssessmentQuery<TotalAttachmentsSizeQueryResult> {
    private static final String PROPERTY_NAME = "propertyname";
    private static final String FILESIZE = "FILESIZE";
    private final EntityManagerTemplate tmpl;

    public TotalAttachmentsSizeQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.TOTAL_ATTACHMENTS_SIZE_GB.name();
    }

    @Override
    public TotalAttachmentsSizeQueryResult execute() {
        String query = "select cast(sum(longval)/1024/1024/1024 as big_decimal) from ContentProperty cp where cp.name = :propertyname";
        BigDecimal size = this.tmpl.query(BigDecimal.class, query).param(PROPERTY_NAME, (Object)FILESIZE).single();
        return new TotalAttachmentsSizeQueryResult(size);
    }
}

