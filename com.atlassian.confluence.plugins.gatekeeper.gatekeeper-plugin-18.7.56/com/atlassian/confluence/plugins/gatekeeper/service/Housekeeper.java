/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.confluence.plugins.gatekeeper.service.AddonGlobal;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="addonHousekeeperJob-v2")
public class Housekeeper
implements JobRunner {
    private static final Logger logger = LoggerFactory.getLogger(Housekeeper.class);
    private final AddonGlobal addonGlobal;

    @Autowired
    public Housekeeper(AddonGlobal addonGlobal) {
        this.addonGlobal = addonGlobal;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        logger.debug("running housekeeper job");
        this.addonGlobal.getEvaluationThreadPoolExecutor().cleanup();
        logger.debug("Finished housekeeper job.");
        return JobRunnerResponse.success();
    }
}

