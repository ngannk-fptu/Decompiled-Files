/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch.operations;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchDataSerializerHook;
import com.hazelcast.concurrent.countdownlatch.LatchKey;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.impl.AbstractNamedOperation;

abstract class AbstractCountDownLatchOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
IdentifiedDataSerializable {
    AbstractCountDownLatchOperation() {
    }

    AbstractCountDownLatchOperation(String name) {
        super(name);
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:countDownLatchService";
    }

    @Override
    public final int getFactoryId() {
        return CountDownLatchDataSerializerHook.F_ID;
    }

    WaitNotifyKey waitNotifyKey() {
        return new LatchKey(this.name);
    }
}

