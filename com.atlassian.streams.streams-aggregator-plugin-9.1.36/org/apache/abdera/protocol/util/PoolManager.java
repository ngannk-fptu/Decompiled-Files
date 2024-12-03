/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import java.util.Stack;
import org.apache.abdera.protocol.ItemManager;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class PoolManager<T>
implements ItemManager<T> {
    private static final int DEFAULT_SIZE = 25;
    private final Stack<T> pool;

    protected PoolManager() {
        this(25);
    }

    protected PoolManager(int max) {
        this.pool = this.initStack(max);
    }

    private Stack<T> initStack(final int max) {
        return new Stack<T>(){
            private static final long serialVersionUID = -6647024253014661104L;

            @Override
            public T push(T item) {
                Object obj = super.push(item);
                if (this.size() > max) {
                    this.removeElementAt(0);
                }
                return obj;
            }
        };
    }

    protected synchronized T getInstance() {
        return !this.pool.empty() ? this.pool.pop() : this.internalNewInstance();
    }

    @Override
    public synchronized void release(T t) {
        if (t == null || this.pool.contains(t)) {
            return;
        }
        this.pool.push(t);
    }

    protected abstract T internalNewInstance();
}

