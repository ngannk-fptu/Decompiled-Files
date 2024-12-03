/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.session.CPSessionManagementService;

public interface CPSubsystem {
    public IAtomicLong getAtomicLong(String var1);

    public <E> IAtomicReference<E> getAtomicReference(String var1);

    public ICountDownLatch getCountDownLatch(String var1);

    public FencedLock getLock(String var1);

    public ISemaphore getSemaphore(String var1);

    public CPMember getLocalCPMember();

    public CPSubsystemManagementService getCPSubsystemManagementService();

    public CPSessionManagementService getCPSessionManagementService();
}

