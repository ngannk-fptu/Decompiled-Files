/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.api;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AuditSearchService {
    @Nonnull
    default public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest) throws TimeoutException {
        return this.findBy(query, pageRequest, Integer.MAX_VALUE);
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery var1, @Nonnull PageRequest<AuditEntityCursor> var2, int var3) throws TimeoutException;

    public void stream(@Nonnull AuditQuery var1, int var2, int var3, @Nonnull Consumer<AuditEntity> var4) throws TimeoutException;

    public long count(@Nullable AuditQuery var1) throws TimeoutException;
}

