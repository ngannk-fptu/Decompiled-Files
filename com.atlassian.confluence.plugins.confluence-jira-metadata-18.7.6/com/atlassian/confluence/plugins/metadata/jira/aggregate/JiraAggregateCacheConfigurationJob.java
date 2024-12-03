/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.metadata.jira.aggregate;

import com.atlassian.confluence.plugins.metadata.jira.aggregate.JiraAggregateCache;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.springframework.stereotype.Component;

@Component
public class JiraAggregateCacheConfigurationJob
implements JobRunner {
    private final JiraAggregateCache jiraAggregateCache;

    public JiraAggregateCacheConfigurationJob(JiraAggregateCache jiraAggregateCache) {
        this.jiraAggregateCache = jiraAggregateCache;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.jiraAggregateCache.configureCache();
        return JobRunnerResponse.success();
    }
}

