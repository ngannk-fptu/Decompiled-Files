/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.entity.AuditResource;
import java.util.List;
import javax.annotation.Nonnull;

public interface AffectedObjectsSerializer {
    public List<AuditResource> deserialize(@Nonnull String var1);

    public String serialize(@Nonnull List<AuditResource> var1);
}

