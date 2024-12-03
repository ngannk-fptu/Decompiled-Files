/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.operation;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AbstractCountDownLatchOp;

public class GetRoundOp
extends AbstractCountDownLatchOp
implements IndeterminateOperationStateAware {
    public GetRoundOp() {
    }

    public GetRoundOp(String name) {
        super(name);
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftCountDownLatchService service = (RaftCountDownLatchService)this.getService();
        return service.getRound(groupId, this.name);
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

