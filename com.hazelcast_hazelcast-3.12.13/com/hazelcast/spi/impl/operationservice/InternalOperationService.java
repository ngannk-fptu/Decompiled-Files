/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationservice.PartitionTaskFactory;
import java.util.BitSet;
import java.util.List;

public interface InternalOperationService
extends OperationService {
    public static final String SERVICE_NAME = "hz:impl:operationService";

    public int getResponseQueueSize();

    public int getOperationExecutorQueueSize();

    public int getPriorityOperationExecutorQueueSize();

    public int getRunningOperationsCount();

    public int getRemoteOperationsCount();

    public long getExecutedOperationCount();

    public int getPartitionThreadCount();

    public int getGenericThreadCount();

    public void onStartAsyncOperation(Operation var1);

    public void onCompletionAsyncOperation(Operation var1);

    public boolean isCallTimedOut(Operation var1);

    public boolean isRunAllowed(Operation var1);

    public void execute(PartitionSpecificRunnable var1);

    public void executeOnPartitions(PartitionTaskFactory var1, BitSet var2);

    public List<SlowOperationDTO> getSlowOperationDTOs();

    public <V> void asyncInvokeOnPartition(String var1, Operation var2, int var3, ExecutionCallback<V> var4);

    public void onEndpointLeft(Address var1);
}

