/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.webdav.job;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionStore;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ParametersAreNonnullByDefault
public class ConfluenceDavSessionInvalidatorJob
implements JobRunner {
    private final ConfluenceDavSessionStore confluenceDavSessionStore;

    @Autowired
    public ConfluenceDavSessionInvalidatorJob(ConfluenceDavSessionStore confluenceDavSessionStore) {
        this.confluenceDavSessionStore = Objects.requireNonNull(confluenceDavSessionStore);
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.confluenceDavSessionStore.invalidateExpiredSessions();
        return null;
    }
}

