/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.IAtomicReference;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="IAtomicReference")
public class AtomicReferenceMBean
extends HazelcastMBean<IAtomicReference> {
    public AtomicReferenceMBean(IAtomicReference managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IAtomicReference", managedObject.getName());
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((IAtomicReference)this.managedObject).getName();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((IAtomicReference)this.managedObject).getPartitionKey();
    }
}

