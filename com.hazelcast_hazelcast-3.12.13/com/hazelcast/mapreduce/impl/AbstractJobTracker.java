/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.config.JobTrackerConfig;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.MapCombineTask;
import com.hazelcast.mapreduce.impl.task.ReducerTask;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.spi.NodeEngine;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

public abstract class AbstractJobTracker
implements JobTracker {
    protected final ConcurrentMap<String, TrackableJobFuture> trackableJobs = new ConcurrentHashMap<String, TrackableJobFuture>();
    protected final ConcurrentMap<String, ReducerTask> reducerTasks = new ConcurrentHashMap<String, ReducerTask>();
    protected final ConcurrentMap<String, MapCombineTask> mapCombineTasks = new ConcurrentHashMap<String, MapCombineTask>();
    protected final NodeEngine nodeEngine;
    protected final ExecutorService executorService;
    protected final MapReduceService mapReduceService;
    protected final JobTrackerConfig jobTrackerConfig;
    protected final String name;

    AbstractJobTracker(String name, JobTrackerConfig jobTrackerConfig, NodeEngine nodeEngine, MapReduceService mapReduceService) {
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.jobTrackerConfig = jobTrackerConfig;
        this.mapReduceService = mapReduceService;
        this.executorService = nodeEngine.getExecutionService().getExecutor(name);
    }

    @Override
    public void destroy() {
    }

    @Override
    public String getPartitionKey() {
        return this.getName();
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    public JobTrackerConfig getJobTrackerConfig() {
        return this.jobTrackerConfig;
    }

    public <V> boolean registerTrackableJob(TrackableJobFuture<V> trackableJob) {
        return this.trackableJobs.putIfAbsent(trackableJob.getJobId(), trackableJob) == null;
    }

    public <V> TrackableJobFuture<V> unregisterTrackableJob(String jobId) {
        return (TrackableJobFuture)this.trackableJobs.remove(jobId);
    }

    public <V> TrackableJobFuture<V> getTrackableJob(String jobId) {
        return (TrackableJobFuture)this.trackableJobs.get(jobId);
    }

    public <Key, Chunk> void registerReducerTask(ReducerTask<Key, Chunk> reducerTask) {
        this.reducerTasks.put(reducerTask.getJobId(), reducerTask);
    }

    public ReducerTask unregisterReducerTask(String jobId) {
        return (ReducerTask)this.reducerTasks.remove(jobId);
    }

    public <Key, Chunk> ReducerTask<Key, Chunk> getReducerTask(String jobId) {
        return (ReducerTask)this.reducerTasks.get(jobId);
    }

    public <KeyIn, ValueIn, KeyOut, ValueOut, Chunk> void registerMapCombineTask(MapCombineTask<KeyIn, ValueIn, KeyOut, ValueOut, Chunk> mapCombineTask) {
        if (this.mapCombineTasks.putIfAbsent(mapCombineTask.getJobId(), mapCombineTask) == null) {
            mapCombineTask.process();
        }
    }

    public MapCombineTask unregisterMapCombineTask(String jobId) {
        return (MapCombineTask)this.mapCombineTasks.remove(jobId);
    }

    public <KeyIn, ValueIn, KeyOut, ValueOut, Chunk> MapCombineTask<KeyIn, ValueIn, KeyOut, ValueOut, Chunk> getMapCombineTask(String jobId) {
        return (MapCombineTask)this.mapCombineTasks.get(jobId);
    }
}

