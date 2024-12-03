/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.upload;

import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.eventfilter.BlacklistFilter;
import com.atlassian.analytics.client.hash.AnalyticsEmailHasher;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.config.JobRunnerKey;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteFilterRead
implements JobRunner,
Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteFilterRead.class);
    static final JobRunnerKey KEY = JobRunnerKey.of((String)(RemoteFilterRead.class + "_JobHandlerKey"));
    private final PeriodicEventUploaderScheduler scheduler;
    private final BlacklistFilter blacklistFilter;
    private final BaseDataLogger baseDataLogger;
    private final AnalyticsConfig analyticsConfig;
    private final AnalyticsEmailHasher analyticsEmailHasher;

    public RemoteFilterRead(PeriodicEventUploaderScheduler scheduler, BlacklistFilter blacklistFilter, BaseDataLogger baseDataLogger, AnalyticsConfig analyticsConfig, AnalyticsEmailHasher analyticsEmailHasher) {
        this.scheduler = scheduler;
        this.blacklistFilter = blacklistFilter;
        this.baseDataLogger = baseDataLogger;
        this.analyticsConfig = analyticsConfig;
        this.analyticsEmailHasher = analyticsEmailHasher;
    }

    public JobRunnerResponse runJob(@Nullable JobRunnerRequest jobRunnerRequest) {
        LOG.debug("Executing analytics remote reader job.");
        this.scheduler.rescheduleRemoteReadJob();
        if (this.analyticsConfig.canCollectAnalytics()) {
            this.analyticsEmailHasher.readRemoteInstructions();
            this.blacklistFilter.readRemoteList();
            if (!this.analyticsConfig.hasLoggedBaseData()) {
                this.baseDataLogger.logBaseData();
                this.analyticsConfig.setLoggedBaseData(true);
            }
        }
        return JobRunnerResponse.success();
    }
}

