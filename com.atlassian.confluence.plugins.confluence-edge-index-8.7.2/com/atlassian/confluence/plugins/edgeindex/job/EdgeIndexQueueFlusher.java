/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent
 *  com.atlassian.confluence.search.FlushStatistics
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Effect
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.util.concurrent.atomic.AtomicInteger
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex.job;

import com.atlassian.confluence.event.events.search.EdgeIndexQueueFlushCompleteEvent;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTask;
import com.atlassian.confluence.plugins.edgeindex.EdgeIndexTaskQueue;
import com.atlassian.confluence.plugins.edgeindex.EdgeSearchIndexAccessor;
import com.atlassian.confluence.search.FlushStatistics;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Effect;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.util.concurrent.atomic.AtomicInteger;
import java.util.Date;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="flushEdgeIndexQueueJob")
public class EdgeIndexQueueFlusher
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(EdgeIndexQueueFlusher.class);
    private final EdgeIndexTaskQueue queue;
    private final EdgeSearchIndexAccessor edgeSearchIndexAccessor;
    private final EventPublisher eventPublisher;

    @Autowired
    public EdgeIndexQueueFlusher(EdgeIndexTaskQueue queue, EdgeSearchIndexAccessor edgeSearchIndexAccessor, EventPublisher eventPublisher) {
        this.queue = queue;
        this.edgeSearchIndexAccessor = edgeSearchIndexAccessor;
        this.eventPublisher = eventPublisher;
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        log.debug("Flushing edge index queue...");
        AtomicInteger processedTaskCount = new AtomicInteger();
        FlushStatistics currentFlushStats = new FlushStatistics();
        currentFlushStats.setStarted(new Date());
        this.edgeSearchIndexAccessor.withBatchUpdate(() -> this.queue.processEntries((Effect<EdgeIndexTask>)((Effect)indexTask -> {
            this.edgeSearchIndexAccessor.execute(arg_0 -> ((EdgeIndexTask)indexTask).perform(arg_0));
            processedTaskCount.incrementAndGet();
        })));
        currentFlushStats.setQueueSize((long)processedTaskCount.get());
        if (processedTaskCount.get() > 0) {
            currentFlushStats.setRecreated(false);
            currentFlushStats.setFinished(new Date());
            this.eventPublisher.publish((Object)new EdgeIndexQueueFlushCompleteEvent((Object)this, currentFlushStats));
            log.debug("Flushed {} items in {} milliseconds", (Object)processedTaskCount.get(), (Object)currentFlushStats.getElapsedMilliseconds());
        } else {
            log.debug("There were no tasks on the index queue");
        }
        return null;
    }
}

