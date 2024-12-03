/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch.proxy;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.AwaitOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.CountDownOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.GetCountOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.GetRoundOp;
import com.hazelcast.cp.internal.datastructures.countdownlatch.operation.TrySetCountOp;
import com.hazelcast.cp.internal.datastructures.spi.operation.DestroyRaftObjectOp;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.TimeUnit;

public class RaftCountDownLatchProxy
implements ICountDownLatch {
    private final RaftInvocationManager invocationManager;
    private final RaftGroupId groupId;
    private final String proxyName;
    private final String objectName;

    public RaftCountDownLatchProxy(NodeEngine nodeEngine, RaftGroupId groupId, String proxyName, String objectName) {
        RaftService service = (RaftService)nodeEngine.getService("hz:core:raft");
        this.invocationManager = service.getInvocationManager();
        this.groupId = groupId;
        this.proxyName = proxyName;
        this.objectName = objectName;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) {
        Preconditions.checkNotNull(unit);
        long timeoutMillis = Math.max(0L, unit.toMillis(timeout));
        return (Boolean)this.invocationManager.invoke(this.groupId, new AwaitOp(this.objectName, UuidUtil.newUnsecureUUID(), timeoutMillis)).join();
    }

    @Override
    public void countDown() {
        int round = (Integer)this.invocationManager.invoke(this.groupId, new GetRoundOp(this.objectName)).join();
        this.invocationManager.invoke(this.groupId, new CountDownOp(this.objectName, UuidUtil.newUnsecureUUID(), round)).join();
    }

    @Override
    public int getCount() {
        return (Integer)this.invocationManager.invoke(this.groupId, new GetCountOp(this.objectName)).join();
    }

    @Override
    public boolean trySetCount(int count) {
        return (Boolean)this.invocationManager.invoke(this.groupId, new TrySetCountOp(this.objectName, count)).join();
    }

    @Override
    public String getPartitionKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.proxyName;
    }

    @Override
    public String getServiceName() {
        return "hz:raft:countDownLatchService";
    }

    @Override
    public void destroy() {
        this.invocationManager.invoke(this.groupId, new DestroyRaftObjectOp(this.getServiceName(), this.objectName)).join();
    }

    public CPGroupId getGroupId() {
        return this.groupId;
    }
}

