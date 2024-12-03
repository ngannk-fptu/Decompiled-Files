/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.spi.entity.AuditEntityTransformationService
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.transform;

import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.spi.entity.AuditEntityTransformationService;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorIgnoredTransformationService
implements AuditEntityTransformationService {
    private static final Logger log = LoggerFactory.getLogger(ErrorIgnoredTransformationService.class);
    private final AuditEntityTransformationService delegate;

    public ErrorIgnoredTransformationService(AuditEntityTransformationService delegate) {
        this.delegate = delegate;
    }

    @Nonnull
    public List<AuditEntity> transform(@Nonnull List<AuditEntity> auditEntities) {
        try {
            auditEntities = this.delegate.transform(auditEntities);
        }
        catch (RuntimeException e) {
            log.error("Failed to invoke AuditEntityTransformationService.", (Throwable)e);
        }
        return auditEntities;
    }
}

