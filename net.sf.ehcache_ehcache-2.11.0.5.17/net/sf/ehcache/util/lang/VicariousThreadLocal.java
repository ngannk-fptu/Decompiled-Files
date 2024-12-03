/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.lang;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class VicariousThreadLocal<T>
extends ThreadLocal<T> {
    private static final ThreadLocal<WeakReference<Thread>> weakThread = new ThreadLocal();
    private static final Object UNINITIALISED = new Object();
    private final ThreadLocal<WeakReference<Holder>> local = new ThreadLocal();
    private volatile Holder strongRefs;
    private static final AtomicReferenceFieldUpdater<VicariousThreadLocal, Holder> strongRefsUpdater = AtomicReferenceFieldUpdater.newUpdater(VicariousThreadLocal.class, Holder.class, "strongRefs");
    private final ReferenceQueue<Object> queue = new ReferenceQueue();

    static WeakReference<Thread> currentThreadRef() {
        WeakReference<Thread> ref = weakThread.get();
        if (ref == null) {
            ref = new WeakReference<Thread>(Thread.currentThread());
            weakThread.set(ref);
        }
        return ref;
    }

    @Override
    public T get() {
        Object value;
        Holder holder;
        WeakReference<Holder> ref = this.local.get();
        if (ref != null) {
            holder = (Holder)ref.get();
            value = holder.value;
            if (value != UNINITIALISED) {
                return value;
            }
        } else {
            holder = this.createHolder();
        }
        value = this.initialValue();
        holder.value = value;
        return value;
    }

    @Override
    public void set(T value) {
        WeakReference<Holder> ref = this.local.get();
        Holder holder = ref != null ? (Holder)ref.get() : this.createHolder();
        holder.value = value;
    }

    private Holder createHolder() {
        Holder old;
        this.poll();
        Holder holder = new Holder(this.queue);
        WeakReference<Holder> ref = new WeakReference<Holder>(holder);
        do {
            holder.next = old = this.strongRefs;
        } while (!strongRefsUpdater.compareAndSet(this, old, holder));
        this.local.set(ref);
        return holder;
    }

    @Override
    public void remove() {
        WeakReference<Holder> ref = this.local.get();
        if (ref != null) {
            ((Holder)ref.get()).value = UNINITIALISED;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void poll() {
        ReferenceQueue<Object> referenceQueue = this.queue;
        synchronized (referenceQueue) {
            if (this.queue.poll() == null) {
                return;
            }
            while (this.queue.poll() != null) {
            }
            Holder first = this.strongRefs;
            if (first == null) {
                return;
            }
            Holder link = first;
            Holder next = link.next;
            while (next != null) {
                if (next.get() == null) {
                    link.next = next = next.next;
                    continue;
                }
                link = next;
                next = next.next;
            }
            if (first.get() == null && !strongRefsUpdater.weakCompareAndSet(this, first, first.next)) {
                first.value = null;
            }
        }
    }

    private static class Holder
    extends WeakReference<Object> {
        Holder next;
        Object value = UNINITIALISED;

        Holder(ReferenceQueue<Object> queue) {
            super(VicariousThreadLocal.currentThreadRef(), queue);
        }
    }
}

