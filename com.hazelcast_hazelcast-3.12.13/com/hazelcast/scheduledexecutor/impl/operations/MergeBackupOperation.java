/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.AbstractSchedulerOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeBackupOperation
extends AbstractSchedulerOperation {
    private List<ScheduledTaskDescriptor> descriptors;

    public MergeBackupOperation() {
    }

    MergeBackupOperation(String name, List<ScheduledTaskDescriptor> descriptors) {
        super(name);
        this.descriptors = descriptors;
    }

    @Override
    public void run() throws Exception {
        ScheduledExecutorContainer container = this.getContainer();
        for (ScheduledTaskDescriptor descriptor : this.descriptors) {
            container.enqueueSuspended(descriptor, true);
        }
    }

    @Override
    public int getId() {
        return 28;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.descriptors.size());
        for (ScheduledTaskDescriptor descriptor : this.descriptors) {
            out.writeObject(descriptor);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int size = in.readInt();
        this.descriptors = new ArrayList<ScheduledTaskDescriptor>(size);
        for (int i = 0; i < size; ++i) {
            ScheduledTaskDescriptor descriptor = (ScheduledTaskDescriptor)in.readObject();
            this.descriptors.add(descriptor);
        }
    }
}

