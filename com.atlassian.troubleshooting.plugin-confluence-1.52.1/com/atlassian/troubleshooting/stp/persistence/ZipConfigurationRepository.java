/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.troubleshooting.stp.persistence.ZipConfiguration;
import java.util.Optional;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ZipConfigurationRepository {
    private static final Logger LOG = LoggerFactory.getLogger(ZipConfigurationRepository.class);
    private static final String ZIP_CONFIGURATION_KEY = "com.atlassian.troubleshooting.zip.configuration.v1";
    private final ObjectMapper mapper;
    private final PluginSettings globalSettings;

    @Autowired
    public ZipConfigurationRepository(PluginSettingsFactory pluginSettingsFactory) {
        this(pluginSettingsFactory.createGlobalSettings(), new ObjectMapper());
    }

    ZipConfigurationRepository(PluginSettings globalSettings, ObjectMapper mapper) {
        this.globalSettings = globalSettings;
        this.mapper = mapper;
    }

    public void saveConfiguration(ZipConfiguration zipConfiguration) {
        try {
            String configurationJson = this.mapper.writeValueAsString((Object)zipConfiguration);
            this.globalSettings.put(ZIP_CONFIGURATION_KEY, (Object)configurationJson);
        }
        catch (Exception e) {
            LOG.error(String.format("Couldn't persist support zip creation configuration with key '%s'", ZIP_CONFIGURATION_KEY), (Throwable)e);
        }
    }

    public Optional<ZipConfiguration> getConfiguration() {
        Object storedObject = this.globalSettings.get(ZIP_CONFIGURATION_KEY);
        if (storedObject == null) {
            return Optional.empty();
        }
        try {
            String json = (String)storedObject;
            return Optional.of(this.mapper.readValue(json, ZipConfiguration.class));
        }
        catch (Exception e) {
            LOG.error(String.format("Couldn't read '%s' value from plugin settings", ZIP_CONFIGURATION_KEY), (Throwable)e);
            return Optional.empty();
        }
    }
}

