/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.PostConstruct
 *  org.codehaus.jackson.type.TypeReference
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.dto.ConcurrencySettingsEnum;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.DefaultTypeSettings;
import com.atlassian.migration.agent.service.impl.MigrationSettingsType;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.codehaus.jackson.type.TypeReference;

public class ConcurrencySettingsService
extends DefaultTypeSettings {
    private static final TypeReference<HashMap<ConcurrencySettingsEnum, Integer>> MAP_TYPE_REFERENCE = new TypeReference<HashMap<ConcurrencySettingsEnum, Integer>>(){};

    public ConcurrencySettingsService(PluginSettingsFactory pluginSettingsFactory) {
        super(pluginSettingsFactory, MigrationSettingsType.CONCURRENCY);
    }

    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();
        this.updateDefaultSettings();
    }

    private void updateDefaultSettings() {
        Object currentCurrencySettings = this.getDefaultPluginSettings();
        currentCurrencySettings.putAll(this.getSettings());
        this.putSettings(currentCurrencySettings);
    }

    @Override
    public Map<ConcurrencySettingsEnum, Integer> getSettings() {
        return (Map)super.getSettings();
    }

    @Override
    public boolean putSettings(Object settingsTypeValue) {
        if (this.isValidSettings(settingsTypeValue)) {
            EnumMap currentCurrencySettings = new EnumMap(this.getSettings());
            currentCurrencySettings.putAll(settingsTypeValue);
            settingsTypeValue = currentCurrencySettings;
        }
        return super.putSettings(settingsTypeValue);
    }

    @Override
    protected boolean isValidSettings(Object settingsObj) {
        Map concurrencyMap = (Map)settingsObj;
        return concurrencyMap != null && !concurrencyMap.isEmpty();
    }

    @Override
    public Object mapStringToObject(String concurrencyMap) {
        return Jsons.readValue(concurrencyMap, MAP_TYPE_REFERENCE);
    }

    @Override
    protected Map<ConcurrencySettingsEnum, Integer> getDefaultPluginSettings() {
        return ConcurrencySettingsEnum.getDefaultMap();
    }

    private Integer getConcurrencySettingsValue(ConcurrencySettingsEnum concurrencySettingsEnum) {
        return Optional.ofNullable(this.getSettings().get((Object)concurrencySettingsEnum)).orElse(concurrencySettingsEnum.getDefaultConcurrency());
    }

    public int getSpaceUsersMigrationExecutorConcurrencyClusterMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.SPACE_USERS_CONCURRENCY_MAX);
    }

    public int getSpaceUsersMigrationConcurrencyNodeMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.SPACE_USERS_CONCURRENCY_NODE_MAX);
    }

    public int getAttachmentMigrationConcurrencyClusterMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.ATTACHMENT_CONCURRENCY_CLUSTER_MAX);
    }

    public int getAttachmentMigrationConcurrencyNodeMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.ATTACHMENT_CONCURRENCY_NODE_MAX);
    }

    public Integer getExportConcurrencyNodeMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.EXPORT_CONCURRENCY_NODE_MAX);
    }

    public Integer getExportConcurrencyClusterMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.EXPORT_CONCURRENCY_CLUSTER_MAX);
    }

    public Integer getUploadConcurrencyClusterMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.UPLOAD_CONCURRENCY_CLUSTER_MAX);
    }

    public int getUploadConcurrencyNodeMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.UPLOAD_CONCURRENCY_NODE_MAX);
    }

    public int getImportConcurrencyClusterMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.IMPORT_CONCURRENCY_CLUSTER_MAX);
    }

    public int getImportConcurrencyNodeMax() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.IMPORT_CONCURRENCY_NODE_MAX);
    }

    public int getAttachmentUploadConcurrency() {
        return this.getConcurrencySettingsValue(ConcurrencySettingsEnum.ATTACHMENT_UPLOAD_CONCURRENCY);
    }
}

