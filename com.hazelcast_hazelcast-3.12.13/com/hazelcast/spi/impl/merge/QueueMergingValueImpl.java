/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.spi.impl.merge.AbstractCollectionMergingValueImpl;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;

public class QueueMergingValueImpl
extends AbstractCollectionMergingValueImpl<Collection<Object>, QueueMergingValueImpl>
implements SplitBrainMergeTypes.QueueMergeTypes {
    public QueueMergingValueImpl() {
    }

    public QueueMergingValueImpl(SerializationService serializationService) {
        super(serializationService);
    }

    @Override
    public int getId() {
        return 1;
    }
}

