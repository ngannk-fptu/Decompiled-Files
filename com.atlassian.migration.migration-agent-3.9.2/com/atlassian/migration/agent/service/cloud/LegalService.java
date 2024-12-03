/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 */
package com.atlassian.migration.agent.service.cloud;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;

@ParametersAreNonnullByDefault
public class LegalService {
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final Supplier<PluginSettings> pluginSettingsSupplier;
    private String rememberLegalOptInKey;

    public LegalService(MigrationAgentConfiguration migrationAgentConfiguration, PluginSettingsFactory pluginSettingsFactory) {
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.pluginSettingsSupplier = () -> ((PluginSettingsFactory)pluginSettingsFactory).createGlobalSettings();
    }

    @PostConstruct
    public void initialize() {
        this.rememberLegalOptInKey = String.format("%s:%s", this.migrationAgentConfiguration.getPluginKey(), "rememberLegalOptIn");
    }

    public void rememberLegalOptIn() {
        this.pluginSettingsSupplier.get().put(this.rememberLegalOptInKey, (Object)"true");
    }

    public void forgetLegalOptIn() {
        this.pluginSettingsSupplier.get().remove(this.rememberLegalOptInKey);
    }

    public boolean getRememberLegalOptIn() {
        Object value = this.pluginSettingsSupplier.get().get(this.rememberLegalOptInKey);
        return Boolean.parseBoolean((String)value);
    }
}

