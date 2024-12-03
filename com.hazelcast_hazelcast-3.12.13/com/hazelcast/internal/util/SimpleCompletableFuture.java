/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.Executor;

public class SimpleCompletableFuture<T>
extends AbstractCompletableFuture<T>
implements InternalCompletableFuture<T> {
    public SimpleCompletableFuture(NodeEngine nodeEngine) {
        super(nodeEngine, nodeEngine.getLogger(SimpleCompletableFuture.class));
    }

    public SimpleCompletableFuture(Executor executor, ILogger logger) {
        super(executor, logger);
    }

    @Override
    public boolean setResult(Object result) {
        return super.setResult(result);
    }

    @Override
    public T join() {
        try {
            return (T)this.get();
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public boolean complete(Object value) {
        return this.setResult(value);
    }
}

