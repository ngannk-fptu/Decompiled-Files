/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.retention;

import com.atlassian.audit.analytics.RetentionFileConfigUpdatedEvent;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.coverage.AuditedCoverageConfigService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.file.AuditRetentionFileConfig;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SalAuditRetentionFileConfigService
implements AuditRetentionFileConfigService {
    public static final int DEFAULT_MAX_FILE_COUNT = 100;
    public static final int DEFAULT_MAX_FILE_SIZE_IN_MB = 100;
    private static final String PLUGIN_KEY_FILE_MAX_COUNT = "com.atlassian.audit.plugin:audit-config:retention:max:file:count";
    private final EventPublisher eventPublisher;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final AuditPluginInfo auditPluginInfo;
    private final AuditService auditService;
    private final PropertiesProvider propertiesProvider;

    public SalAuditRetentionFileConfigService(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo, PluginSettingsFactory pluginSettingsFactory, AuditService auditService, PropertiesProvider propertiesProvider) {
        this.eventPublisher = eventPublisher;
        this.auditPluginInfo = auditPluginInfo;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.auditService = auditService;
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    @Nonnull
    public AuditRetentionFileConfig getConfig() {
        return new AuditRetentionFileConfig(this.getMaxFileCount(), this.propertiesProvider.getInteger("plugin.audit.file.max.file.size", 100));
    }

    private int getMaxFileCount() {
        return Optional.ofNullable((String)this.getPluginSettings().get(PLUGIN_KEY_FILE_MAX_COUNT)).map(Integer::parseInt).orElse(this.propertiesProvider.getInteger("plugin.audit.file.max.file.count", 100));
    }

    @Override
    public void updateConfig(@Nonnull AuditRetentionFileConfig newAuditFileConfig) {
        Objects.requireNonNull(newAuditFileConfig, "auditRetentionFileConfig");
        AuditRetentionFileConfig oldAuditFileConfig = this.getConfig();
        if (!newAuditFileConfig.equals(oldAuditFileConfig)) {
            int oldFileCountLimit = oldAuditFileConfig.getMaxFileCount();
            int newFileCountLimit = newAuditFileConfig.getMaxFileCount();
            PluginSettings pluginSettings = this.getPluginSettings();
            pluginSettings.put(PLUGIN_KEY_FILE_MAX_COUNT, (Object)Integer.toString(newFileCountLimit));
            this.auditService.audit(AuditEvent.builder((AuditType)AuditedCoverageConfigService.AUDIT_CONFIG_UPDATED).changedValue(ChangedValue.fromI18nKeys((String)"atlassian.audit.event.change.retention.file").from(String.format("%d", oldFileCountLimit)).to(String.format("%d", newFileCountLimit)).build()).build());
            this.eventPublisher.publish((Object)new RetentionFileConfigUpdatedEvent(oldFileCountLimit, newFileCountLimit, this.auditPluginInfo.getPluginVersion()));
        }
    }

    private PluginSettings getPluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }
}

