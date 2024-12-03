/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.ManagedExecutorService")
public class ManagedExecutorServiceMBean
extends HazelcastMBean<ManagedExecutorService> {
    private static final int INITIAL_CAPACITY = 3;

    public ManagedExecutorServiceMBean(HazelcastInstance hazelcastInstance, ManagedExecutorService executorService, ManagementService service) {
        super(executorService, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.ManagedExecutorService"));
        properties.put("name", ManagementService.quote(executorService.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="The name of the ManagedExecutor")
    public String getName() {
        return ((ManagedExecutorService)this.managedObject).getName();
    }

    @ManagedAnnotation(value="queueSize")
    @ManagedDescription(value="The work queue size")
    public int queueSize() {
        return ((ManagedExecutorService)this.managedObject).getQueueSize();
    }

    @ManagedAnnotation(value="poolSize")
    @ManagedDescription(value="The current number of thread in the threadpool")
    public int poolSize() {
        return ((ManagedExecutorService)this.managedObject).getPoolSize();
    }

    @ManagedAnnotation(value="remainingQueueCapacity")
    @ManagedDescription(value="The remaining capacity on the work queue")
    public int queueRemainingCapacity() {
        return ((ManagedExecutorService)this.managedObject).getRemainingQueueCapacity();
    }

    @ManagedAnnotation(value="maximumPoolSize")
    @ManagedDescription(value="The maximum number of thread in the threadpool")
    public int maxPoolSize() {
        return ((ManagedExecutorService)this.managedObject).getMaximumPoolSize();
    }

    @ManagedAnnotation(value="isShutdown")
    @ManagedDescription(value="If the ManagedExecutor is shutdown")
    public boolean isShutdown() {
        return ((ManagedExecutorService)this.managedObject).isShutdown();
    }

    @ManagedAnnotation(value="isTerminated")
    @ManagedDescription(value="If the ManagedExecutor is terminated")
    public boolean isTerminated() {
        return ((ManagedExecutorService)this.managedObject).isTerminated();
    }

    @ManagedAnnotation(value="completedTaskCount")
    @ManagedDescription(value="The number of tasks this ManagedExecutor has executed")
    public long getExecutedCount() {
        return ((ManagedExecutorService)this.managedObject).getCompletedTaskCount();
    }
}

