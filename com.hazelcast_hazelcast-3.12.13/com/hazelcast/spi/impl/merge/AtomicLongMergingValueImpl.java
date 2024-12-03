/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.merge;

import com.hazelcast.spi.impl.merge.AbstractMergingValueImpl;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;

public class AtomicLongMergingValueImpl
extends AbstractMergingValueImpl<Long, AtomicLongMergingValueImpl>
implements SplitBrainMergeTypes.AtomicLongMergeTypes {
    public AtomicLongMergingValueImpl() {
    }

    public AtomicLongMergingValueImpl(SerializationService serializationService) {
        super(serializationService);
    }

    @Override
    public int getId() {
        return 2;
    }
}

