/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditAuthor
 *  com.atlassian.audit.entity.AuditResource
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.spi.lookup;

import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditAuthor;
import com.atlassian.audit.entity.AuditResource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AuditingResourcesLookupService {
    public Page<AuditAuthor, String> lookupAuditAuthor(@Nullable String var1, @Nonnull PageRequest<String> var2);

    public Page<AuditResource, String> lookupAuditResource(@Nonnull String var1, @Nullable String var2, @Nonnull PageRequest<String> var3);
}

