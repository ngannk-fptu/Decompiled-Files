/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.querycache.QueryCacheScheduler;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.util.executor.ExecutorType;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NodeQueryCacheScheduler
implements QueryCacheScheduler {
    private static final String EXECUTOR_NAME_PREFIX = "hz:scheduled:cqc:";
    private static final int EXECUTOR_DEFAULT_QUEUE_CAPACITY = 10000;
    private final String executorName;
    private final TaskScheduler taskScheduler;
    private final ExecutionService executionService;

    public NodeQueryCacheScheduler(MapServiceContext mapServiceContext) {
        this.executionService = this.getExecutionService(mapServiceContext);
        this.executorName = EXECUTOR_NAME_PREFIX + UuidUtil.newUnsecureUuidString();
        this.executionService.register(this.executorName, 1, 10000, ExecutorType.CACHED);
        this.taskScheduler = this.executionService.getTaskScheduler(this.executorName);
    }

    private ExecutionService getExecutionService(MapServiceContext mapServiceContext) {
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        return nodeEngine.getExecutionService();
    }

    @Override
    public ScheduledFuture<?> scheduleWithRepetition(Runnable task, long delaySeconds) {
        return this.taskScheduler.scheduleWithRepetition(task, 1L, delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void execute(Runnable task) {
        this.taskScheduler.execute(task);
    }

    @Override
    public void shutdown() {
        this.executionService.shutdownExecutor(this.executorName);
    }
}

