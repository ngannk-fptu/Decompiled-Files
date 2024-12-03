/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.analytics;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.analytics.StatisticsEvent;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.storage.config.dao.ClientConfigStore;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import com.atlassian.oauth2.common.jobs.LifecycleAwareJob;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsCollectionService
extends LifecycleAwareJob {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCollectionService.class);
    private static final JobId JOB_ID = JobId.of((String)StatisticsCollectionService.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)StatisticsCollectionService.class.getSimpleName());
    private static final String SCHEDULE_EVERY_DAY_AT_11_PM = "0 0 23 * * ?";
    private final ClientConfigStore clientConfigStore;
    private final ClientTokenStore clientTokenStore;
    private final ApplicationProperties applicationProperties;
    private final EventPublisher eventPublisher;

    public StatisticsCollectionService(ClientConfigStore clientConfigStore, ClientTokenStore clientTokenStore, ApplicationProperties applicationProperties, EventPublisher eventPublisher, SchedulerService schedulerService) {
        super(schedulerService);
        this.clientConfigStore = clientConfigStore;
        this.clientTokenStore = clientTokenStore;
        this.applicationProperties = applicationProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected JobRunnerResponse job() {
        logger.info("Collecting usage statistics of OAuth 2.0 Client Plugin");
        String platformId = this.applicationProperties.getPlatformId();
        Map<String, ProviderType> configTypes = this.fetchConfigTypes();
        Map<String, Long> numberOfConfigsByType = this.countConfigsByType(configTypes);
        Map<String, Long> numberOfTokensByType = this.countTokensByType(configTypes);
        this.eventPublisher.publish((Object)new StatisticsEvent(platformId, numberOfConfigsByType, numberOfTokensByType));
        return JobRunnerResponse.success();
    }

    private Map<String, ProviderType> fetchConfigTypes() {
        return this.clientConfigStore.list().stream().collect(Collectors.toMap(ClientConfigurationEntity::getId, ClientConfigurationEntity::getProviderType));
    }

    private Map<String, Long> countConfigsByType(Map<String, ProviderType> configTypes) {
        return this.countByType(configTypes.values());
    }

    private Map<String, Long> countTokensByType(Map<String, ProviderType> configTypes) {
        List<ClientTokenEntity> tokens = this.clientTokenStore.list();
        List<ProviderType> tokenTypes = tokens.stream().map(ClientTokenEntity::getConfigId).filter(configTypes::containsKey).map(configTypes::get).collect(Collectors.toList());
        return this.countByType(tokenTypes);
    }

    private Map<String, Long> countByType(Collection<ProviderType> types) {
        Map<String, Long> result = types.stream().collect(Collectors.groupingBy(ProviderType::getKey, Collectors.counting()));
        Arrays.stream(ProviderType.values()).forEach(providerType -> result.putIfAbsent(providerType.key, 0L));
        result.put("total", Long.valueOf(types.size()));
        return result;
    }

    @Override
    protected JobId getJobId() {
        return JOB_ID;
    }

    @Override
    protected JobRunnerKey getJobRunnerKey() {
        return JOB_RUNNER_KEY;
    }

    @Override
    protected JobConfig getJobConfig() {
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)SCHEDULE_EVERY_DAY_AT_11_PM)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }
}

