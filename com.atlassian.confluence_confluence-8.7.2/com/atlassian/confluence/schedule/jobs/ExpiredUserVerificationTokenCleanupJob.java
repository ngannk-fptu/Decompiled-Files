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
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpiredUserVerificationTokenCleanupJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(ExpiredUserVerificationTokenCleanupJob.class);
    private final UserVerificationTokenManager userVerificationTokenManager;

    public ExpiredUserVerificationTokenCleanupJob(UserVerificationTokenManager userVerificationTokenManager) {
        this.userVerificationTokenManager = userVerificationTokenManager;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.info("Cleaning up expired user verification tokens...");
        long startTime = Instant.now().toEpochMilli();
        int totalExpiredTokens = this.userVerificationTokenManager.clearAllExpiredTokens();
        log.info("{} expired user verification tokens deleted, total time: {} ms", (Object)totalExpiredTokens, (Object)(Instant.now().toEpochMilli() - startTime));
        return JobRunnerResponse.success((String)(totalExpiredTokens + " expired user verification tokens have been cleaned up."));
    }
}

