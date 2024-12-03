/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AbstractAtomicLongOp;

public class LocalGetOp
extends AbstractAtomicLongOp
implements IndeterminateOperationStateAware {
    public LocalGetOp() {
    }

    public LocalGetOp(String name) {
        super(name);
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        return this.getAtomicLong(groupId).value();
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getId() {
        return 7;
    }
}

