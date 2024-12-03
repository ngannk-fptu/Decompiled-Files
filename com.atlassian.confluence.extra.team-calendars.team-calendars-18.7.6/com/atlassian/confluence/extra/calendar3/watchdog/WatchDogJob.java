/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.watchdog;

import com.atlassian.confluence.extra.calendar3.watchdog.WatchDogActivator;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WatchDogJob
implements JobRunner {
    private static Logger LOGGER = LoggerFactory.getLogger(WatchDogJob.class);
    private final WatchDogActivator watchDogActivator;
    private boolean skipRun = true;

    @Autowired
    public WatchDogJob(WatchDogActivator watchDogActivator) {
        this.watchDogActivator = watchDogActivator;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if (this.skipRun) {
            LOGGER.debug("Skip running watch dog for the first time");
            this.skipRun = false;
            return JobRunnerResponse.success();
        }
        this.watchDogActivator.startWatchDog();
        return JobRunnerResponse.success();
    }
}

