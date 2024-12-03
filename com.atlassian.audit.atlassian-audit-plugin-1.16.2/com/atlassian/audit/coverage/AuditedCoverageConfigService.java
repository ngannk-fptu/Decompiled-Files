/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.SalAuditCoverageConfigService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.audit.entity.EffectiveCoverageLevel;

public class AuditedCoverageConfigService
implements InternalAuditCoverageConfigService {
    public static final AuditType AUDIT_CONFIG_UPDATED = AuditType.fromI18nKeys((CoverageArea)CoverageArea.AUDIT_LOG, (CoverageLevel)CoverageLevel.BASE, (String)"atlassian.audit.event.category.audit", (String)"atlassian.audit.event.action.audit.config.updated").build();
    private final InternalAuditCoverageConfigService delegate;
    private final AuditService auditService;

    public AuditedCoverageConfigService(InternalAuditCoverageConfigService delegate, AuditService auditService) {
        this.delegate = delegate;
        this.auditService = auditService;
    }

    public AuditCoverageConfig getConfig() {
        return this.delegate.getConfig();
    }

    @Override
    public void updateConfig(AuditCoverageConfig config) {
        AuditCoverageConfig oldConfig = this.delegate.getConfig();
        this.delegate.updateConfig(config);
        config.getLevelByArea().forEach((area, level) -> {
            EffectiveCoverageLevel oldLevel = (EffectiveCoverageLevel)oldConfig.getLevelByArea().get(area);
            if (!level.equals((Object)oldLevel)) {
                this.auditService.audit(AuditEvent.builder((AuditType)AUDIT_CONFIG_UPDATED).changedValue(ChangedValue.fromI18nKeys((String)"atlassian.audit.event.change.coverage.level").from(String.format("%s : %s", SalAuditCoverageConfigService.areaToString(area), SalAuditCoverageConfigService.levelToString(oldLevel))).to(String.format("%s : %s", SalAuditCoverageConfigService.areaToString(area), SalAuditCoverageConfigService.levelToString(level))).build()).build());
            }
        });
    }
}

