/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider
 *  com.atlassian.confluence.api.model.audit.RetentionPeriod
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.audit.migration;

import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider;
import com.atlassian.confluence.api.impl.service.audit.adapter.AdapterUtils;
import com.atlassian.confluence.api.model.audit.RetentionPeriod;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceAuditRetentionConfigProvider
implements LegacyRetentionConfigProvider {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAuditRetentionConfigProvider.class);
    private final SettingsManager settingsManager;

    public ConfluenceAuditRetentionConfigProvider(SettingsManager settingsManager) {
        this.settingsManager = Objects.requireNonNull(settingsManager);
    }

    public Optional<AuditRetentionConfig> get() {
        try {
            RetentionPeriod currentRetentionPeriod = this.getRetentionPeriod();
            return Optional.of(new AuditRetentionConfig(AdapterUtils.toPeriod(currentRetentionPeriod)));
        }
        catch (RuntimeException rte) {
            log.warn("Error getting current audit retention config. Default value will be used.", (Throwable)rte);
            return Optional.empty();
        }
    }

    private RetentionPeriod getRetentionPeriod() {
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        return RetentionPeriod.of((int)globalSettings.getAuditLogRetentionNumber(), (ChronoUnit)ChronoUnit.valueOf(globalSettings.getAuditLogRetentionUnit().toUpperCase()));
    }
}

