/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.EventFilter;

public interface EventRegistration
extends DataSerializable {
    public String getId();

    public EventFilter getFilter();

    public Address getSubscriber();

    public boolean isLocalOnly();
}

