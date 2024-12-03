/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.AbstractCompletableFuture;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class BasicCompletableFuture<V>
extends AbstractCompletableFuture<V> {
    final Future<V> delegate;

    BasicCompletableFuture(Future<V> delegate, NodeEngine nodeEngine) {
        super(nodeEngine, nodeEngine.getLogger(BasicCompletableFuture.class));
        this.delegate = delegate;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (V)this.ensureResultSet(timeout, unit);
    }

    private Object ensureResultSet(long timeout, TimeUnit unit) throws ExecutionException, CancellationException {
        Throwable result = null;
        try {
            result = this.delegate.get(timeout, unit);
        }
        catch (TimeoutException ex) {
            ExceptionUtil.sneakyThrow(ex);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            ExceptionUtil.sneakyThrow(ex);
        }
        catch (ExecutionException ex) {
            this.setResult(ex);
            throw ex;
        }
        catch (CancellationException ex) {
            this.setResult(ex);
            throw ex;
        }
        catch (Throwable t) {
            result = t;
        }
        this.setResult(result);
        return result;
    }

    @Override
    public boolean isDone() {
        if (this.delegate.isDone()) {
            try {
                this.ensureResultSet(Long.MAX_VALUE, TimeUnit.DAYS);
            }
            catch (ExecutionException ignored) {
                EmptyStatement.ignore(ignored);
            }
            catch (CancellationException ignored) {
                EmptyStatement.ignore(ignored);
            }
            return true;
        }
        return super.isDone();
    }

    @Override
    public boolean isCancelled() {
        if (this.delegate.isCancelled()) {
            this.cancel(true);
            return true;
        }
        return super.isCancelled();
    }

    @Override
    public boolean shouldCancel(boolean mayInterruptIfRunning) {
        if (!this.delegate.isCancelled()) {
            this.delegate.cancel(mayInterruptIfRunning);
        }
        return true;
    }
}

