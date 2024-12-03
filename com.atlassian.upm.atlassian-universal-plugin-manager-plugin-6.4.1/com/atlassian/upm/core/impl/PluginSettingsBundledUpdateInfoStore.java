/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.map.MappingJsonFactory
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.BundledUpdateInfo;
import com.atlassian.upm.core.BundledUpdateInfoStore;
import java.util.Iterator;
import java.util.Objects;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsBundledUpdateInfoStore
implements BundledUpdateInfoStore {
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsBundledUpdateInfoStore.class);
    private static final String KEY = "com.atlassian.upm.core.impl.PluginSettingsBundledUpdateInfoStore";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ObjectMapper objectMapper;

    public PluginSettingsBundledUpdateInfoStore(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.objectMapper = new ObjectMapper((JsonFactory)new MappingJsonFactory());
    }

    @Override
    public Option<BundledUpdateInfo> getUpdateInfo() {
        Iterator<Object> iterator = Option.option(this.getPluginSettings().get(KEY)).iterator();
        if (iterator.hasNext()) {
            Object data = iterator.next();
            try {
                return Option.some(this.objectMapper.readValue(data.toString(), BundledUpdateInfo.class));
            }
            catch (Exception e) {
                log.warn("Unexpected error while trying to read BundledUpdateInfo: " + e);
                log.debug(e.toString(), (Throwable)e);
                return Option.none();
            }
        }
        return Option.none();
    }

    @Override
    public void setUpdateInfo(Option<BundledUpdateInfo> optInfo) {
        for (BundledUpdateInfo info : optInfo) {
            try {
                this.getPluginSettings().put(KEY, (Object)this.objectMapper.writeValueAsString((Object)info));
            }
            catch (Exception e) {
                log.warn("Unexpected error while trying to store BundledUpdateInfo: " + e);
                log.debug(e.toString(), (Throwable)e);
            }
        }
        if (!optInfo.isDefined()) {
            this.getPluginSettings().remove(KEY);
        }
    }

    private PluginSettings getPluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }
}

