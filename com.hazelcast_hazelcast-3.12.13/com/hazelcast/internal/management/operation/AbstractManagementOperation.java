/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.internal.management.ManagementDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;

public abstract class AbstractManagementOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return ManagementDataSerializerHook.F_ID;
    }
}

