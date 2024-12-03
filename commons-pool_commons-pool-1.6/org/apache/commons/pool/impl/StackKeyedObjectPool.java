/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.apache.commons.pool.BaseKeyedObjectPool;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.PoolUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackKeyedObjectPool<K, V>
extends BaseKeyedObjectPool<K, V>
implements KeyedObjectPool<K, V> {
    protected static final int DEFAULT_MAX_SLEEPING = 8;
    protected static final int DEFAULT_INIT_SLEEPING_CAPACITY = 4;
    @Deprecated
    protected HashMap<K, Stack<V>> _pools = null;
    @Deprecated
    protected KeyedPoolableObjectFactory<K, V> _factory = null;
    @Deprecated
    protected int _maxSleeping = 8;
    @Deprecated
    protected int _initSleepingCapacity = 4;
    @Deprecated
    protected int _totActive = 0;
    @Deprecated
    protected int _totIdle = 0;
    @Deprecated
    protected HashMap<K, Integer> _activeCount = null;

    public StackKeyedObjectPool() {
        this(null, 8, 4);
    }

    public StackKeyedObjectPool(int max) {
        this(null, max, 4);
    }

    public StackKeyedObjectPool(int max, int init) {
        this(null, max, init);
    }

    public StackKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory) {
        this(factory, 8);
    }

    public StackKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int max) {
        this(factory, max, 4);
    }

    public StackKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int max, int init) {
        this._factory = factory;
        this._maxSleeping = max < 0 ? 8 : max;
        this._initSleepingCapacity = init < 1 ? 4 : init;
        this._pools = new HashMap();
        this._activeCount = new HashMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized V borrowObject(K key) throws Exception {
        this.assertOpen();
        Stack<Object> stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity(this._initSleepingCapacity > this._maxSleeping ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        Object obj = null;
        do {
            boolean newlyMade = false;
            if (!stack.empty()) {
                obj = stack.pop();
                --this._totIdle;
            } else {
                if (null == this._factory) {
                    throw new NoSuchElementException("pools without a factory cannot create new objects as needed.");
                }
                obj = this._factory.makeObject(key);
                newlyMade = true;
            }
            if (null == this._factory || null == obj) continue;
            try {
                this._factory.activateObject(key, obj);
                if (!this._factory.validateObject(key, obj)) {
                    throw new Exception("ValidateObject failed");
                }
            }
            catch (Throwable t) {
                PoolUtils.checkRethrow(t);
                try {
                    this._factory.destroyObject(key, obj);
                }
                catch (Throwable t2) {
                    PoolUtils.checkRethrow(t2);
                }
                finally {
                    obj = null;
                }
                if (!newlyMade) continue;
                throw new NoSuchElementException("Could not create a validated object, cause: " + t.getMessage());
            }
        } while (obj == null);
        this.incrementActiveCount(key);
        return (V)obj;
    }

    @Override
    public synchronized void returnObject(K key, V obj) throws Exception {
        int stackSize;
        this.decrementActiveCount(key);
        if (null != this._factory) {
            if (this._factory.validateObject(key, obj)) {
                try {
                    this._factory.passivateObject(key, obj);
                }
                catch (Exception ex) {
                    this._factory.destroyObject(key, obj);
                    return;
                }
            } else {
                return;
            }
        }
        if (this.isClosed()) {
            if (null != this._factory) {
                try {
                    this._factory.destroyObject(key, obj);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            return;
        }
        Stack<Object> stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity(this._initSleepingCapacity > this._maxSleeping ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        if ((stackSize = stack.size()) >= this._maxSleeping) {
            Object staleObj;
            if (stackSize > 0) {
                staleObj = stack.remove(0);
                --this._totIdle;
            } else {
                staleObj = obj;
            }
            if (null != this._factory) {
                try {
                    this._factory.destroyObject(key, staleObj);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        stack.push(obj);
        ++this._totIdle;
    }

    @Override
    public synchronized void invalidateObject(K key, V obj) throws Exception {
        this.decrementActiveCount(key);
        if (null != this._factory) {
            this._factory.destroyObject(key, obj);
        }
        this.notifyAll();
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public synchronized void addObject(K key) throws Exception {
        int stackSize;
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        V obj = this._factory.makeObject(key);
        try {
            if (!this._factory.validateObject(key, obj)) {
                return;
            }
        }
        catch (Exception e) {
            try {
                this._factory.destroyObject(key, obj);
                return;
            }
            catch (Exception e2) {
                // empty catch block
            }
            return;
        }
        this._factory.passivateObject(key, obj);
        Stack<Object> stack = this._pools.get(key);
        if (null == stack) {
            stack = new Stack();
            stack.ensureCapacity(this._initSleepingCapacity > this._maxSleeping ? this._maxSleeping : this._initSleepingCapacity);
            this._pools.put(key, stack);
        }
        if ((stackSize = stack.size()) >= this._maxSleeping) {
            void var5_9;
            if (stackSize > 0) {
                Object e = stack.remove(0);
                --this._totIdle;
            } else {
                V v = obj;
            }
            try {
                this._factory.destroyObject(key, var5_9);
                return;
            }
            catch (Exception e) {
                if (obj != var5_9) return;
                throw e;
            }
        } else {
            stack.push(obj);
            ++this._totIdle;
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return this._totIdle;
    }

    @Override
    public synchronized int getNumActive() {
        return this._totActive;
    }

    @Override
    public synchronized int getNumActive(K key) {
        return this.getActiveCount(key);
    }

    @Override
    public synchronized int getNumIdle(K key) {
        try {
            return this._pools.get(key).size();
        }
        catch (Exception e) {
            return 0;
        }
    }

    @Override
    public synchronized void clear() {
        for (K key : this._pools.keySet()) {
            Stack<V> stack = this._pools.get(key);
            this.destroyStack(key, stack);
        }
        this._totIdle = 0;
        this._pools.clear();
        this._activeCount.clear();
    }

    @Override
    public synchronized void clear(K key) {
        Stack<V> stack = this._pools.remove(key);
        this.destroyStack(key, stack);
    }

    private synchronized void destroyStack(K key, Stack<V> stack) {
        if (null == stack) {
            return;
        }
        if (null != this._factory) {
            Iterator it = stack.iterator();
            while (it.hasNext()) {
                try {
                    this._factory.destroyObject(key, it.next());
                }
                catch (Exception e) {}
            }
        }
        this._totIdle -= stack.size();
        this._activeCount.remove(key);
        stack.clear();
    }

    public synchronized String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.getClass().getName());
        buf.append(" contains ").append(this._pools.size()).append(" distinct pools: ");
        for (K key : this._pools.keySet()) {
            buf.append(" |").append(key).append("|=");
            Stack<V> s = this._pools.get(key);
            buf.append(s.size());
        }
        return buf.toString();
    }

    @Override
    public void close() throws Exception {
        super.close();
        this.clear();
    }

    @Override
    @Deprecated
    public synchronized void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException {
        if (0 < this.getNumActive()) {
            throw new IllegalStateException("Objects are already active");
        }
        this.clear();
        this._factory = factory;
    }

    public synchronized KeyedPoolableObjectFactory<K, V> getFactory() {
        return this._factory;
    }

    private int getActiveCount(K key) {
        try {
            return this._activeCount.get(key);
        }
        catch (NoSuchElementException e) {
            return 0;
        }
        catch (NullPointerException e) {
            return 0;
        }
    }

    private void incrementActiveCount(K key) {
        ++this._totActive;
        Integer old = this._activeCount.get(key);
        if (null == old) {
            this._activeCount.put(key, new Integer(1));
        } else {
            this._activeCount.put(key, new Integer(old + 1));
        }
    }

    private void decrementActiveCount(K key) {
        --this._totActive;
        Integer active = this._activeCount.get(key);
        if (null != active) {
            if (active <= 1) {
                this._activeCount.remove(key);
            } else {
                this._activeCount.put(key, new Integer(active - 1));
            }
        }
    }

    public Map<K, Stack<V>> getPools() {
        return this._pools;
    }

    public int getMaxSleeping() {
        return this._maxSleeping;
    }

    public int getInitSleepingCapacity() {
        return this._initSleepingCapacity;
    }

    public int getTotActive() {
        return this._totActive;
    }

    public int getTotIdle() {
        return this._totIdle;
    }

    public Map<K, Integer> getActiveCount() {
        return this._activeCount;
    }
}

