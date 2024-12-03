/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface AuditEntityDao {
    @Nonnull
    default public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest) {
        return this.findBy(query, pageRequest, Integer.MAX_VALUE);
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery var1, @Nonnull PageRequest<AuditEntityCursor> var2, int var3);

    public void stream(@Nonnull AuditQuery var1, @Nonnull Consumer<AuditEntity> var2, int var3, int var4);

    public void save(@Nonnull List<AuditEntity> var1);

    public void save(@Nonnull AuditEntity var1);

    public void removeBefore(Instant var1);

    public int fastCountEstimate();

    public int count();

    public int count(@Nonnull AuditQuery var1);

    public void retainRecent(int var1);
}

