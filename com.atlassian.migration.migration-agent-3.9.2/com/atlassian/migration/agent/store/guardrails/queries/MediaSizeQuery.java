/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails.queries;

import com.atlassian.migration.agent.store.guardrails.GrQuery;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.QueryIds;
import com.atlassian.migration.agent.store.guardrails.results.MediaSizeQueryResult;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.math.BigDecimal;

public class MediaSizeQuery
implements GrQuery<MediaSizeQueryResult>,
L1AssessmentQuery<MediaSizeQueryResult> {
    private final EntityManagerTemplate tmpl;

    public MediaSizeQuery(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public String getQueryId() {
        return QueryIds.MEDIA_SIZE.name();
    }

    @Override
    public MediaSizeQueryResult execute() {
        String query = "select cast(sum(longval)/1024/1024/1024 as big_decimal) from ContentProperty s where s.name = 'FILESIZE'  and exists (select c.id from ContentProperty c where c.content = s.content and c.name = 'FILESTORE_ID')";
        BigDecimal size = this.tmpl.query(BigDecimal.class, query).single();
        return new MediaSizeQueryResult(size);
    }
}

