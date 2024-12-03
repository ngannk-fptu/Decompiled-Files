/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.thready.manager;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.troubleshooting.thready.manager.ConfigurationPersistenceService;
import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultConfigurationPersistenceService
implements ConfigurationPersistenceService {
    public static final String ENABLED_SETTING_KEY = "com.atlassian.troubleshooting.thready.configuration.enabled";
    private final PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public DefaultConfigurationPersistenceService(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory);
    }

    @Override
    public Optional<ThreadDiagnosticsConfigurationManager.Configuration> findConfiguration() {
        return Optional.ofNullable(this.pluginSettingsFactory.createGlobalSettings().get(ENABLED_SETTING_KEY)).map(o -> new ThreadDiagnosticsConfigurationManager.Configuration(Boolean.parseBoolean((String)o)));
    }

    @Override
    public ThreadDiagnosticsConfigurationManager.Configuration storeConfiguration(@Nonnull ThreadDiagnosticsConfigurationManager.Configuration newConfiguration) {
        this.pluginSettingsFactory.createGlobalSettings().put(ENABLED_SETTING_KEY, (Object)Boolean.toString(newConfiguration.isEnabled()));
        return newConfiguration;
    }
}

