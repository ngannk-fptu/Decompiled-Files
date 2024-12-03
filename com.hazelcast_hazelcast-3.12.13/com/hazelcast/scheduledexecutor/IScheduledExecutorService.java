/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public interface IScheduledExecutorService
extends DistributedObject {
    public IScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    public <V> IScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

    public IScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

    public IScheduledFuture<?> scheduleOnMember(Runnable var1, Member var2, long var3, TimeUnit var5);

    public <V> IScheduledFuture<V> scheduleOnMember(Callable<V> var1, Member var2, long var3, TimeUnit var5);

    public IScheduledFuture<?> scheduleOnMemberAtFixedRate(Runnable var1, Member var2, long var3, long var5, TimeUnit var7);

    public IScheduledFuture<?> scheduleOnKeyOwner(Runnable var1, Object var2, long var3, TimeUnit var5);

    public <V> IScheduledFuture<V> scheduleOnKeyOwner(Callable<V> var1, Object var2, long var3, TimeUnit var5);

    public IScheduledFuture<?> scheduleOnKeyOwnerAtFixedRate(Runnable var1, Object var2, long var3, long var5, TimeUnit var7);

    public Map<Member, IScheduledFuture<?>> scheduleOnAllMembers(Runnable var1, long var2, TimeUnit var4);

    public <V> Map<Member, IScheduledFuture<V>> scheduleOnAllMembers(Callable<V> var1, long var2, TimeUnit var4);

    public Map<Member, IScheduledFuture<?>> scheduleOnAllMembersAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

    public Map<Member, IScheduledFuture<?>> scheduleOnMembers(Runnable var1, Collection<Member> var2, long var3, TimeUnit var5);

    public <V> Map<Member, IScheduledFuture<V>> scheduleOnMembers(Callable<V> var1, Collection<Member> var2, long var3, TimeUnit var5);

    public Map<Member, IScheduledFuture<?>> scheduleOnMembersAtFixedRate(Runnable var1, Collection<Member> var2, long var3, long var5, TimeUnit var7);

    public <V> IScheduledFuture<V> getScheduledFuture(ScheduledTaskHandler var1);

    public <V> Map<Member, List<IScheduledFuture<V>>> getAllScheduledFutures();

    public void shutdown();
}

