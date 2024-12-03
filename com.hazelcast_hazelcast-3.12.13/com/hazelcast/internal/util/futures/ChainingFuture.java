/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.futures;

import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class ChainingFuture<T>
extends AbstractCompletableFuture<T> {
    private final ExceptionHandler exceptionHandler;

    public ChainingFuture(Iterator<ICompletableFuture<T>> futuresToChain, Executor executor, ExceptionHandler exceptionHandler, ILogger logger) {
        super(executor, logger);
        this.exceptionHandler = exceptionHandler;
        if (!futuresToChain.hasNext()) {
            this.setResult(null);
        } else {
            ICompletableFuture<T> future = futuresToChain.next();
            this.registerCallback(future, futuresToChain);
        }
    }

    private void registerCallback(ICompletableFuture<T> future, final Iterator<ICompletableFuture<T>> invocationIterator) {
        future.andThen(new ExecutionCallback<T>(){

            @Override
            public void onResponse(T response) {
                ChainingFuture.this.advanceOrComplete(response, invocationIterator);
            }

            @Override
            public void onFailure(Throwable t) {
                try {
                    ChainingFuture.this.exceptionHandler.handle(t);
                    ChainingFuture.this.advanceOrComplete(null, invocationIterator);
                }
                catch (Throwable throwable) {
                    ChainingFuture.this.setResult(t);
                }
            }
        });
    }

    private void advanceOrComplete(T response, Iterator<ICompletableFuture<T>> invocationIterator) {
        try {
            boolean hasNext = invocationIterator.hasNext();
            if (!hasNext) {
                this.setResult(response);
            } else {
                ICompletableFuture<T> future = invocationIterator.next();
                this.registerCallback(future, invocationIterator);
            }
        }
        catch (Throwable t) {
            this.setResult(t);
        }
    }

    public static interface ExceptionHandler {
        public <T extends Throwable> void handle(T var1) throws T;
    }
}

