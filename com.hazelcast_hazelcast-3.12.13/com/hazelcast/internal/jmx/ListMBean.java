/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.IList;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="IList")
public class ListMBean
extends HazelcastMBean<IList<?>> {
    protected ListMBean(IList<?> managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IList", managedObject.getName());
    }

    @ManagedAnnotation(value="clear", operation=true)
    @ManagedDescription(value="Clear List")
    public void clear() {
        ((IList)this.managedObject).clear();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((IList)this.managedObject).getName();
    }
}

