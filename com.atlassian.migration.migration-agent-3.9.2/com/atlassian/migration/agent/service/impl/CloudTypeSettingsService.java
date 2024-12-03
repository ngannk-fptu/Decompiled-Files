/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.PostConstruct
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.config.url.MigrationEnvironment;
import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.DefaultTypeSettings;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventConsumer;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.CloudSettingsException;
import com.atlassian.migration.agent.service.impl.MigrationSettingsType;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudTypeSettingsService
extends DefaultTypeSettings {
    private static final TypeReference<Map<CloudType, Boolean>> MAP_TYPE_REFERENCE = new TypeReference<Map<CloudType, Boolean>>(){};
    private static final Logger log = LoggerFactory.getLogger(CloudTypeSettingsService.class);
    private final PlanService planService;
    private final CheckResultsService checkResultsService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AnalyticsEventConsumer analyticsEventConsumer;
    private final CloudSiteService cloudSiteService;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final PlanDecoratorService planDecoratorService;

    public CloudTypeSettingsService(PluginSettingsFactory pluginSettingsFactory, MigrationAgentConfiguration migrationAgentConfiguration, PlanService planService, CheckResultsService checkResultsService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, AnalyticsEventConsumer analyticsEventConsumer, CloudSiteService cloudSiteService, PlanDecoratorService planDecoratorService) {
        super(pluginSettingsFactory, MigrationSettingsType.CLOUD_TYPE);
        this.planService = planService;
        this.checkResultsService = checkResultsService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.analyticsEventConsumer = analyticsEventConsumer;
        this.cloudSiteService = cloudSiteService;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.planDecoratorService = planDecoratorService;
    }

    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();
        this.setMigrationConfigurationUrlProvider(this.getCloudTypeSettings());
    }

    public Map<CloudType, Boolean> getCloudTypeSettings() {
        return (Map)this.getSettings();
    }

    public void setCloudTypeSettings(Map<CloudType, Boolean> cloudTypeMap) {
        if (this.checkResultsService.hasRunningPreflights()) {
            log.warn("Cannot change environment while preflights are running");
            throw new CloudSettingsException("Cannot change environment while preflights are running");
        }
        if (this.planService.hasPlansRunningOrStopping()) {
            log.warn("Cannot change environment while plans are running/stopping");
            throw new CloudSettingsException("Cannot change environment while plans are running/stopping");
        }
        if (this.planDecoratorService.hasAppMigrationInProgress()) {
            log.warn("Cannot change environment while app migrations are running");
            throw new CloudSettingsException("Cannot change environment while app migrations are running");
        }
        boolean settingsSuccessfullyUpdated = this.putSettings(cloudTypeMap);
        if (settingsSuccessfullyUpdated) {
            String cloudTypeMapStr = Jsons.valueAsString(cloudTypeMap);
            this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildUpdatedCloudTypeSettingsAnalyticEvent(cloudTypeMapStr)));
            this.analyticsEventConsumer.triggerJobAndDeleteRemainingEvents();
            this.setMigrationConfigurationUrlProvider(cloudTypeMap);
            log.info("Successfully set the environment from settings to {}", (Object)cloudTypeMapStr);
            this.cloudSiteService.markAllTokensAsFailed();
        }
    }

    public void setMigrationConfigurationUrlProvider(Map<CloudType, Boolean> cloudTypeMap) {
        if (cloudTypeMap.get((Object)CloudType.FEDRAMP).booleanValue()) {
            this.migrationAgentConfiguration.setUrlProvider(MigrationEnvironment.FEDRAMP);
        } else {
            this.migrationAgentConfiguration.setUrlProvider(MigrationEnvironment.DEFAULT);
        }
    }

    public boolean isCloudTypeEnabled(CloudType cloudType) {
        return this.getCloudTypeSettings().get((Object)cloudType);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected boolean isValidSettings(Object settingsObj) {
        Map cloudTypeMap = (Map)settingsObj;
        if (cloudTypeMap == null) return false;
        if (cloudTypeMap.isEmpty()) return false;
        if (!Arrays.stream(CloudType.values()).allMatch(cloudTypeMap::containsKey)) return false;
        if (cloudTypeMap.values().stream().allMatch(value -> value == false)) return false;
        if (cloudTypeMap.values().stream().filter(Boolean::booleanValue).limit(2L).count() != 1L) return false;
        return true;
    }

    @Override
    public Object mapStringToObject(String cloudTypeMap) {
        return Jsons.readValue(cloudTypeMap, MAP_TYPE_REFERENCE);
    }

    @Override
    protected Object getDefaultPluginSettings() {
        return ImmutableMap.of((Object)((Object)CloudType.FEDRAMP), (Object)false, (Object)((Object)CloudType.STANDARD), (Object)true);
    }
}

