/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.operations.JoinOperation;
import com.hazelcast.spi.Operation;

public abstract class AbstractJoinOperation
extends Operation
implements JoinOperation {
    @Override
    public int getFactoryId() {
        return 0;
    }
}

