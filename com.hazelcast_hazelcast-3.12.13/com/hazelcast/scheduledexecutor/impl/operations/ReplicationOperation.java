/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorPartition;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class ReplicationOperation
extends AbstractSchedulerOperation {
    private Map<String, Map<String, ScheduledTaskDescriptor>> map;

    public ReplicationOperation() {
    }

    public ReplicationOperation(Map<String, Map<String, ScheduledTaskDescriptor>> map) {
        this.map = map;
    }

    @Override
    public void run() throws Exception {
        DistributedScheduledExecutorService service = (DistributedScheduledExecutorService)this.getService();
        ScheduledExecutorPartition partition = service.getPartition(this.getPartitionId());
        for (Map.Entry<String, Map<String, ScheduledTaskDescriptor>> entry : this.map.entrySet()) {
            ScheduledExecutorContainer container = partition.getOrCreateContainer(entry.getKey());
            for (Map.Entry<String, ScheduledTaskDescriptor> descriptorEntry : entry.getValue().entrySet()) {
                String taskName = descriptorEntry.getKey();
                ScheduledTaskDescriptor descriptor = descriptorEntry.getValue();
                if (container.has(taskName)) continue;
                container.enqueueSuspended(descriptor, false);
            }
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.map.size());
        for (Map.Entry<String, Map<String, ScheduledTaskDescriptor>> entry : this.map.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeInt(entry.getValue().size());
            for (Map.Entry<String, ScheduledTaskDescriptor> subEntry : entry.getValue().entrySet()) {
                out.writeUTF(subEntry.getKey());
                out.writeObject(subEntry.getValue());
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.map = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String key = in.readUTF();
            int subSize = in.readInt();
            Map<String, ScheduledTaskDescriptor> subMap = MapUtil.createHashMap(subSize);
            this.map.put(key, subMap);
            for (int k = 0; k < subSize; ++k) {
                subMap.put(in.readUTF(), (ScheduledTaskDescriptor)in.readObject());
            }
        }
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 19;
    }
}

