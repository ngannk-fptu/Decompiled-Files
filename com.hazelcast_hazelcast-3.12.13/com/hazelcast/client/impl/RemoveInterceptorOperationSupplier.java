/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.map.impl.operation.RemoveInterceptorOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;

public class RemoveInterceptorOperationSupplier
implements Supplier<Operation> {
    private final String id;
    private final String name;

    public RemoveInterceptorOperationSupplier(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Operation get() {
        return new RemoveInterceptorOperation(this.name, this.id);
    }
}

