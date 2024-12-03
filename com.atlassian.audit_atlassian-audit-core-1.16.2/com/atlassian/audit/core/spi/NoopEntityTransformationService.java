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
package com.atlassian.audit.core.spi;

import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.spi.entity.AuditEntityTransformationService;
import java.util.List;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopEntityTransformationService
implements AuditEntityTransformationService {
    private static final Logger log = LoggerFactory.getLogger(NoopEntityTransformationService.class);

    @Nonnull
    public List<AuditEntity> transform(@Nonnull List<AuditEntity> entities) {
        log.debug("Using Noop entity transformation service, returning entities as they are");
        return entities;
    }
}

