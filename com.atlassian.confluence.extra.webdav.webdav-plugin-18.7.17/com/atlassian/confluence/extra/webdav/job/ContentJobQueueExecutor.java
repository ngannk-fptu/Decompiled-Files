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

import com.atlassian.confluence.extra.webdav.job.ContentJobQueue;
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
public class ContentJobQueueExecutor
implements JobRunner {
    private final ContentJobQueue contentJobQueue;

    @Autowired
    public ContentJobQueueExecutor(ContentJobQueue contentJobQueue) {
        this.contentJobQueue = Objects.requireNonNull(contentJobQueue);
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.contentJobQueue.executeTasks();
        return null;
    }
}

