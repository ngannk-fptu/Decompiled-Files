/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.operation;

import com.hazelcast.mapreduce.TopologyChangedException;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.mapreduce.impl.MapReduceDataSerializerHook;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.KeysAssignmentResult;
import com.hazelcast.mapreduce.impl.operation.ProcessingOperation;
import com.hazelcast.mapreduce.impl.operation.RequestPartitionResult;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.partition.NoDataMemberInClusterException;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeysAssignmentOperation
extends ProcessingOperation {
    private Set<Object> keys;
    private KeysAssignmentResult result;

    public KeysAssignmentOperation() {
    }

    public KeysAssignmentOperation(String name, String jobId, Set<Object> keys) {
        super(name, jobId);
        this.keys = keys;
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    public void run() throws Exception {
        MapReduceService mapReduceService = (MapReduceService)this.getService();
        JobSupervisor supervisor = mapReduceService.getJobSupervisor(this.getName(), this.getJobId());
        if (supervisor == null) {
            this.result = new KeysAssignmentResult(RequestPartitionResult.ResultState.NO_SUPERVISOR, null);
            return;
        }
        if (!supervisor.checkAssignedMembersAvailable()) {
            HashMap<Object, Address> assignment = new HashMap<Object, Address>();
            TopologyChangedStrategy tcs = supervisor.getConfiguration().getTopologyChangedStrategy();
            if (tcs == TopologyChangedStrategy.CANCEL_RUNNING_OPERATION) {
                TopologyChangedException exception = new TopologyChangedException();
                supervisor.cancelAndNotify(exception);
                this.result = new KeysAssignmentResult(RequestPartitionResult.ResultState.CHECK_STATE_FAILED, assignment);
                return;
            }
            TopologyChangedException exception = new TopologyChangedException("Unknown or unsupported TopologyChangedStrategy");
            supervisor.cancelAndNotify(exception);
            this.result = new KeysAssignmentResult(RequestPartitionResult.ResultState.CHECK_STATE_FAILED, assignment);
            return;
        }
        Map<Object, Address> assignment = MapUtil.createHashMap(this.keys.size());
        try {
            for (Object key : this.keys) {
                Address address = supervisor.assignKeyReducerAddress(key);
                assignment.put(key, address);
            }
            this.result = new KeysAssignmentResult(RequestPartitionResult.ResultState.SUCCESSFUL, assignment);
        }
        catch (NoDataMemberInClusterException e) {
            supervisor.cancelAndNotify(e);
            this.result = new KeysAssignmentResult(RequestPartitionResult.ResultState.CHECK_STATE_FAILED, assignment);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.keys.size());
        for (Object key : this.keys) {
            out.writeObject(key);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.keys = SetUtil.createHashSet(size);
        for (int i = 0; i < size; ++i) {
            this.keys.add(in.readObject());
        }
    }

    @Override
    public int getFactoryId() {
        return MapReduceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }
}

