/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.jobs;

import com.atlassian.confluence.mail.jobs.DailyReportManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyReportJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(DailyReportJob.class);
    private final DailyReportManager dailyReportManager;

    public DailyReportJob(DailyReportManager dailyReportManager) {
        this.dailyReportManager = dailyReportManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("DailyReportJob Starting");
        this.dailyReportManager.generateDailyReports();
        log.info("DailyReportJob Completed");
        return null;
    }
}

