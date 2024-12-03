/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.JobProcessInformation;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.TrackableJob;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class TrackableJobFuture<V>
extends AbstractCompletableFuture<V>
implements TrackableJob<V>,
JobCompletableFuture<V> {
    private final String name;
    private final String jobId;
    private final JobTracker jobTracker;
    private final Collator collator;
    private final MapReduceService mapReduceService;

    public TrackableJobFuture(String name, String jobId, JobTracker jobTracker, NodeEngine nodeEngine, Collator collator) {
        super(nodeEngine, nodeEngine.getLogger(TrackableJobFuture.class));
        this.name = name;
        this.jobId = jobId;
        this.jobTracker = jobTracker;
        this.collator = collator;
        this.mapReduceService = (MapReduceService)((NodeEngineImpl)nodeEngine).getService("hz:impl:mapReduceService");
    }

    @Override
    public boolean setResult(Object result) {
        Object finalResult = result;
        if (finalResult instanceof Throwable && !(finalResult instanceof CancellationException)) {
            return super.setResult(new ExecutionException((Throwable)finalResult));
        }
        if (this.collator != null) {
            try {
                finalResult = this.collator.collate(((Map)finalResult).entrySet());
            }
            catch (Exception e) {
                finalResult = e;
            }
        }
        if (finalResult instanceof Throwable && !(finalResult instanceof CancellationException)) {
            finalResult = new ExecutionException((Throwable)finalResult);
        }
        return super.setResult(finalResult);
    }

    @Override
    protected boolean shouldCancel(boolean mayInterruptIfRunning) {
        Address jobOwner = this.mapReduceService.getLocalAddress();
        if (!this.mapReduceService.registerJobSupervisorCancellation(this.name, this.jobId, jobOwner)) {
            return false;
        }
        JobSupervisor supervisor = this.mapReduceService.getJobSupervisor(this.name, this.jobId);
        if (supervisor == null || !supervisor.isOwnerNode()) {
            return false;
        }
        CancellationException exception = new CancellationException("Operation was cancelled by the user");
        return supervisor.cancelAndNotify(exception);
    }

    @Override
    public JobTracker getJobTracker() {
        return this.jobTracker;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getJobId() {
        return this.jobId;
    }

    @Override
    public ICompletableFuture<V> getCompletableFuture() {
        JobSupervisor supervisor = this.mapReduceService.getJobSupervisor(this.name, this.jobId);
        if (supervisor == null || !supervisor.isOwnerNode()) {
            return null;
        }
        return this;
    }

    @Override
    public JobProcessInformation getJobProcessInformation() {
        JobSupervisor supervisor = this.mapReduceService.getJobSupervisor(this.name, this.jobId);
        if (supervisor == null || !supervisor.isOwnerNode()) {
            return null;
        }
        return new JobProcessInformationAdapter(supervisor.getJobProcessInformation());
    }

    private static final class JobProcessInformationAdapter
    implements JobProcessInformation {
        private final JobProcessInformation processInformation;

        private JobProcessInformationAdapter(JobProcessInformation processInformation) {
            this.processInformation = processInformation;
        }

        @Override
        public JobPartitionState[] getPartitionStates() {
            JobPartitionState[] partitionStates = this.processInformation.getPartitionStates();
            return Arrays.copyOf(partitionStates, partitionStates.length);
        }

        @Override
        public int getProcessedRecords() {
            return this.processInformation.getProcessedRecords();
        }
    }
}

