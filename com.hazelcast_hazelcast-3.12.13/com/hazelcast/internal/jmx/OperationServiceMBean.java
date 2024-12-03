/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.OperationService")
public class OperationServiceMBean
extends HazelcastMBean<InternalOperationService> {
    private static final int INITIAL_CAPACITY = 3;

    public OperationServiceMBean(HazelcastInstance hazelcastInstance, InternalOperationService operationService, ManagementService service) {
        super(operationService, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.OperationService"));
        properties.put("name", ManagementService.quote("operationService" + hazelcastInstance.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="responseQueueSize")
    @ManagedDescription(value="The size of the response queue")
    public int getResponseQueueSize() {
        return ((InternalOperationService)this.managedObject).getResponseQueueSize();
    }

    @ManagedAnnotation(value="operationExecutorQueueSize")
    @ManagedDescription(value="The size of the operation executor queue")
    public int getOperationExecutorQueueSize() {
        return ((InternalOperationService)this.managedObject).getOperationExecutorQueueSize();
    }

    @ManagedAnnotation(value="runningOperationsCount")
    @ManagedDescription(value="the running operations count")
    public int getRunningOperationsCount() {
        return ((InternalOperationService)this.managedObject).getRunningOperationsCount();
    }

    @ManagedAnnotation(value="remoteOperationCount")
    @ManagedDescription(value="The number of remote operations")
    public int getRemoteOperationsCount() {
        return ((InternalOperationService)this.managedObject).getRemoteOperationsCount();
    }

    @ManagedAnnotation(value="executedOperationCount")
    @ManagedDescription(value="The number of executed operations")
    public long getExecutedOperationCount() {
        return ((InternalOperationService)this.managedObject).getExecutedOperationCount();
    }

    @ManagedAnnotation(value="operationThreadCount")
    @ManagedDescription(value="Number of threads executing operations")
    public long getOperationThreadCount() {
        return ((InternalOperationService)this.managedObject).getPartitionThreadCount();
    }
}

