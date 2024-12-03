/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="ICountDownLatch")
public class CountDownLatchMBean
extends HazelcastMBean<ICountDownLatch> {
    protected CountDownLatchMBean(ICountDownLatch managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ICountDownLatch", managedObject.getName());
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="")
    public String name() {
        return ((ICountDownLatch)this.managedObject).getName();
    }

    @ManagedAnnotation(value="count")
    @ManagedDescription(value="Current Count")
    public int getCount() {
        return ((ICountDownLatch)this.managedObject).getCount();
    }

    @ManagedAnnotation(value="countDown", operation=true)
    @ManagedDescription(value="perform a countdown operation")
    public void countDown() {
        ((ICountDownLatch)this.managedObject).countDown();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((ICountDownLatch)this.managedObject).getPartitionKey();
    }
}

