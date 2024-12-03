/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.cacheanalytics;

import com.atlassian.confluence.plugin.cacheanalytics.CacheStatisticsEventFactory;
import com.atlassian.confluence.plugin.cacheanalytics.events.CacheStatisticsEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cacheStatsJob")
public class CacheStatisticsJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(CacheStatisticsJob.class);
    private final EventPublisher eventPublisher;
    private final CacheStatisticsEventFactory eventFactory;

    @Autowired
    CacheStatisticsJob(@ComponentImport EventPublisher eventPublisher, CacheStatisticsEventFactory eventFactory) {
        this.eventPublisher = eventPublisher;
        this.eventFactory = eventFactory;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        Collection<CacheStatisticsEvent> events = this.eventFactory.createEvents();
        log.info("Publishing {} managed cache stats events", (Object)events.size());
        events.forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        return JobRunnerResponse.success();
    }
}

