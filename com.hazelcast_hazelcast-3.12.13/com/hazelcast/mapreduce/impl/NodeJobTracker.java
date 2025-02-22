/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.internal.util.RuntimeAvailableProcessors;
import com.hazelcast.logging.ILogger;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.task.KeyValueJob;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.executor.ExecutorType;
import java.util.concurrent.CopyOnWriteArrayList;

class NodeJobTracker
extends AbstractJobTracker {
    private final CopyOnWriteArrayList<String> cancelledJobs = new CopyOnWriteArrayList();

    NodeJobTracker(String name, JobTrackerConfig jobTrackerConfig, NodeEngine nodeEngine, MapReduceService mapReduceService) {
        super(name, jobTrackerConfig, nodeEngine, mapReduceService);
        int queueSize;
        ExecutionService es = nodeEngine.getExecutionService();
        IPartitionService ps = nodeEngine.getPartitionService();
        int maxThreadSize = jobTrackerConfig.getMaxThreadSize();
        if (maxThreadSize <= 0) {
            maxThreadSize = RuntimeAvailableProcessors.get();
        }
        if ((queueSize = jobTrackerConfig.getQueueSize()) <= 0) {
            queueSize = ps.getPartitionCount() * 2;
        }
        try {
            String executorName = MapReduceUtil.buildExecutorName(name);
            es.register(executorName, maxThreadSize, queueSize, ExecutorType.CACHED);
        }
        catch (Exception ignore) {
            ILogger logger = nodeEngine.getLogger(NodeJobTracker.class);
            logger.finest("This is likely happened due to a previously cancelled job", ignore);
        }
    }

    @Override
    public <K, V> Job<K, V> newJob(KeyValueSource<K, V> source) {
        return new KeyValueJob<K, V>(this.name, this, this.nodeEngine, this.mapReduceService, source);
    }

    public boolean registerJobSupervisorCancellation(String jobId) {
        return this.cancelledJobs.addIfAbsent(jobId);
    }

    public boolean unregisterJobSupervisorCancellation(String jobId) {
        return this.cancelledJobs.remove(jobId);
    }
}

