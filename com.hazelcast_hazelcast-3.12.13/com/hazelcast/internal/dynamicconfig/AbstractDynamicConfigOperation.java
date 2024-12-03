/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;

public abstract class AbstractDynamicConfigOperation
extends Operation
implements IdentifiedDataSerializable {
    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public String getServiceName() {
        return "configuration-service";
    }
}

