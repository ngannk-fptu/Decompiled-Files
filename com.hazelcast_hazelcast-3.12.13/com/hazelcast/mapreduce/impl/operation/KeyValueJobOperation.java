/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.mapreduce.impl.AbstractJobTracker;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.mapreduce.impl.task.JobTaskConfiguration;
import com.hazelcast.mapreduce.impl.task.TrackableJobFuture;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.concurrent.CancellationException;

public class KeyValueJobOperation<K, V>
extends Operation
implements IdentifiedDataSerializable {
    public static final MemberSelector MEMBER_SELECTOR = MemberSelectors.or(MemberSelectors.DATA_MEMBER_SELECTOR, MemberSelectors.and(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR));
    private String name;
    private String jobId;
    private int chunkSize;
    private KeyValueSource<K, V> keyValueSource;
    private Mapper mapper;
    private CombinerFactory combinerFactory;
    private ReducerFactory reducerFactory;
    private boolean communicateStats;
    private TopologyChangedStrategy topologyChangedStrategy;

    public KeyValueJobOperation() {
    }

    public KeyValueJobOperation(String name, String jobId, int chunkSize, KeyValueSource<K, V> keyValueSource, Mapper mapper, CombinerFactory combinerFactory, ReducerFactory reducerFactory, boolean communicateStats, TopologyChangedStrategy topologyChangedStrategy) {
        this.name = name;
        this.jobId = jobId;
        this.chunkSize = chunkSize;
        this.keyValueSource = keyValueSource;
        this.mapper = mapper;
        this.combinerFactory = combinerFactory;
        this.reducerFactory = reducerFactory;
        this.communicateStats = communicateStats;
        this.topologyChangedStrategy = topologyChangedStrategy;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapReduceService";
    }

    @Override
    public void run() throws Exception {
        AbstractJobTracker jobTracker;
        TrackableJobFuture future;
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        Address jobOwner = this.getCallerAddress();
        if (jobOwner == null) {
            jobOwner = this.getNodeEngine().getThisAddress();
        }
        this.injectManagedContext(this.mapper, this.combinerFactory, this.reducerFactory, this.keyValueSource);
        JobTaskConfiguration config = new JobTaskConfiguration(jobOwner, this.getNodeEngine(), this.chunkSize, this.name, this.jobId, this.mapper, this.combinerFactory, this.reducerFactory, this.keyValueSource, this.communicateStats, this.topologyChangedStrategy);
        JobSupervisor supervisor = mapReduceService.createJobSupervisor(config);
        if (supervisor == null && (future = (jobTracker = (AbstractJobTracker)mapReduceService.getJobTracker(this.name)).unregisterTrackableJob(this.jobId)) != null) {
            CancellationException exception = new CancellationException("Operation was cancelled by the user");
            future.setResult(exception);
        }
    }

    private void injectManagedContext(Object injectee, Object ... injectees) {
        ManagedContext managedContext = this.getNodeEngine().getSerializationService().getManagedContext();
        if (injectee != null) {
            managedContext.initialize(injectee);
        }
        for (Object otherInjectee : injectees) {
            if (otherInjectee == null) continue;
            managedContext.initialize(otherInjectee);
        }
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    public void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.jobId);
        out.writeObject(this.keyValueSource);
        out.writeObject(this.mapper);
        out.writeObject(this.combinerFactory);
        out.writeObject(this.reducerFactory);
        out.writeInt(this.chunkSize);
        out.writeBoolean(this.communicateStats);
    }

    @Override
    public void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.jobId = in.readUTF();
        this.keyValueSource = (KeyValueSource)in.readObject();
        this.mapper = (Mapper)in.readObject();
        this.combinerFactory = (CombinerFactory)in.readObject();
        this.reducerFactory = (ReducerFactory)in.readObject();
        this.chunkSize = in.readInt();
        this.communicateStats = in.readBoolean();
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.name);
    }
}

