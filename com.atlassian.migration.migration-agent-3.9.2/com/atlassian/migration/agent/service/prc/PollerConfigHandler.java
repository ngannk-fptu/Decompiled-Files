/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.prc.client.model.PollerConfig
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.prc.PrcCommandExecutorCallback;
import com.atlassian.migration.agent.service.prc.PrcOkHttpAdapter;
import com.atlassian.migration.agent.service.prc.PrcPollerMetadataCache;
import com.atlassian.migration.prc.client.model.PollerConfig;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PollerConfigHandler {
    private final PrcOkHttpAdapter prcOkHttpAdapter;
    private final PrcCommandExecutorCallback prcCommandExecutorCallback;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final PrcPollerMetadataCache prcPollerMetadataCache;
    private static final Integer POLLING_DELAY_IN_SEC = 5;
    private static final Integer POLLING_EXPIRY_TIME_IN_SEC = 86400;

    public PollerConfigHandler(PrcOkHttpAdapter prcOkHttpAdapter, PrcCommandExecutorCallback prcCommandExecutorCallback, MigrationAgentConfiguration migrationAgentConfiguration, PrcPollerMetadataCache prcPollerMetadataCache) {
        this.prcOkHttpAdapter = prcOkHttpAdapter;
        this.prcCommandExecutorCallback = prcCommandExecutorCallback;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.prcPollerMetadataCache = prcPollerMetadataCache;
    }

    public Integer getPollerExpiryTimeInSec() {
        return POLLING_EXPIRY_TIME_IN_SEC;
    }

    public PollerConfig getPollerConfigWithCallbacks(String channelName, String cloudId, String containerToken) {
        return new PollerConfig(channelName, cloudId, containerToken, this.prcOkHttpAdapter::post, this.migrationAgentConfiguration.getPrcHostUrl(), POLLING_DELAY_IN_SEC.intValue(), POLLING_EXPIRY_TIME_IN_SEC.intValue(), this.prcCommandExecutorCallback::execute);
    }

    public PollerConfig getPollerConfigFromJobParams(Map<String, Serializable> jobParams) {
        String channelName = (String)((Object)jobParams.get("channelName"));
        String cloudId = (String)((Object)jobParams.get("cloudId"));
        Integer pollingDelayInSec = (Integer)jobParams.get("pollingDelayInSec");
        String containerToken = this.prcPollerMetadataCache.getContainerTokenForCloudId(cloudId);
        Integer pollerExpiryTimeInSec = (Integer)jobParams.get("pollerExpiryTimeInSec");
        String prcHostUrl = (String)((Object)jobParams.get("prcHostUrl"));
        return new PollerConfig(channelName, cloudId, containerToken, this.prcOkHttpAdapter::post, prcHostUrl, pollingDelayInSec.intValue(), pollerExpiryTimeInSec.intValue(), this.prcCommandExecutorCallback::execute);
    }

    public Map<String, Serializable> getJobParametersFromConfig(PollerConfig pollerConfig) {
        HashMap<String, Serializable> jobParams = new HashMap<String, Serializable>();
        jobParams.put("channelName", (Serializable)((Object)pollerConfig.getChannelName()));
        jobParams.put("cloudId", (Serializable)((Object)pollerConfig.getCloudId()));
        jobParams.put("pollingDelayInSec", Integer.valueOf(pollerConfig.getPollingDelayInSec()));
        jobParams.put("pollerExpiryTimeInSec", Integer.valueOf(pollerConfig.getPollerExpiryTimeInSec()));
        jobParams.put("prcHostUrl", (Serializable)((Object)pollerConfig.getPrcHostUrl()));
        return jobParams;
    }
}

