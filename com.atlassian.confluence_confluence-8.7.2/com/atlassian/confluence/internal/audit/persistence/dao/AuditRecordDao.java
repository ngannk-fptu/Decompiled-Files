/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.audit.persistence.dao;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@Transactional
public interface AuditRecordDao {
    public void storeRecord(AuditRecordEntity var1);

    @Transactional(readOnly=true)
    public PageResponse<AuditRecordEntity> getRecords(LimitedRequest var1, Instant var2, Instant var3, boolean var4, String var5);

    public List<Long> fetchAllRecordIds();

    public List<AuditRecordEntity> fetchByIds(List<Long> var1);

    public void deleteRecords(Collection<AuditRecordEntity> var1);

    public void cleanOldRecords(Instant var1);
}

