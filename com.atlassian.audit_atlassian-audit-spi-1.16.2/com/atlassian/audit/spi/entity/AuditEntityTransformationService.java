/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.spi.entity;

import com.atlassian.audit.entity.AuditEntity;
import java.util.List;
import javax.annotation.Nonnull;

public interface AuditEntityTransformationService {
    @Nonnull
    public List<AuditEntity> transform(@Nonnull List<AuditEntity> var1);
}

