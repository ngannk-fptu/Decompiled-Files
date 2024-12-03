/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.countdownlatch.AwaitInvocationKey;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatch;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchRegistry;
import com.hazelcast.cp.internal.datastructures.countdownlatch.proxy.RaftCountDownLatchProxy;
import com.hazelcast.cp.internal.datastructures.spi.blocking.AbstractBlockingService;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.UUID;

public class RaftCountDownLatchService
extends AbstractBlockingService<AwaitInvocationKey, RaftCountDownLatch, RaftCountDownLatchRegistry> {
    public static final String SERVICE_NAME = "hz:raft:countDownLatchService";

    public RaftCountDownLatchService(NodeEngine nodeEngine) {
        super(nodeEngine);
    }

    public boolean trySetCount(CPGroupId groupId, String name, int count) {
        return ((RaftCountDownLatchRegistry)this.getOrInitRegistry(groupId)).trySetCount(name, count);
    }

    public int countDown(CPGroupId groupId, String name, UUID invocationUuid, int expectedRound) {
        RaftCountDownLatchRegistry registry = (RaftCountDownLatchRegistry)this.getOrInitRegistry(groupId);
        Tuple2<Integer, Collection<AwaitInvocationKey>> t = registry.countDown(name, invocationUuid, expectedRound);
        this.notifyWaitKeys(groupId, name, (Collection)t.element2, true);
        return (Integer)t.element1;
    }

    public boolean await(CPGroupId groupId, String name, AwaitInvocationKey key, long timeoutMillis) {
        boolean success = ((RaftCountDownLatchRegistry)this.getOrInitRegistry(groupId)).await(name, key, timeoutMillis);
        if (!success) {
            this.scheduleTimeout(groupId, name, key.invocationUid(), timeoutMillis);
        }
        return success;
    }

    public int getRemainingCount(CPGroupId groupId, String name) {
        return ((RaftCountDownLatchRegistry)this.getOrInitRegistry(groupId)).getRemainingCount(name);
    }

    public int getRound(CPGroupId groupId, String name) {
        return ((RaftCountDownLatchRegistry)this.getOrInitRegistry(groupId)).getRound(name);
    }

    @Override
    protected RaftCountDownLatchRegistry createNewRegistry(CPGroupId groupId) {
        return new RaftCountDownLatchRegistry(groupId);
    }

    @Override
    protected Object expiredWaitKeyResponse() {
        return false;
    }

    @Override
    protected String serviceName() {
        return SERVICE_NAME;
    }

    public ICountDownLatch createProxy(String proxyName) {
        try {
            proxyName = RaftService.withoutDefaultGroupName(proxyName);
            RaftService service = (RaftService)this.nodeEngine.getService("hz:core:raft");
            RaftGroupId groupId = service.createRaftGroupForProxy(proxyName);
            return new RaftCountDownLatchProxy(this.nodeEngine, groupId, proxyName, RaftService.getObjectNameForProxy(proxyName));
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

