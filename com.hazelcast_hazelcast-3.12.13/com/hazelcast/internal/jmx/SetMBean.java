/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.ISet;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="ISet")
public class SetMBean
extends HazelcastMBean<ISet> {
    protected SetMBean(ISet managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ISet", managedObject.getName());
    }

    @ManagedAnnotation(value="clear", operation=true)
    @ManagedDescription(value="Clear Set")
    public void clear() {
        ((ISet)this.managedObject).clear();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((ISet)this.managedObject).getName();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((ISet)this.managedObject).getPartitionKey();
    }
}

