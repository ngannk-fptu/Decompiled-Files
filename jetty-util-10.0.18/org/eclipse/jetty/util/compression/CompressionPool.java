/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.compression;

import java.io.Closeable;
import org.eclipse.jetty.util.Pool;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject
public abstract class CompressionPool<T>
extends ContainerLifeCycle {
    public static final int DEFAULT_CAPACITY = 1024;
    private int _capacity;
    private Pool<Entry> _pool;

    public CompressionPool(int capacity) {
        this._capacity = capacity;
    }

    public int getCapacity() {
        return this._capacity;
    }

    public void setCapacity(int capacity) {
        if (this.isStarted()) {
            throw new IllegalStateException("Already Started");
        }
        this._capacity = capacity;
    }

    public Pool<Entry> getPool() {
        return this._pool;
    }

    protected abstract T newPooled();

    protected abstract void end(T var1);

    protected abstract void reset(T var1);

    public Entry acquire() {
        Pool.Entry acquiredEntry;
        Entry entry = null;
        if (this._pool != null && (acquiredEntry = this._pool.acquire(e -> new Entry(this.newPooled(), (Pool.Entry)e))) != null) {
            entry = (Entry)acquiredEntry.getPooled();
        }
        return entry == null ? new Entry(this.newPooled()) : entry;
    }

    public void release(Entry entry) {
        entry.release();
    }

    @Override
    protected void doStart() throws Exception {
        if (this._capacity > 0) {
            this._pool = new Pool(Pool.StrategyType.RANDOM, this._capacity, true);
            this.addBean(this._pool);
        }
        super.doStart();
    }

    @Override
    public void doStop() throws Exception {
        if (this._pool != null) {
            this._pool.close();
            this.removeBean(this._pool);
            this._pool = null;
        }
        super.doStop();
    }

    @Override
    public String toString() {
        return String.format("%s@%x{%s,size=%d,capacity=%s}", this.getClass().getSimpleName(), this.hashCode(), this.getState(), this._pool == null ? -1 : this._pool.size(), this._capacity);
    }

    public class Entry
    implements Closeable {
        private final T _value;
        private final Pool.Entry _entry;

        Entry(T value) {
            this(value, null);
        }

        Entry(T value, Pool.Entry entry) {
            this._value = value;
            this._entry = entry;
        }

        public T get() {
            return this._value;
        }

        public void release() {
            CompressionPool.this.reset(this._value);
            if (this._entry != null) {
                if (!CompressionPool.this._pool.release(this._entry) && CompressionPool.this._pool.remove(this._entry)) {
                    this.close();
                }
            } else {
                this.close();
            }
        }

        @Override
        public void close() {
            CompressionPool.this.end(this._value);
        }
    }
}

