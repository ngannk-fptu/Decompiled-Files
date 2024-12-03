/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.ISemaphore;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="ISemaphore")
public class SemaphoreMBean
extends HazelcastMBean<ISemaphore> {
    protected SemaphoreMBean(ISemaphore managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ISemaphore", managedObject.getName());
    }

    @ManagedAnnotation(value="name")
    public String getName() {
        return ((ISemaphore)this.managedObject).getName();
    }

    @ManagedAnnotation(value="available")
    public int getAvailable() {
        return ((ISemaphore)this.managedObject).availablePermits();
    }

    @ManagedAnnotation(value="drain", operation=true)
    @ManagedDescription(value="Acquire and return all permits that are immediately available")
    public int drain() {
        return ((ISemaphore)this.managedObject).drainPermits();
    }

    @ManagedAnnotation(value="reduce", operation=true)
    @ManagedDescription(value="Shrinks the number of available permits by the indicated reduction. Does not block")
    public void reduce(int reduction) {
        ((ISemaphore)this.managedObject).reducePermits(reduction);
    }

    @ManagedAnnotation(value="increase", operation=true)
    @ManagedDescription(value="Increases the number of available permits by the indicated increase. Does not block")
    public void increase(int increases) {
        ((ISemaphore)this.managedObject).increasePermits(increases);
    }

    @ManagedAnnotation(value="release", operation=true)
    @ManagedDescription(value="Releases the given number of permits, increasing the number of available permits by that amount")
    public void release(int permits) {
        ((ISemaphore)this.managedObject).release(permits);
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((ISemaphore)this.managedObject).getPartitionKey();
    }
}

