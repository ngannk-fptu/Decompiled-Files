/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.KeyValueSourceMappingPhase;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;

public class StartProcessingJobOperation<K>
extends Operation
implements IdentifiedDataSerializable {
    private String name;
    private Collection<K> keys;
    private String jobId;
    private KeyPredicate<? super K> predicate;

    public StartProcessingJobOperation() {
    }

    public StartProcessingJobOperation(String name, String jobId, Collection<K> keys, KeyPredicate<? super K> predicate) {
        this.name = name;
        this.keys = keys;
        this.jobId = jobId;
        this.predicate = predicate;
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        if (mapReduceService.unregisterJobSupervisorCancellation(this.name, this.jobId)) {
            AbstractJobTracker jobTracker = (AbstractJobTracker)mapReduceService.getJobTracker(this.name);
            TrackableJobFuture future = jobTracker.unregisterTrackableJob(this.jobId);
            if (future != null) {
                CancellationException exception = new CancellationException("Operation was cancelled by the user");
                future.setResult(exception);
            }
            return;
        }
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.name, this.jobId);
        if (supervisor == null) {
            return;
        }
        KeyValueSourceMappingPhase mappingPhase = new KeyValueSourceMappingPhase(this.keys, this.predicate);
        supervisor.startTasks(mappingPhase);
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.jobId);
        out.writeInt(this.keys == null ? 0 : this.keys.size());
        if (this.keys != null) {
            for (K key : this.keys) {
                out.writeObject(key);
            }
        }
        out.writeObject(this.predicate);
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.jobId = in.readUTF();
        int size = in.readInt();
        this.keys = new ArrayList<K>();
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readObject());
        }
        this.predicate = (KeyPredicate)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 9;
    }
}

