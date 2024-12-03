/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface CPSubsystemManagementService {
    public CPMember getLocalCPMember();

    public ICompletableFuture<Collection<CPGroupId>> getCPGroupIds();

    public ICompletableFuture<CPGroup> getCPGroup(String var1);

    public ICompletableFuture<Void> forceDestroyCPGroup(String var1);

    public ICompletableFuture<Collection<CPMember>> getCPMembers();

    public ICompletableFuture<Void> promoteToCPMember();

    public ICompletableFuture<Void> removeCPMember(String var1);

    public ICompletableFuture<Void> restart();

    public boolean isDiscoveryCompleted();

    public boolean awaitUntilDiscoveryCompleted(long var1, TimeUnit var3) throws InterruptedException;
}

