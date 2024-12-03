/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;

@BinaryInterface
public interface EventData
extends DataSerializable {
    public String getSource();

    public String getMapName();

    public Address getCaller();

    public int getEventType();
}

