/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.ILock;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="ILock")
public class LockMBean
extends HazelcastMBean<ILock> {
    protected LockMBean(ILock managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ILock", managedObject.getName());
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((ILock)this.managedObject).getName();
    }

    @ManagedAnnotation(value="lockObject")
    @ManagedDescription(value="Lock Object as String")
    public String getLockObject() {
        String lockObject = ((ILock)this.managedObject).getName();
        if (lockObject == null) {
            return null;
        }
        return lockObject.toString();
    }

    @ManagedAnnotation(value="remainingLeaseTime")
    @ManagedDescription(value="remaining time in milliseconds or -1 if not locked")
    public long getRemainingLeaseTime() {
        return ((ILock)this.managedObject).getRemainingLeaseTime();
    }

    @ManagedAnnotation(value="lockCount")
    @ManagedDescription(value="re-entrant lock hold count, regardless of lock ownership")
    public int getLockCount() {
        return ((ILock)this.managedObject).getLockCount();
    }

    @ManagedAnnotation(value="forceUnlock", operation=true)
    @ManagedDescription(value="force unlock of this lock")
    public void clear() {
        ((ILock)this.managedObject).forceUnlock();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((ILock)this.managedObject).getPartitionKey();
    }
}

