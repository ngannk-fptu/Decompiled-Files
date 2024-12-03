/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

public final class SimplePool {
    private Object[] pool;
    private int max;
    private int current = -1;

    public SimplePool(int max) {
        this.max = max;
        this.pool = new Object[max];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(Object o) {
        int idx = -1;
        SimplePool simplePool = this;
        synchronized (simplePool) {
            if (this.current < this.max - 1) {
                idx = ++this.current;
            }
            if (idx >= 0) {
                this.pool[idx] = o;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get() {
        SimplePool simplePool = this;
        synchronized (simplePool) {
            if (this.current >= 0) {
                Object o = this.pool[this.current];
                this.pool[this.current] = null;
                --this.current;
                return o;
            }
        }
        return null;
    }

    public int getMax() {
        return this.max;
    }

    Object[] getPool() {
        return this.pool;
    }
}

