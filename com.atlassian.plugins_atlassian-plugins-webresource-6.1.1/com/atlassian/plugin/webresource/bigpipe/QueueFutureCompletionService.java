/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  javax.annotation.concurrent.GuardedBy
 */
package com.atlassian.plugin.webresource.bigpipe;

import com.atlassian.plugin.webresource.bigpipe.FutureCompletionService;
import com.atlassian.plugin.webresource.bigpipe.KeyedValue;
import com.atlassian.plugin.webresource.util.ConsList;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.concurrent.GuardedBy;

class QueueFutureCompletionService<K, T>
implements FutureCompletionService<K, T> {
    private final Object lock = new Object();
    @GuardedBy(value="lock")
    private final BlockingQueue<KeyedValue<K, T>> completionQueue = new LinkedBlockingQueue<KeyedValue<K, T>>();
    @GuardedBy(value="lock")
    private final PendingPromises<K> pendingPromises = new PendingPromises();
    private final Set<K> completeKeys = Collections.newSetFromMap(new ConcurrentHashMap());

    QueueFutureCompletionService() {
    }

    @Override
    public void add(K key, CompletionStage<T> promise) {
        this.pendingPromises.add(key);
        promise.whenComplete((result, throwable) -> {
            if (result != null) {
                this.complete(KeyedValue.success(key, result));
            } else {
                this.complete(KeyedValue.fail(key, throwable));
            }
        });
    }

    @Override
    public void forceCompleteAll() {
        RuntimeException t = new RuntimeException("Deadline exceeded");
        for (K key : this.pendingPromises) {
            this.complete(KeyedValue.fail(key, t));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void waitAnyPendingToComplete() throws InterruptedException {
        Object object = this.lock;
        synchronized (object) {
            while (this.completionQueue.isEmpty() && !this.pendingPromises.isEmpty()) {
                this.lock.wait(60000L);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isComplete() {
        Object object = this.lock;
        synchronized (object) {
            return this.pendingPromises.isEmpty() && this.completionQueue.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void complete(KeyedValue<K, T> value) {
        if (this.completeKeys.add(value.key())) {
            Object object = this.lock;
            synchronized (object) {
                this.completionQueue.add(value);
                this.pendingPromises.remove(value.key());
                this.lock.notifyAll();
            }
        }
    }

    @Override
    public Iterable<KeyedValue<K, T>> poll() {
        return this.getResult((KeyedValue)this.completionQueue.poll());
    }

    @Override
    public Iterable<KeyedValue<K, T>> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return this.getResult(this.completionQueue.poll(timeout, unit));
    }

    private Iterable<KeyedValue<K, T>> getResult(KeyedValue<K, T> promise) {
        LinkedList<KeyedValue<K, T>> results = new LinkedList<KeyedValue<K, T>>();
        if (null != promise) {
            results.add(promise);
        }
        this.completionQueue.drainTo(results);
        return results;
    }

    private static final class PendingPromises<A>
    implements Iterable<A> {
        private final AtomicReference<ConsList<A>> promises = new AtomicReference(ConsList.empty());

        private PendingPromises() {
        }

        void add(A p) {
            this.promises.updateAndGet(input -> input.prepend(p));
        }

        void remove(A p) {
            this.promises.updateAndGet(input -> input.remove(p));
        }

        boolean isEmpty() {
            return Iterables.isEmpty((Iterable)this);
        }

        @Override
        public Iterator<A> iterator() {
            return this.promises.get().iterator();
        }
    }
}

