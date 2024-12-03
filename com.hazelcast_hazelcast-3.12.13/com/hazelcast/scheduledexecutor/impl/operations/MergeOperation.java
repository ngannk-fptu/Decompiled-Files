/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractBackupAwareSchedulerOperation;
import com.hazelcast.scheduledexecutor.impl.operations.MergeBackupOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeOperation
extends AbstractBackupAwareSchedulerOperation {
    private List<SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergingEntries;
    private SplitBrainMergePolicy<ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergePolicy;
    private transient List<ScheduledTaskDescriptor> mergedTasks;

    public MergeOperation() {
    }

    public MergeOperation(String name, List<SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergingEntries, SplitBrainMergePolicy<ScheduledTaskDescriptor, SplitBrainMergeTypes.ScheduledExecutorMergeTypes> mergePolicy) {
        super(name);
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public boolean shouldBackup() {
        return super.shouldBackup() && this.mergedTasks != null && !this.mergedTasks.isEmpty();
    }

    @Override
    public void run() throws Exception {
        ScheduledExecutorContainer container = this.getContainer();
        this.mergedTasks = new ArrayList<ScheduledTaskDescriptor>();
        for (SplitBrainMergeTypes.ScheduledExecutorMergeTypes mergingEntry : this.mergingEntries) {
            ScheduledTaskDescriptor merged = container.merge(mergingEntry, this.mergePolicy);
            if (merged == null) continue;
            this.mergedTasks.add(merged);
        }
        container.promoteSuspended();
    }

    @Override
    public int getId() {
        return 27;
    }

    @Override
    public Operation getBackupOperation() {
        return new MergeBackupOperation(this.getSchedulerName(), this.mergedTasks);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.mergePolicy);
        out.writeInt(this.mergingEntries.size());
        for (SplitBrainMergeTypes.ScheduledExecutorMergeTypes mergingEntry : this.mergingEntries) {
            out.writeObject(mergingEntry);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.mergePolicy = (SplitBrainMergePolicy)in.readObject();
        int size = in.readInt();
        this.mergingEntries = new ArrayList<SplitBrainMergeTypes.ScheduledExecutorMergeTypes>(size);
        for (int i = 0; i < size; ++i) {
            SplitBrainMergeTypes.ScheduledExecutorMergeTypes mergingEntry = (SplitBrainMergeTypes.ScheduledExecutorMergeTypes)in.readObject();
            this.mergingEntries.add(mergingEntry);
        }
    }
}

