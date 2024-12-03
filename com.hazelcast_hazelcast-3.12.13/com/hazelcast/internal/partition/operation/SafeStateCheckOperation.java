/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;

public class SafeStateCheckOperation
extends AbstractPartitionOperation
implements AllowedDuringPassiveState {
    private transient boolean safe;

    @Override
    public void run() throws Exception {
        InternalPartitionService service = (InternalPartitionService)this.getService();
        this.safe = service.isMemberStateSafe();
    }

    @Override
    public Object getResponse() {
        return this.safe;
    }

    @Override
    public int getId() {
        return 14;
    }
}

