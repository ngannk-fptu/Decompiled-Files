/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl;

import com.hazelcast.core.ExecutionCallback;

public abstract class SimpleExecutionCallback<E>
implements ExecutionCallback<E> {
    public abstract void notify(Object var1);

    @Override
    public final void onResponse(E response) {
        this.notify(response);
    }

    @Override
    public final void onFailure(Throwable t) {
        this.notify(t);
    }
}

