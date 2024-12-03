/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.util.CleanerThread;

public class SoftReferenceCache {
    protected final Map map = new HashMap();
    private final boolean synchronous;

    protected SoftReferenceCache() {
        this(false);
    }

    protected SoftReferenceCache(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public synchronized void flush() {
        this.map.clear();
        this.notifyAll();
    }

    protected final synchronized boolean isPresentImpl(Object key) {
        if (!this.map.containsKey(key)) {
            return false;
        }
        Object o = this.map.get(key);
        if (o == null) {
            return true;
        }
        SoftReference sr = (SoftReference)o;
        if ((o = sr.get()) != null) {
            return true;
        }
        this.clearImpl(key);
        return false;
    }

    protected final synchronized boolean isDoneImpl(Object key) {
        Object o = this.map.get(key);
        if (o == null) {
            return false;
        }
        SoftReference sr = (SoftReference)o;
        if ((o = sr.get()) != null) {
            return true;
        }
        this.clearImpl(key);
        return false;
    }

    protected final synchronized Object requestImpl(Object key) {
        if (this.map.containsKey(key)) {
            SoftReference sr;
            Object o = this.map.get(key);
            while (o == null) {
                if (this.synchronous) {
                    return null;
                }
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                if (!this.map.containsKey(key)) break;
                o = this.map.get(key);
            }
            if (o != null && (o = (sr = (SoftReference)o).get()) != null) {
                return o;
            }
        }
        this.map.put(key, null);
        return null;
    }

    protected final synchronized void clearImpl(Object key) {
        this.map.remove(key);
        this.notifyAll();
    }

    protected final synchronized void putImpl(Object key, Object object) {
        if (this.map.containsKey(key)) {
            SoftRefKey ref = new SoftRefKey(object, key);
            this.map.put(key, ref);
            this.notifyAll();
        }
    }

    class SoftRefKey
    extends CleanerThread.SoftReferenceCleared {
        Object key;

        public SoftRefKey(Object o, Object key) {
            super(o);
            this.key = key;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void cleared() {
            SoftReferenceCache cache = SoftReferenceCache.this;
            if (cache == null) {
                return;
            }
            SoftReferenceCache softReferenceCache = cache;
            synchronized (softReferenceCache) {
                if (!cache.map.containsKey(this.key)) {
                    return;
                }
                Object o = cache.map.remove(this.key);
                if (this == o) {
                    cache.notifyAll();
                } else {
                    cache.map.put(this.key, o);
                }
            }
        }
    }
}

