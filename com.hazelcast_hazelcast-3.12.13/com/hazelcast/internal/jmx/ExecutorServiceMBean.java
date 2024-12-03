/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.IExecutorService;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

public class ExecutorServiceMBean
extends HazelcastMBean<IExecutorService> {
    protected ExecutorServiceMBean(IExecutorService managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IExecutorService", managedObject.getName());
    }

    @ManagedAnnotation(value="localPendingTaskCount")
    @ManagedDescription(value="the number of pending operations of this executor service on this member")
    public long getLocalPendingTaskCount() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getPendingTaskCount();
    }

    @ManagedAnnotation(value="localStartedTaskCount")
    @ManagedDescription(value=" the number of started operations of this executor service on this member")
    public long getLocalStartedTaskCount() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getStartedTaskCount();
    }

    @ManagedAnnotation(value="localCompletedTaskCount")
    @ManagedDescription(value="the number of completed operations of this executor service on this member")
    public long getLocalCompletedTaskCount() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getCompletedTaskCount();
    }

    @ManagedAnnotation(value="localCancelledTaskCount")
    @ManagedDescription(value="the number of cancelled operations of this executor service on this member")
    public long getLocalCancelledTaskCount() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getCancelledTaskCount();
    }

    @ManagedAnnotation(value="localTotalStartLatency")
    @ManagedDescription(value="the total start latency of operations started of this executor on this member")
    public long getLocalTotalStartLatency() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getTotalStartLatency();
    }

    @ManagedAnnotation(value="localTotalExecutionLatency")
    @ManagedDescription(value="the total execution time of operations finished of this executor on this member")
    public long getLocalTotalExecutionLatency() {
        return ((IExecutorService)this.managedObject).getLocalExecutorStats().getTotalExecutionLatency();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="")
    public String name() {
        return ((IExecutorService)this.managedObject).getName();
    }
}

