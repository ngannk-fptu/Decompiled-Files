/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="IAtomicLong")
public class AtomicLongMBean
extends HazelcastMBean<IAtomicLong> {
    public AtomicLongMBean(IAtomicLong managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("IAtomicLong", managedObject.getName());
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((IAtomicLong)this.managedObject).getName();
    }

    @ManagedAnnotation(value="currentValue")
    @ManagedDescription(value="Current Value")
    public long getCurrentValue() {
        return ((IAtomicLong)this.managedObject).get();
    }

    @ManagedAnnotation(value="set", operation=true)
    @ManagedDescription(value="set value")
    public void set(long value) {
        ((IAtomicLong)this.managedObject).set(value);
    }

    @ManagedAnnotation(value="addAndGet", operation=true)
    @ManagedDescription(value="add value and get")
    public long addAndGet(long delta) {
        return ((IAtomicLong)this.managedObject).addAndGet(delta);
    }

    @ManagedAnnotation(value="compareAndSet", operation=true)
    @ManagedDescription(value="compare expected value with current value if equals then set")
    public boolean compareAndSet(long expect, long value) {
        return ((IAtomicLong)this.managedObject).compareAndSet(expect, value);
    }

    @ManagedAnnotation(value="decrementAndGet", operation=true)
    @ManagedDescription(value="decrement the current value and get")
    public long decrementAndGet() {
        return ((IAtomicLong)this.managedObject).decrementAndGet();
    }

    @ManagedAnnotation(value="getAndAdd", operation=true)
    @ManagedDescription(value="get the current value then add")
    public long getAndAdd(long delta) {
        return ((IAtomicLong)this.managedObject).getAndAdd(delta);
    }

    @ManagedAnnotation(value="getAndIncrement", operation=true)
    @ManagedDescription(value="get the current value then increment")
    public long getAndIncrement() {
        return ((IAtomicLong)this.managedObject).getAndIncrement();
    }

    @ManagedAnnotation(value="getAndSet", operation=true)
    @ManagedDescription(value="get the current value then set")
    public long getAndSet(long value) {
        return ((IAtomicLong)this.managedObject).getAndSet(value);
    }

    @ManagedAnnotation(value="incrementAndGet", operation=true)
    @ManagedDescription(value="increment the current value then get")
    public long incrementAndGet() {
        return ((IAtomicLong)this.managedObject).incrementAndGet();
    }

    @ManagedAnnotation(value="partitionKey")
    @ManagedDescription(value="the partitionKey")
    public String getPartitionKey() {
        return ((IAtomicLong)this.managedObject).getPartitionKey();
    }
}

