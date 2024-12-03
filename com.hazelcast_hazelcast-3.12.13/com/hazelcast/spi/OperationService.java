/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.InvocationBuilder;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.util.Collection;
import java.util.Map;

public interface OperationService {
    public void run(Operation var1);

    public void execute(Operation var1);

    public <E> InternalCompletableFuture<E> invokeOnPartition(String var1, Operation var2, int var3);

    public <E> InternalCompletableFuture<E> invokeOnPartition(Operation var1);

    public <E> InternalCompletableFuture<E> invokeOnTarget(String var1, Operation var2, Address var3);

    public InvocationBuilder createInvocationBuilder(String var1, Operation var2, int var3);

    public InvocationBuilder createInvocationBuilder(String var1, Operation var2, Address var3);

    public Map<Integer, Object> invokeOnAllPartitions(String var1, OperationFactory var2) throws Exception;

    public <T> ICompletableFuture<Map<Integer, T>> invokeOnAllPartitionsAsync(String var1, OperationFactory var2);

    public <T> Map<Integer, T> invokeOnPartitions(String var1, OperationFactory var2, Collection<Integer> var3) throws Exception;

    public <T> ICompletableFuture<Map<Integer, T>> invokeOnPartitionsAsync(String var1, OperationFactory var2, Collection<Integer> var3);

    public Map<Integer, Object> invokeOnPartitions(String var1, OperationFactory var2, int[] var3) throws Exception;

    public boolean send(Operation var1, Address var2);
}

