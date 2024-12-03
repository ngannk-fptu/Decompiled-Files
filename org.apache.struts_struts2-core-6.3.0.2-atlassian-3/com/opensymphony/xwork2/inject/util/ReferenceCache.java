/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.Function;
import com.opensymphony.xwork2.inject.util.ReferenceMap;
import com.opensymphony.xwork2.inject.util.ReferenceType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class ReferenceCache<K, V>
extends ReferenceMap<K, V> {
    private static final long serialVersionUID = 0L;
    transient ConcurrentMap<Object, Future<V>> futures = new ConcurrentHashMap<Object, Future<V>>();
    transient ThreadLocal<Future<V>> localFuture = new ThreadLocal();

    public ReferenceCache(ReferenceType keyReferenceType, ReferenceType valueReferenceType) {
        super(keyReferenceType, valueReferenceType);
    }

    public ReferenceCache() {
        super(ReferenceType.STRONG, ReferenceType.STRONG);
    }

    protected abstract V create(K var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    V internalCreate(K key) {
        try {
            FutureTask futureTask = new FutureTask(new CallableCreate(key));
            Object keyReference = this.referenceKey(key);
            Future future = this.futures.putIfAbsent(keyReference, futureTask);
            if (future != null) return future.get();
            try {
                if (this.localFuture.get() != null) {
                    throw new IllegalStateException("Nested creations within the same cache are not allowed.");
                }
                this.localFuture.set(futureTask);
                futureTask.run();
                Object value = futureTask.get();
                this.putStrategy().execute(this, keyReference, this.referenceValue(keyReference, value));
                Object v = value;
                return v;
            }
            finally {
                this.localFuture.remove();
                this.futures.remove(keyReference);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (!(cause instanceof Error)) throw new RuntimeException(cause);
            throw (Error)cause;
        }
    }

    @Override
    public V get(Object key) {
        Object value = super.get(key);
        return value == null ? this.internalCreate(key) : value;
    }

    protected void cancel() {
        Future<V> future = this.localFuture.get();
        if (future == null) {
            throw new IllegalStateException("Not in create().");
        }
        future.cancel(false);
    }

    public static <K, V> ReferenceCache<K, V> of(ReferenceType keyReferenceType, ReferenceType valueReferenceType, final Function<? super K, ? extends V> function) {
        ReferenceCache.ensureNotNull(function);
        return new ReferenceCache<K, V>(keyReferenceType, valueReferenceType){
            private static final long serialVersionUID = 0L;

            @Override
            protected V create(K key) {
                return function.apply(key);
            }
        };
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.futures = new ConcurrentHashMap<Object, Future<V>>();
        this.localFuture = new ThreadLocal();
    }

    class CallableCreate
    implements Callable<V> {
        K key;

        public CallableCreate(K key) {
            this.key = key;
        }

        @Override
        public V call() {
            Object value = ReferenceCache.this.internalGet(this.key);
            if (value != null) {
                return value;
            }
            value = ReferenceCache.this.create(this.key);
            if (value == null) {
                throw new NullPointerException("create(K) returned null for: " + this.key);
            }
            return value;
        }
    }
}

