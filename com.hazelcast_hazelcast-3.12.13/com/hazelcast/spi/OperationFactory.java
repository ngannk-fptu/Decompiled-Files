/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;

public interface OperationFactory
extends IdentifiedDataSerializable {
    public Operation createOperation();
}

