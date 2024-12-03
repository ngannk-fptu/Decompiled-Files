/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.troubleshooting.jfr.config.JfrServiceProductSupport;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.service.JfrSettingsService;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrSettingsService
implements JfrSettingsService,
LifecycleAware {
    public static final String JFR_ENABLED_SETTINGS_KEY = "com.atlassian.troubleshooting.jfr.settings.v2.enabled";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJfrSettingsService.class);
    private volatile boolean pluginSystemReady = false;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final Optional<JfrServiceProductSupport> jfrServiceProductSupport;

    @Autowired
    public DefaultJfrSettingsService(PluginSettingsFactory pluginSettingsFactory, ObjectProvider<JfrServiceProductSupport> jfrServiceProductSupport) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory);
        this.jfrServiceProductSupport = Optional.ofNullable(jfrServiceProductSupport.getIfAvailable());
    }

    @Override
    public boolean isPluginSystemReady() {
        return this.pluginSystemReady;
    }

    @Override
    public JfrSettings storeSettings(JfrSettings settings) {
        Objects.requireNonNull(settings);
        if (this.pluginSystemReady) {
            this.pluginSettingsFactory.createGlobalSettings().put(JFR_ENABLED_SETTINGS_KEY, (Object)Boolean.toString(settings.isEnabled()));
            LOG.debug("JFR settings stored successfully! Value set to : {}", (Object)settings.isEnabled());
        } else {
            LOG.debug("JFR settings were not stored as plugin system is not started");
        }
        return this.getSettings();
    }

    @Override
    public JfrSettings getSettings() {
        return this.getJfrSettingsFromGlobalSettings().map(String.class::cast).map(JfrSettings::new).orElseGet(this::getSettingsFromProductService);
    }

    private Optional<Object> getJfrSettingsFromGlobalSettings() {
        if (this.pluginSystemReady) {
            return Optional.ofNullable(this.pluginSettingsFactory.createGlobalSettings().get(JFR_ENABLED_SETTINGS_KEY));
        }
        return Optional.empty();
    }

    private JfrSettings getSettingsFromProductService() {
        return this.jfrServiceProductSupport.map(service -> new JfrSettings(service.isRunningByDefault())).orElseGet(() -> new JfrSettings(Boolean.FALSE.toString()));
    }

    public void onStart() {
        this.pluginSystemReady = true;
    }

    public void onStop() {
        this.pluginSystemReady = false;
    }
}

