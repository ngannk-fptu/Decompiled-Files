/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.api.events.AuditRetentionConfigUpdatedEvent
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.retention;

import com.atlassian.audit.analytics.RetentionUpdatedEvent;
import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.api.events.AuditRetentionConfigUpdatedEvent;
import com.atlassian.audit.coverage.AuditedCoverageConfigService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SalAuditRetentionConfigService
implements AuditRetentionConfigService {
    private static final String PLUGIN_KEY_RETENTION_PERIOD = "com.atlassian.audit.plugin:audit-config:retention:period";
    private final EventPublisher eventPublisher;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final AuditPluginInfo auditPluginInfo;
    private final AuditService auditService;

    public SalAuditRetentionConfigService(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, PluginSettingsFactory pluginSettingsFactory, AuditService auditService) {
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.auditService = auditService;
    }

    @Nonnull
    public AuditRetentionConfig getConfig() {
        Period period = Optional.ofNullable((String)this.getPluginSettings().get(PLUGIN_KEY_RETENTION_PERIOD)).map(Period::parse).orElse(AuditRetentionConfig.DEFAULT_RETENTION_PERIOD);
        return new AuditRetentionConfig(period);
    }

    public void updateConfig(@Nonnull AuditRetentionConfig auditRetentionConfig) {
        Objects.requireNonNull(auditRetentionConfig, "auditRetentionConfig");
        PluginSettings pluginSettings = this.getPluginSettings();
        String oldRetentionConfig = (String)pluginSettings.get(PLUGIN_KEY_RETENTION_PERIOD);
        pluginSettings.put(PLUGIN_KEY_RETENTION_PERIOD, (Object)auditRetentionConfig.getPeriod().toString());
        this.eventPublisher.publish((Object)new AuditRetentionConfigUpdatedEvent());
        if (!auditRetentionConfig.getPeriod().toString().equals(oldRetentionConfig)) {
            Period oldPeriod = oldRetentionConfig == null ? null : Period.parse(oldRetentionConfig);
            Period newPeriod = auditRetentionConfig.getPeriod();
            this.auditService.audit(AuditEvent.builder((AuditType)AuditedCoverageConfigService.AUDIT_CONFIG_UPDATED).changedValue(ChangedValue.fromI18nKeys((String)"atlassian.audit.event.change.retention").from(oldPeriod == null ? null : String.format("%s %s", this.getPeriodValue(oldPeriod), this.getPeriodUnit(oldPeriod))).to(String.format("%s %s", this.getPeriodValue(newPeriod), this.getPeriodUnit(newPeriod))).build()).build());
            this.eventPublisher.publish((Object)new RetentionUpdatedEvent(this.getPeriodValue(oldPeriod), this.getPeriodUnit(oldPeriod), this.getPeriodValue(newPeriod), this.getPeriodUnit(newPeriod), this.auditPluginInfo.getPluginVersion()));
        }
    }

    private PluginSettings getPluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }

    private String getPeriodUnit(Period oldPeriod) {
        return oldPeriod == null ? "" : (oldPeriod.getYears() > 0 ? ChronoUnit.YEARS.toString() : (oldPeriod.getMonths() > 0 ? ChronoUnit.MONTHS.toString() : ChronoUnit.DAYS.toString()));
    }

    private String getPeriodValue(Period oldPeriod) {
        return String.valueOf(oldPeriod == null ? "" : Integer.valueOf(oldPeriod.getYears() > 0 ? oldPeriod.getYears() : (oldPeriod.getMonths() > 0 ? oldPeriod.getMonths() : oldPeriod.getDays())));
    }
}

