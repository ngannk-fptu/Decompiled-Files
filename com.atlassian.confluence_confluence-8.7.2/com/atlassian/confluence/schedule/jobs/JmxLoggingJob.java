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
 */
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.confluence.schedule.jobs.JmxLoggingHelper;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxLoggingJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger((String)"jmx-logger");
    private final JmxLoggingHelper helper = new JmxLoggingHelper();
    private final List<JmxLoggingHelper.InstrumentQuery> queries = this.helper.readQueriesFromConfig("jmx-log-config.json", log);

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.queries.forEach(instrumentQuery -> this.helper.logQueryResults((JmxLoggingHelper.InstrumentQuery)instrumentQuery, log));
        return JobRunnerResponse.success((String)"JMX instruments have been flushed to atlassian-confluence-jmx.log");
    }
}

