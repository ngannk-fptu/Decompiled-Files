/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.rememberme;

import com.atlassian.confluence.user.rememberme.ConfluenceRememberMeTokenDao;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ClearExpiredRememberMeTokensJob
implements JobRunner {
    private final ConfluenceRememberMeTokenDao rememberMeTokenDao;

    public ClearExpiredRememberMeTokensJob(ConfluenceRememberMeTokenDao rememberMeTokenDao) {
        this.rememberMeTokenDao = rememberMeTokenDao;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        this.rememberMeTokenDao.removeExpiredTokens();
        return null;
    }
}

