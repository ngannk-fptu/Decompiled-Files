/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.policy;

import com.atlassian.audit.broker.AuditPolicy;
import com.atlassian.audit.coverage.CachingAuditCoverageService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoverageBasedAuditPolicy
implements AuditPolicy {
    private static final Logger log = LoggerFactory.getLogger(CoverageBasedAuditPolicy.class);
    private final CachingAuditCoverageService coverageConfigService;

    public CoverageBasedAuditPolicy(CachingAuditCoverageService coverageConfigService) {
        this.coverageConfigService = coverageConfigService;
    }

    @Override
    public boolean pass(@Nonnull AuditEntity entity) {
        AuditCoverageConfig currentConfig = this.coverageConfigService.getConfig();
        AuditType type = entity.getAuditType();
        EffectiveCoverageLevel configuredLevel = (EffectiveCoverageLevel)currentConfig.getLevelByArea().get(type.getArea());
        boolean shouldAllow = configuredLevel.shouldAllow(type.getLevel());
        log.trace("#pass shouldAllow={}, entity={}, currentConfig={}, type={}, configuredLevel={}", new Object[]{shouldAllow, entity, currentConfig, type, configuredLevel});
        return shouldAllow;
    }
}

