/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.analytics.LevelUpdatedEvent;
import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.coverage.SalAuditCoverageConfigService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.event.api.EventPublisher;

public class AnalyticsTrackedCoverageConfigService
implements InternalAuditCoverageConfigService {
    private final InternalAuditCoverageConfigService delegate;
    private final EventPublisher eventPublisher;
    private final AuditPluginInfo auditPluginInfo;

    public AnalyticsTrackedCoverageConfigService(InternalAuditCoverageConfigService delegate, EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
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
                this.eventPublisher.publish((Object)new LevelUpdatedEvent(SalAuditCoverageConfigService.areaToString(area), SalAuditCoverageConfigService.levelToString(oldLevel), SalAuditCoverageConfigService.levelToString(level), this.auditPluginInfo.getPluginVersion()));
            }
        });
    }
}

