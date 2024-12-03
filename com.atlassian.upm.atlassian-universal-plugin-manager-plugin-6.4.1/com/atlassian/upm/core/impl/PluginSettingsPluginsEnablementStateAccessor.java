/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.io.IOUtils
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginsEnablementState;
import com.atlassian.upm.core.PluginsEnablementStateAccessor;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.core.util.Base64Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsPluginsEnablementStateAccessor
implements PluginsEnablementStateAccessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginSettingsPluginsEnablementStateAccessor.class);
    private static final String KEY_PREFIX = "com.atlassian.upm.ConfigurationStore:configuration:";
    protected static final String UPM_CONFIGURATION_KEY = "upm_configuration";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ObjectMapper mapper;
    private final Base64Util base64Util;

    public PluginSettingsPluginsEnablementStateAccessor(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.mapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
        this.base64Util = new Base64Util();
        this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean hasSavedConfiguration() throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException {
        return this.getSavedConfiguration().isDefined();
    }

    @Override
    public Option<PluginsEnablementState> getSavedConfiguration() throws PluginsEnablementStateAccessor.PluginsEnablementStateStoreException {
        try {
            Iterator<String> iterator = this.getConfigurationString().iterator();
            if (iterator.hasNext()) {
                String val = iterator.next();
                return this.getConfigurationFromString(val);
            }
        }
        catch (Exception e) {
            throw new PluginsEnablementStateAccessor.PluginsEnablementStateStoreException("Unable to read saved configuration", e);
        }
        return Option.none(PluginsEnablementState.class);
    }

    protected Option<String> getConfigurationString() {
        for (Object val : Option.option(this.getPluginSettings().get(UPM_CONFIGURATION_KEY))) {
            if (!(val instanceof String)) continue;
            return Option.some(String.valueOf(val));
        }
        return Option.none();
    }

    private Option<PluginsEnablementState> getConfigurationFromString(String value) throws IOException {
        String decompressedValue;
        byte[] bytes = this.base64Util.tryDecodeBase64(value);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream gzipIn = new GZIPInputStream(bis);){
            decompressedValue = IOUtils.toString((InputStream)gzipIn, (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException error) {
            return Option.some(this.mapper.readValue(value, PluginsEnablementState.class));
        }
        return Option.some(this.mapper.readValue(decompressedValue, PluginsEnablementState.class));
    }

    protected PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }

    protected ObjectMapper getMapper() {
        return this.mapper;
    }

    protected Base64Util getBase64Util() {
        return this.base64Util;
    }
}

