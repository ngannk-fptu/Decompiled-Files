/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl.operations;

import com.hazelcast.durableexecutor.impl.DistributedDurableExecutorService;
import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.DurableExecutorDataSerializerHook;
import com.hazelcast.durableexecutor.impl.DurableExecutorPartitionContainer;
import com.hazelcast.durableexecutor.impl.TaskRingBuffer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private List<DurableHolder> list;

    public ReplicationOperation() {
    }

    public ReplicationOperation(Map<String, DurableExecutorContainer> map) {
        this.list = new ArrayList<DurableHolder>(map.size());
        for (Map.Entry<String, DurableExecutorContainer> containerEntry : map.entrySet()) {
            String name = containerEntry.getKey();
            DurableExecutorContainer value = containerEntry.getValue();
            this.list.add(new DurableHolder(name, value.getRingBuffer()));
        }
    }

    @Override
    public void run() throws Exception {
        DistributedDurableExecutorService service = (DistributedDurableExecutorService)this.getService();
        DurableExecutorPartitionContainer partitionContainer = service.getPartitionContainer(this.getPartitionId());
        for (DurableHolder durableHolder : this.list) {
            partitionContainer.createExecutorContainer(durableHolder.name, durableHolder.ringBuffer);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.list.size());
        for (DurableHolder durableHolder : this.list) {
            durableHolder.write(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.list = new ArrayList<DurableHolder>(size);
        for (int i = 0; i < size; ++i) {
            DurableHolder durableHolder = new DurableHolder();
            durableHolder.read(in);
            this.list.add(durableHolder);
        }
    }

    @Override
    public int getFactoryId() {
        return DurableExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    private static class DurableHolder {
        private String name;
        private TaskRingBuffer ringBuffer;

        DurableHolder() {
        }

        DurableHolder(String name, TaskRingBuffer ringBuffer) {
            this.name = name;
            this.ringBuffer = ringBuffer;
        }

        private void write(ObjectDataOutput out) throws IOException {
            out.writeUTF(this.name);
            this.ringBuffer.write(out);
        }

        private void read(ObjectDataInput in) throws IOException {
            this.name = in.readUTF();
            this.ringBuffer = new TaskRingBuffer();
            this.ringBuffer.read(in);
        }
    }
}

