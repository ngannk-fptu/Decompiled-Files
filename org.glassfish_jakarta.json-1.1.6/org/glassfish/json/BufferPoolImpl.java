/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.glassfish.json.api.BufferPool;

class BufferPoolImpl
implements BufferPool {
    private volatile WeakReference<ConcurrentLinkedQueue<char[]>> queue;

    BufferPoolImpl() {
    }

    @Override
    public final char[] take() {
        char[] t = this.getQueue().poll();
        if (t == null) {
            return new char[4096];
        }
        return t;
    }

    private ConcurrentLinkedQueue<char[]> getQueue() {
        ConcurrentLinkedQueue<char[]> d;
        WeakReference<ConcurrentLinkedQueue<char[]>> q = this.queue;
        if (q != null && (d = (ConcurrentLinkedQueue<char[]>)q.get()) != null) {
            return d;
        }
        d = new ConcurrentLinkedQueue<char[]>();
        this.queue = new WeakReference<ConcurrentLinkedQueue<char[]>>(d);
        return d;
    }

    @Override
    public final void recycle(char[] t) {
        this.getQueue().offer(t);
    }
}

