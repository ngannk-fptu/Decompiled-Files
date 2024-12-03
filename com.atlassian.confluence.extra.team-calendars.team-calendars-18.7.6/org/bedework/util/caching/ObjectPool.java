/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.caching;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;
import org.bedework.util.misc.Logged;

public class ObjectPool<T>
extends Logged
implements Serializable {
    private final WeakHashMap<T, SoftReference<T>> pool = new WeakHashMap();
    private static long refs;
    private static long hits;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T get(T val) {
        T tval;
        if (this.debug && refs % 500L == 0L) {
            this.debug("pool refs " + refs + ": hits " + hits);
        }
        ++refs;
        SoftReference<T> poolVal = this.pool.get(val);
        if (poolVal != null && (tval = poolVal.get()) != null) {
            ++hits;
            return tval;
        }
        WeakHashMap<T, SoftReference<T>> weakHashMap = this.pool;
        synchronized (weakHashMap) {
            this.pool.put(val, new SoftReference<T>(val));
            return val;
        }
    }
}

