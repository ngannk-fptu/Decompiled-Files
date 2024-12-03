/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogEntityType;
import javax.annotation.Nullable;

public interface AuditLogEntity {
    @Nullable
    public AuditLogEntityType getEntityType();

    @Nullable
    public Long getEntityId();

    @Nullable
    public String getEntityName();

    public boolean isPrimary();
}

