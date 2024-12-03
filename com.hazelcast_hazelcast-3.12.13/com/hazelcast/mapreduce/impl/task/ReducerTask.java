/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.impl.CombinerResultList;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.MapReduceUtil;
import com.hazelcast.mapreduce.impl.notification.ReducingFinishedNotification;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.ReducerChunk;
import com.hazelcast.mapreduce.impl.task.ReducerTaskScheduler;
import com.hazelcast.nio.Address;
import com.hazelcast.util.ExceptionUtil;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReducerTask<Key, Chunk>
implements Runnable {
    private volatile boolean visibility;
    private final AtomicBoolean cancelled = new AtomicBoolean();
    private final JobSupervisor supervisor;
    private final Queue<ReducerChunk<Key, Chunk>> reducerQueue;
    private final String name;
    private final String jobId;
    private final ReducerTaskScheduler scheduler;

    public ReducerTask(String name, String jobId, JobSupervisor supervisor) {
        this.name = name;
        this.jobId = jobId;
        this.supervisor = supervisor;
        this.reducerQueue = new ConcurrentLinkedQueue<ReducerChunk<Key, Chunk>>();
        this.scheduler = new ReducerTaskScheduler(this.getExecutorService(), this);
    }

    public String getName() {
        return this.name;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void cancel() {
        this.cancelled.set(true);
    }

    public void processChunk(Map<Key, Chunk> chunk) {
        this.processChunk(-1, null, chunk);
    }

    public void processChunk(int partitionId, Address sender, Map<Key, Chunk> chunk) {
        if (this.cancelled.get()) {
            return;
        }
        this.reducerQueue.offer(new ReducerChunk<Key, Chunk>(chunk, partitionId, sender));
        this.scheduler.requestExecution();
    }

    private ExecutorService getExecutorService() {
        MapReduceService mapReduceService = this.supervisor.getMapReduceService();
        return mapReduceService.getExecutorService(this.name);
    }

    /*
     * Loose catch block
     */
    @Override
    public void run() {
        block8: {
            boolean visibility = this.visibility;
            while (true) {
                ReducerChunk<Key, Chunk> reducerChunk;
                block7: {
                    reducerChunk = this.reducerQueue.poll();
                    if (reducerChunk == null) break;
                    if (!this.cancelled.get()) break block7;
                    this.visibility = !visibility;
                    this.scheduler.afterExecution();
                    return;
                }
                this.reduceChunk(reducerChunk.chunk);
                this.processProcessedState(reducerChunk);
                continue;
                break;
            }
            this.visibility = !visibility;
            this.scheduler.afterExecution();
            break block8;
            catch (Throwable t) {
                try {
                    MapReduceUtil.notifyRemoteException(this.supervisor, t);
                    if (t instanceof Error) {
                        ExceptionUtil.sneakyThrow(t);
                    }
                    this.visibility = !visibility;
                }
                catch (Throwable throwable) {
                    this.visibility = !visibility;
                    this.scheduler.afterExecution();
                    throw throwable;
                }
                this.scheduler.afterExecution();
            }
        }
    }

    private void reduceChunk(Map<Key, Chunk> chunk) {
        for (Map.Entry<Key, Chunk> entry : chunk.entrySet()) {
            Reducer reducer = this.supervisor.getReducerByKey(entry.getKey());
            if (reducer == null) continue;
            Chunk chunkValue = entry.getValue();
            if (chunkValue instanceof CombinerResultList) {
                for (Object value : (List)chunkValue) {
                    reducer.reduce(value);
                }
                continue;
            }
            reducer.reduce(chunkValue);
        }
    }

    private void processProcessedState(ReducerChunk<Key, Chunk> reducerChunk) {
        if (reducerChunk.partitionId != -1) {
            MapReduceService mapReduceService = this.supervisor.getMapReduceService();
            ReducingFinishedNotification notification = new ReducingFinishedNotification(mapReduceService.getLocalAddress(), this.name, this.jobId, reducerChunk.partitionId);
            mapReduceService.sendNotification(reducerChunk.sender, notification);
        }
    }
}

