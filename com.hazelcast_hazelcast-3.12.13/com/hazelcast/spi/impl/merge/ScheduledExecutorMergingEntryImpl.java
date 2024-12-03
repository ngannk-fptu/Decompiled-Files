/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.spi.impl.merge.AbstractMergingEntryImpl;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;

public class ScheduledExecutorMergingEntryImpl
extends AbstractMergingEntryImpl<String, ScheduledTaskDescriptor, ScheduledExecutorMergingEntryImpl>
implements SplitBrainMergeTypes.ScheduledExecutorMergeTypes {
    public ScheduledExecutorMergingEntryImpl() {
    }

    public ScheduledExecutorMergingEntryImpl(SerializationService serializationService) {
        super(serializationService);
    }

    @Override
    public int getId() {
        return 10;
    }
}

