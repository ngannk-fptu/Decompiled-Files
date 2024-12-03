/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.monitor.LocalExecutorStats;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public interface IExecutorService
extends ExecutorService,
DistributedObject {
    public void execute(Runnable var1, MemberSelector var2);

    public void executeOnKeyOwner(Runnable var1, Object var2);

    public void executeOnMember(Runnable var1, Member var2);

    public void executeOnMembers(Runnable var1, Collection<Member> var2);

    public void executeOnMembers(Runnable var1, MemberSelector var2);

    public void executeOnAllMembers(Runnable var1);

    public <T> Future<T> submit(Callable<T> var1, MemberSelector var2);

    public <T> Future<T> submitToKeyOwner(Callable<T> var1, Object var2);

    public <T> Future<T> submitToMember(Callable<T> var1, Member var2);

    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> var1, Collection<Member> var2);

    public <T> Map<Member, Future<T>> submitToMembers(Callable<T> var1, MemberSelector var2);

    public <T> Map<Member, Future<T>> submitToAllMembers(Callable<T> var1);

    public <T> void submit(Runnable var1, ExecutionCallback<T> var2);

    public <T> void submit(Runnable var1, MemberSelector var2, ExecutionCallback<T> var3);

    public <T> void submitToKeyOwner(Runnable var1, Object var2, ExecutionCallback<T> var3);

    public <T> void submitToMember(Runnable var1, Member var2, ExecutionCallback<T> var3);

    public void submitToMembers(Runnable var1, Collection<Member> var2, MultiExecutionCallback var3);

    public void submitToMembers(Runnable var1, MemberSelector var2, MultiExecutionCallback var3);

    public void submitToAllMembers(Runnable var1, MultiExecutionCallback var2);

    public <T> void submit(Callable<T> var1, ExecutionCallback<T> var2);

    public <T> void submit(Callable<T> var1, MemberSelector var2, ExecutionCallback<T> var3);

    public <T> void submitToKeyOwner(Callable<T> var1, Object var2, ExecutionCallback<T> var3);

    public <T> void submitToMember(Callable<T> var1, Member var2, ExecutionCallback<T> var3);

    public <T> void submitToMembers(Callable<T> var1, Collection<Member> var2, MultiExecutionCallback var3);

    public <T> void submitToMembers(Callable<T> var1, MemberSelector var2, MultiExecutionCallback var3);

    public <T> void submitToAllMembers(Callable<T> var1, MultiExecutionCallback var2);

    public LocalExecutorStats getLocalExecutorStats();
}

