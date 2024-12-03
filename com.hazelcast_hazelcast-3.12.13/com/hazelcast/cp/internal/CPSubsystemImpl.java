/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.session.CPSessionManagementService;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.util.Preconditions;

public class CPSubsystemImpl
implements CPSubsystem {
    private final HazelcastInstanceImpl instance;

    public CPSubsystemImpl(HazelcastInstanceImpl instance) {
        this.instance = instance;
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
        Preconditions.checkNotNull(name, "Retrieving an atomic long instance with a null name is not allowed!");
        RaftRemoteService service = (RaftRemoteService)this.getService("hz:raft:atomicLongService");
        return (IAtomicLong)service.createProxy(name);
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
        Preconditions.checkNotNull(name, "Retrieving an atomic reference instance with a null name is not allowed!");
        RaftRemoteService service = (RaftRemoteService)this.getService("hz:raft:atomicRefService");
        return (IAtomicReference)service.createProxy(name);
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
        Preconditions.checkNotNull(name, "Retrieving a count down latch instance with a null name is not allowed!");
        RaftRemoteService service = (RaftRemoteService)this.getService("hz:raft:countDownLatchService");
        return (ICountDownLatch)service.createProxy(name);
    }

    @Override
    public FencedLock getLock(String name) {
        Preconditions.checkNotNull(name, "Retrieving an fenced lock instance with a null name is not allowed!");
        RaftRemoteService service = (RaftRemoteService)this.getService("hz:raft:lockService");
        return (FencedLock)service.createProxy(name);
    }

    @Override
    public ISemaphore getSemaphore(String name) {
        Preconditions.checkNotNull(name, "Retrieving a semaphore instance with a null name is not allowed!");
        RaftRemoteService service = (RaftRemoteService)this.getService("hz:raft:semaphoreService");
        return (ISemaphore)service.createProxy(name);
    }

    @Override
    public CPMember getLocalCPMember() {
        return this.getCPSubsystemManagementService().getLocalCPMember();
    }

    @Override
    public CPSubsystemManagementService getCPSubsystemManagementService() {
        if (this.instance.getConfig().getCPSubsystemConfig().getCPMemberCount() == 0) {
            throw new HazelcastException("CP Subsystem is not enabled!");
        }
        return (CPSubsystemManagementService)this.instance.node.getNodeEngine().getService("hz:core:raft");
    }

    @Override
    public CPSessionManagementService getCPSessionManagementService() {
        if (this.instance.getConfig().getCPSubsystemConfig().getCPMemberCount() == 0) {
            throw new HazelcastException("CP Subsystem is not enabled!");
        }
        return (CPSessionManagementService)this.instance.node.getNodeEngine().getService("hz:core:raftSession");
    }

    private <T> T getService(String serviceName) {
        return this.instance.node.getNodeEngine().getService(serviceName);
    }
}

