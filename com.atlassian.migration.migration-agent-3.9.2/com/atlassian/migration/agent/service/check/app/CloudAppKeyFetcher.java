/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app;

import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.MigratabliltyInfo;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudAppKeyFetcher {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CloudAppKeyFetcher.class);
    private final MigrationAppAggregatorService appAggregatorService;
    private final MigratabliltyInfo migratabliltyInfo;

    public String getCloudAppKey(String serverKey) {
        MigrationAppAggregatorResponse maaInfo = this.appAggregatorService.getCachedServerAppData(serverKey);
        String cloudAppKeyFromMaa = null;
        if (maaInfo != null) {
            cloudAppKeyFromMaa = maaInfo.getCloudKey();
        }
        Set<String> cloudAppKeys = this.migratabliltyInfo.getCloudAppKeys(serverKey, cloudAppKeyFromMaa);
        log.debug("For Server app key {} Cloud App Keys is {}.", (Object)serverKey, cloudAppKeys);
        return !cloudAppKeys.isEmpty() ? cloudAppKeys.iterator().next() : serverKey;
    }

    @Generated
    public CloudAppKeyFetcher(MigrationAppAggregatorService appAggregatorService, MigratabliltyInfo migratabliltyInfo) {
        this.appAggregatorService = appAggregatorService;
        this.migratabliltyInfo = migratabliltyInfo;
    }
}

