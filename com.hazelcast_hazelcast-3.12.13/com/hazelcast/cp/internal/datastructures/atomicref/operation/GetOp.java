/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.atomicref.operation.AbstractAtomicRefOp;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class GetOp
extends AbstractAtomicRefOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    public GetOp() {
    }

    public GetOp(String name) {
        super(name);
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        return this.getAtomicRef(groupId).get();
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 5;
    }
}

