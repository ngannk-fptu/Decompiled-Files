/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.operation.AddInterceptorOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.function.Supplier;

public class AddInterceptorOperationSupplier
implements Supplier<Operation> {
    private final String id;
    private final String name;
    private final MapInterceptor mapInterceptor;

    public AddInterceptorOperationSupplier(String id, String name, MapInterceptor mapInterceptor) {
        this.id = id;
        this.name = name;
        this.mapInterceptor = mapInterceptor;
    }

    @Override
    public Operation get() {
        return new AddInterceptorOperation(this.id, this.mapInterceptor, this.name);
    }
}

