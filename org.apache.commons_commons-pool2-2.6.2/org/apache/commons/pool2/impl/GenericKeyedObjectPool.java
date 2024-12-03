/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PoolUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.impl.BaseGenericObjectPool;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolMXBean;
import org.apache.commons.pool2.impl.LinkedBlockingDeque;

public class GenericKeyedObjectPool<K, T>
extends BaseGenericObjectPool<T>
implements KeyedObjectPool<K, T>,
GenericKeyedObjectPoolMXBean<K> {
    private volatile int maxIdlePerKey = 8;
    private volatile int minIdlePerKey = 0;
    private volatile int maxTotalPerKey = 8;
    private final KeyedPooledObjectFactory<K, T> factory;
    private final boolean fairness;
    private final Map<K, ObjectDeque<T>> poolMap = new ConcurrentHashMap<K, ObjectDeque<T>>();
    private final List<K> poolKeyList = new ArrayList<K>();
    private final ReadWriteLock keyLock = new ReentrantReadWriteLock(true);
    private final AtomicInteger numTotal = new AtomicInteger(0);
    private Iterator<K> evictionKeyIterator = null;
    private K evictionKey = null;
    private static final String ONAME_BASE = "org.apache.commons.pool2:type=GenericKeyedObjectPool,name=";

    public GenericKeyedObjectPool(KeyedPooledObjectFactory<K, T> factory) {
        this(factory, new GenericKeyedObjectPoolConfig());
    }

    public GenericKeyedObjectPool(KeyedPooledObjectFactory<K, T> factory, GenericKeyedObjectPoolConfig<T> config) {
        super(config, ONAME_BASE, config.getJmxNamePrefix());
        if (factory == null) {
            this.jmxUnregister();
            throw new IllegalArgumentException("factory may not be null");
        }
        this.factory = factory;
        this.fairness = config.getFairness();
        this.setConfig(config);
    }

    @Override
    public int getMaxTotalPerKey() {
        return this.maxTotalPerKey;
    }

    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    @Override
    public int getMaxIdlePerKey() {
        return this.maxIdlePerKey;
    }

    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    @Override
    public int getMinIdlePerKey() {
        int maxIdlePerKeySave = this.getMaxIdlePerKey();
        if (this.minIdlePerKey > maxIdlePerKeySave) {
            return maxIdlePerKeySave;
        }
        return this.minIdlePerKey;
    }

    @Override
    public void setConfig(GenericKeyedObjectPoolConfig<T> conf) {
        super.setConfig(conf);
        this.setMaxIdlePerKey(conf.getMaxIdlePerKey());
        this.setMaxTotalPerKey(conf.getMaxTotalPerKey());
        this.setMaxTotal(conf.getMaxTotal());
        this.setMinIdlePerKey(conf.getMinIdlePerKey());
    }

    public KeyedPooledObjectFactory<K, T> getFactory() {
        return this.factory;
    }

    @Override
    public T borrowObject(K key) throws Exception {
        return this.borrowObject(key, this.getMaxWaitMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T borrowObject(K key, long borrowMaxWaitMillis) throws Exception {
        this.assertOpen();
        PooledObject<T> p = null;
        boolean blockWhenExhausted = this.getBlockWhenExhausted();
        long waitTime = System.currentTimeMillis();
        ObjectDeque<T> objectDeque = this.register(key);
        try {
            while (p == null) {
                boolean create;
                block19: {
                    create = false;
                    p = objectDeque.getIdleObjects().pollFirst();
                    if (p == null && (p = this.create(key)) != null) {
                        create = true;
                    }
                    if (blockWhenExhausted) {
                        if (p == null) {
                            p = borrowMaxWaitMillis < 0L ? objectDeque.getIdleObjects().takeFirst() : objectDeque.getIdleObjects().pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
                        }
                        if (p == null) {
                            throw new NoSuchElementException("Timeout waiting for idle object");
                        }
                    } else if (p == null) {
                        throw new NoSuchElementException("Pool exhausted");
                    }
                    if (!p.allocate()) {
                        p = null;
                    }
                    if (p == null) continue;
                    try {
                        this.factory.activateObject(key, p);
                    }
                    catch (Exception e) {
                        try {
                            this.destroy(key, p, true);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        p = null;
                        if (!create) break block19;
                        NoSuchElementException nsee = new NoSuchElementException("Unable to activate object");
                        nsee.initCause(e);
                        throw nsee;
                    }
                }
                if (p == null || !this.getTestOnBorrow() && (!create || !this.getTestOnCreate())) continue;
                boolean validate = false;
                Throwable validationThrowable = null;
                try {
                    validate = this.factory.validateObject(key, p);
                }
                catch (Throwable t) {
                    PoolUtils.checkRethrow(t);
                    validationThrowable = t;
                }
                if (validate) continue;
                try {
                    this.destroy(key, p, true);
                    this.destroyedByBorrowValidationCount.incrementAndGet();
                }
                catch (Exception t) {
                    // empty catch block
                }
                p = null;
                if (!create) continue;
                NoSuchElementException nsee = new NoSuchElementException("Unable to validate object");
                nsee.initCause(validationThrowable);
                throw nsee;
            }
        }
        finally {
            this.deregister(key);
        }
        this.updateStatsBorrow(p, System.currentTimeMillis() - waitTime);
        return p.getObject();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void returnObject(K key, T obj) {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        PooledObject<T> p = objectDeque.getAllObjects().get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
        if (p == null) {
            throw new IllegalStateException("Returned object not currently part of this pool");
        }
        this.markReturningState(p);
        long activeTime = p.getActiveTimeMillis();
        try {
            if (this.getTestOnReturn() && !this.factory.validateObject(key, p)) {
                try {
                    this.destroy(key, p, true);
                }
                catch (Exception e) {
                    this.swallowException(e);
                }
                this.whenWaitersAddObject(key, ((ObjectDeque)objectDeque).idleObjects);
                return;
            }
            try {
                this.factory.passivateObject(key, p);
            }
            catch (Exception e1) {
                this.swallowException(e1);
                try {
                    this.destroy(key, p, true);
                }
                catch (Exception e) {
                    this.swallowException(e);
                }
                this.whenWaitersAddObject(key, ((ObjectDeque)objectDeque).idleObjects);
                if (this.hasBorrowWaiters()) {
                    this.reuseCapacity();
                }
                this.updateStatsReturn(activeTime);
                return;
            }
            if (!p.deallocate()) {
                throw new IllegalStateException("Object has already been returned to this pool");
            }
            int maxIdle = this.getMaxIdlePerKey();
            LinkedBlockingDeque<PooledObject<T>> idleObjects = objectDeque.getIdleObjects();
            if (this.isClosed() || maxIdle > -1 && maxIdle <= idleObjects.size()) {
                try {
                    this.destroy(key, p, true);
                }
                catch (Exception e) {
                    this.swallowException(e);
                }
            } else {
                if (this.getLifo()) {
                    idleObjects.addFirst(p);
                } else {
                    idleObjects.addLast(p);
                }
                if (this.isClosed()) {
                    this.clear(key);
                }
            }
        }
        finally {
            if (this.hasBorrowWaiters()) {
                this.reuseCapacity();
            }
            this.updateStatsReturn(activeTime);
        }
    }

    private void whenWaitersAddObject(K key, LinkedBlockingDeque<PooledObject<T>> idleObjects) {
        if (idleObjects.hasTakeWaiters()) {
            try {
                this.addObject(key);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateObject(K key, T obj) throws Exception {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        PooledObject<T> p = objectDeque.getAllObjects().get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
        if (p == null) {
            throw new IllegalStateException("Object not currently part of this pool");
        }
        PooledObject<T> pooledObject = p;
        synchronized (pooledObject) {
            if (p.getState() != PooledObjectState.INVALID) {
                this.destroy(key, p, true);
            }
        }
        if (((ObjectDeque)objectDeque).idleObjects.hasTakeWaiters()) {
            this.addObject(key);
        }
    }

    @Override
    public void clear() {
        Iterator<K> iter = this.poolMap.keySet().iterator();
        while (iter.hasNext()) {
            this.clear(iter.next());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear(K key) {
        ObjectDeque<T> objectDeque = this.register(key);
        try {
            LinkedBlockingDeque<PooledObject<T>> idleObjects = objectDeque.getIdleObjects();
            PooledObject<T> p = idleObjects.poll();
            while (p != null) {
                try {
                    this.destroy(key, p, true);
                }
                catch (Exception e) {
                    this.swallowException(e);
                }
                p = idleObjects.poll();
            }
        }
        finally {
            this.deregister(key);
        }
    }

    @Override
    public int getNumActive() {
        return this.numTotal.get() - this.getNumIdle();
    }

    @Override
    public int getNumIdle() {
        Iterator<ObjectDeque<T>> iter = this.poolMap.values().iterator();
        int result = 0;
        while (iter.hasNext()) {
            result += iter.next().getIdleObjects().size();
        }
        return result;
    }

    @Override
    public int getNumActive(K key) {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        if (objectDeque != null) {
            return objectDeque.getAllObjects().size() - objectDeque.getIdleObjects().size();
        }
        return 0;
    }

    @Override
    public int getNumIdle(K key) {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        return objectDeque != null ? objectDeque.getIdleObjects().size() : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.isClosed()) {
            return;
        }
        Object object = this.closeLock;
        synchronized (object) {
            if (this.isClosed()) {
                return;
            }
            this.stopEvitor();
            this.closed = true;
            this.clear();
            this.jmxUnregister();
            Iterator<ObjectDeque<T>> iter = this.poolMap.values().iterator();
            while (iter.hasNext()) {
                iter.next().getIdleObjects().interuptTakeWaiters();
            }
            this.clear();
        }
    }

    public void clearOldest() {
        TreeMap<PooledObject<T>, K> map = new TreeMap<PooledObject<T>, K>();
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            K k = entry.getKey();
            ObjectDeque<T> deque = entry.getValue();
            if (deque == null) continue;
            LinkedBlockingDeque<PooledObject<T>> idleObjects = deque.getIdleObjects();
            for (PooledObject<T> p : idleObjects) {
                map.put(p, k);
            }
        }
        int itemsToRemove = (int)((double)map.size() * 0.15) + 1;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext() && itemsToRemove > 0) {
            Map.Entry entry = iter.next();
            Object key = entry.getValue();
            PooledObject p = (PooledObject)entry.getKey();
            boolean destroyed = true;
            try {
                destroyed = this.destroy(key, p, false);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            if (!destroyed) continue;
            --itemsToRemove;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reuseCapacity() {
        int maxTotalPerKeySave = this.getMaxTotalPerKey();
        int maxQueueLength = 0;
        LinkedBlockingDeque<PooledObject<T>> mostLoaded = null;
        K loadedKey = null;
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            K k = entry.getKey();
            ObjectDeque<T> deque = entry.getValue();
            if (deque == null) continue;
            LinkedBlockingDeque<PooledObject<T>> pool = deque.getIdleObjects();
            int queueLength = pool.getTakeQueueLength();
            if (this.getNumActive(k) >= maxTotalPerKeySave || queueLength <= maxQueueLength) continue;
            maxQueueLength = queueLength;
            mostLoaded = pool;
            loadedKey = k;
        }
        if (mostLoaded != null) {
            this.register(loadedKey);
            try {
                PooledObject<T> p = this.create(loadedKey);
                if (p != null) {
                    this.addIdleObject(loadedKey, p);
                }
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            finally {
                this.deregister(loadedKey);
            }
        }
    }

    private boolean hasBorrowWaiters() {
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            LinkedBlockingDeque<PooledObject<T>> pool;
            ObjectDeque<T> deque = entry.getValue();
            if (deque == null || !(pool = deque.getIdleObjects()).hasTakeWaiters()) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void evict() throws Exception {
        this.assertOpen();
        if (this.getNumIdle() == 0) {
            return;
        }
        Object underTest = null;
        EvictionPolicy evictionPolicy = this.getEvictionPolicy();
        Object object = this.evictionLock;
        synchronized (object) {
            EvictionConfig evictionConfig = new EvictionConfig(this.getMinEvictableIdleTimeMillis(), this.getSoftMinEvictableIdleTimeMillis(), this.getMinIdlePerKey());
            boolean testWhileIdle = this.getTestWhileIdle();
            int m = this.getNumTests();
            for (int i = 0; i < m; ++i) {
                boolean evict;
                Deque idleObjects;
                if (this.evictionIterator == null || !this.evictionIterator.hasNext()) {
                    if (this.evictionKeyIterator == null || !this.evictionKeyIterator.hasNext()) {
                        ArrayList<K> keyCopy = new ArrayList<K>();
                        Lock readLock = this.keyLock.readLock();
                        readLock.lock();
                        try {
                            keyCopy.addAll(this.poolKeyList);
                        }
                        finally {
                            readLock.unlock();
                        }
                        this.evictionKeyIterator = keyCopy.iterator();
                    }
                    while (this.evictionKeyIterator.hasNext()) {
                        this.evictionKey = this.evictionKeyIterator.next();
                        ObjectDeque<T> objectDeque = this.poolMap.get(this.evictionKey);
                        if (objectDeque == null) continue;
                        LinkedBlockingDeque<PooledObject<T>> idleObjects2 = objectDeque.getIdleObjects();
                        this.evictionIterator = new BaseGenericObjectPool.EvictionIterator(this, idleObjects2);
                        if (this.evictionIterator.hasNext()) break;
                        this.evictionIterator = null;
                    }
                }
                if (this.evictionIterator == null) {
                    return;
                }
                try {
                    underTest = this.evictionIterator.next();
                    idleObjects = this.evictionIterator.getIdleObjects();
                }
                catch (NoSuchElementException nsee) {
                    --i;
                    this.evictionIterator = null;
                    continue;
                }
                if (!underTest.startEvictionTest()) {
                    --i;
                    continue;
                }
                try {
                    evict = evictionPolicy.evict(evictionConfig, underTest, this.poolMap.get(this.evictionKey).getIdleObjects().size());
                }
                catch (Throwable t) {
                    PoolUtils.checkRethrow(t);
                    this.swallowException(new Exception(t));
                    evict = false;
                }
                if (evict) {
                    this.destroy(this.evictionKey, (PooledObject<T>)underTest, true);
                    this.destroyedByEvictorCount.incrementAndGet();
                    continue;
                }
                if (testWhileIdle) {
                    boolean active = false;
                    try {
                        this.factory.activateObject(this.evictionKey, (PooledObject<T>)underTest);
                        active = true;
                    }
                    catch (Exception e) {
                        this.destroy(this.evictionKey, (PooledObject<T>)underTest, true);
                        this.destroyedByEvictorCount.incrementAndGet();
                    }
                    if (active) {
                        if (!this.factory.validateObject(this.evictionKey, (PooledObject<T>)underTest)) {
                            this.destroy(this.evictionKey, (PooledObject<T>)underTest, true);
                            this.destroyedByEvictorCount.incrementAndGet();
                        } else {
                            try {
                                this.factory.passivateObject(this.evictionKey, (PooledObject<T>)underTest);
                            }
                            catch (Exception e) {
                                this.destroy(this.evictionKey, (PooledObject<T>)underTest, true);
                                this.destroyedByEvictorCount.incrementAndGet();
                            }
                        }
                    }
                }
                if (underTest.endEvictionTest(idleObjects)) continue;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PooledObject<T> create(K key) throws Exception {
        int maxTotalPerKeySave = this.getMaxTotalPerKey();
        if (maxTotalPerKeySave < 0) {
            maxTotalPerKeySave = Integer.MAX_VALUE;
        }
        int maxTotal = this.getMaxTotal();
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        boolean loop = true;
        while (loop) {
            int newNumTotal = this.numTotal.incrementAndGet();
            if (maxTotal > -1 && newNumTotal > maxTotal) {
                this.numTotal.decrementAndGet();
                if (this.getNumIdle() == 0) {
                    return null;
                }
                this.clearOldest();
                continue;
            }
            loop = false;
        }
        Boolean create = null;
        while (create == null) {
            Object object = ((ObjectDeque)objectDeque).makeObjectCountLock;
            synchronized (object) {
                long newCreateCount = objectDeque.getCreateCount().incrementAndGet();
                if (newCreateCount > (long)maxTotalPerKeySave) {
                    objectDeque.getCreateCount().decrementAndGet();
                    if (((ObjectDeque)objectDeque).makeObjectCount == 0L) {
                        create = Boolean.FALSE;
                    } else {
                        ((ObjectDeque)objectDeque).makeObjectCountLock.wait();
                    }
                } else {
                    ((ObjectDeque)objectDeque).makeObjectCount++;
                    create = Boolean.TRUE;
                }
            }
        }
        if (!create.booleanValue()) {
            this.numTotal.decrementAndGet();
            return null;
        }
        PooledObject<T> p = null;
        try {
            p = this.factory.makeObject(key);
        }
        catch (Exception e) {
            this.numTotal.decrementAndGet();
            objectDeque.getCreateCount().decrementAndGet();
            throw e;
        }
        finally {
            Object object = ((ObjectDeque)objectDeque).makeObjectCountLock;
            synchronized (object) {
                ((ObjectDeque)objectDeque).makeObjectCount--;
                ((ObjectDeque)objectDeque).makeObjectCountLock.notifyAll();
            }
        }
        this.createdCount.incrementAndGet();
        objectDeque.getAllObjects().put(new BaseGenericObjectPool.IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean destroy(K key, PooledObject<T> toDestroy, boolean always) throws Exception {
        ObjectDeque<T> objectDeque = this.register(key);
        try {
            boolean isIdle = objectDeque.getIdleObjects().remove(toDestroy);
            if (isIdle || always) {
                objectDeque.getAllObjects().remove(new BaseGenericObjectPool.IdentityWrapper<T>(toDestroy.getObject()));
                toDestroy.invalidate();
                try {
                    this.factory.destroyObject(key, toDestroy);
                }
                finally {
                    objectDeque.getCreateCount().decrementAndGet();
                    this.destroyedCount.incrementAndGet();
                    this.numTotal.decrementAndGet();
                }
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.deregister(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ObjectDeque<T> register(K k) {
        Lock lock = this.keyLock.readLock();
        ObjectDeque<Object> objectDeque = null;
        try {
            lock.lock();
            objectDeque = this.poolMap.get(k);
            if (objectDeque == null) {
                lock.unlock();
                lock = this.keyLock.writeLock();
                lock.lock();
                objectDeque = this.poolMap.get(k);
                if (objectDeque == null) {
                    objectDeque = new ObjectDeque(this.fairness);
                    objectDeque.getNumInterested().incrementAndGet();
                    this.poolMap.put(k, objectDeque);
                    this.poolKeyList.add(k);
                } else {
                    objectDeque.getNumInterested().incrementAndGet();
                }
            } else {
                objectDeque.getNumInterested().incrementAndGet();
            }
        }
        finally {
            lock.unlock();
        }
        return objectDeque;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deregister(K k) {
        Lock lock = this.keyLock.readLock();
        try {
            lock.lock();
            ObjectDeque<T> objectDeque = this.poolMap.get(k);
            long numInterested = objectDeque.getNumInterested().decrementAndGet();
            if (numInterested == 0L && objectDeque.getCreateCount().get() == 0) {
                lock.unlock();
                lock = this.keyLock.writeLock();
                lock.lock();
                if (objectDeque.getCreateCount().get() == 0 && objectDeque.getNumInterested().get() == 0L) {
                    this.poolMap.remove(k);
                    this.poolKeyList.remove(k);
                }
            }
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    void ensureMinIdle() throws Exception {
        int minIdlePerKeySave = this.getMinIdlePerKey();
        if (minIdlePerKeySave < 1) {
            return;
        }
        for (K k : this.poolMap.keySet()) {
            this.ensureMinIdle(k);
        }
    }

    private void ensureMinIdle(K key) throws Exception {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        int deficit = this.calculateDeficit(objectDeque);
        for (int i = 0; i < deficit && this.calculateDeficit(objectDeque) > 0; ++i) {
            this.addObject(key);
            if (objectDeque != null) continue;
            objectDeque = this.poolMap.get(key);
        }
    }

    @Override
    public void addObject(K key) throws Exception {
        this.assertOpen();
        this.register(key);
        try {
            PooledObject<T> p = this.create(key);
            this.addIdleObject(key, p);
        }
        finally {
            this.deregister(key);
        }
    }

    private void addIdleObject(K key, PooledObject<T> p) throws Exception {
        if (p != null) {
            this.factory.passivateObject(key, p);
            LinkedBlockingDeque<PooledObject<T>> idleObjects = this.poolMap.get(key).getIdleObjects();
            if (this.getLifo()) {
                idleObjects.addFirst(p);
            } else {
                idleObjects.addLast(p);
            }
        }
    }

    public void preparePool(K key) throws Exception {
        int minIdlePerKeySave = this.getMinIdlePerKey();
        if (minIdlePerKeySave < 1) {
            return;
        }
        this.ensureMinIdle(key);
    }

    private int getNumTests() {
        int totalIdle = this.getNumIdle();
        int numTests = this.getNumTestsPerEvictionRun();
        if (numTests >= 0) {
            return Math.min(numTests, totalIdle);
        }
        return (int)Math.ceil((double)totalIdle / Math.abs((double)numTests));
    }

    private int calculateDeficit(ObjectDeque<T> objectDeque) {
        int growLimit;
        if (objectDeque == null) {
            return this.getMinIdlePerKey();
        }
        int maxTotal = this.getMaxTotal();
        int maxTotalPerKeySave = this.getMaxTotalPerKey();
        int objectDefecit = 0;
        objectDefecit = this.getMinIdlePerKey() - objectDeque.getIdleObjects().size();
        if (maxTotalPerKeySave > 0) {
            growLimit = Math.max(0, maxTotalPerKeySave - objectDeque.getIdleObjects().size());
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        if (maxTotal > 0) {
            growLimit = Math.max(0, maxTotal - this.getNumActive() - this.getNumIdle());
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        return objectDefecit;
    }

    @Override
    public Map<String, Integer> getNumActivePerKey() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            if (entry == null) continue;
            K key = entry.getKey();
            ObjectDeque<T> objectDequeue = entry.getValue();
            if (key == null || objectDequeue == null) continue;
            result.put(key.toString(), objectDequeue.getAllObjects().size() - objectDequeue.getIdleObjects().size());
        }
        return result;
    }

    @Override
    public int getNumWaiters() {
        int result = 0;
        if (this.getBlockWhenExhausted()) {
            Iterator<ObjectDeque<T>> iter = this.poolMap.values().iterator();
            while (iter.hasNext()) {
                result += iter.next().getIdleObjects().getTakeQueueLength();
            }
        }
        return result;
    }

    @Override
    public Map<String, Integer> getNumWaitersByKey() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            K k = entry.getKey();
            ObjectDeque<T> deque = entry.getValue();
            if (deque == null) continue;
            if (this.getBlockWhenExhausted()) {
                result.put(k.toString(), deque.getIdleObjects().getTakeQueueLength());
                continue;
            }
            result.put(k.toString(), 0);
        }
        return result;
    }

    @Override
    public Map<String, List<DefaultPooledObjectInfo>> listAllObjects() {
        HashMap<String, List<DefaultPooledObjectInfo>> result = new HashMap<String, List<DefaultPooledObjectInfo>>();
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            K k = entry.getKey();
            ObjectDeque<T> deque = entry.getValue();
            if (deque == null) continue;
            ArrayList<DefaultPooledObjectInfo> list = new ArrayList<DefaultPooledObjectInfo>();
            result.put(k.toString(), list);
            for (PooledObject<T> p : deque.getAllObjects().values()) {
                list.add(new DefaultPooledObjectInfo(p));
            }
        }
        return result;
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", maxIdlePerKey=");
        builder.append(this.maxIdlePerKey);
        builder.append(", minIdlePerKey=");
        builder.append(this.minIdlePerKey);
        builder.append(", maxTotalPerKey=");
        builder.append(this.maxTotalPerKey);
        builder.append(", factory=");
        builder.append(this.factory);
        builder.append(", fairness=");
        builder.append(this.fairness);
        builder.append(", poolMap=");
        builder.append(this.poolMap);
        builder.append(", poolKeyList=");
        builder.append(this.poolKeyList);
        builder.append(", keyLock=");
        builder.append(this.keyLock);
        builder.append(", numTotal=");
        builder.append(this.numTotal);
        builder.append(", evictionKeyIterator=");
        builder.append(this.evictionKeyIterator);
        builder.append(", evictionKey=");
        builder.append(this.evictionKey);
    }

    private class ObjectDeque<S> {
        private final LinkedBlockingDeque<PooledObject<S>> idleObjects;
        private final AtomicInteger createCount = new AtomicInteger(0);
        private long makeObjectCount = 0L;
        private final Object makeObjectCountLock = new Object();
        private final Map<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>> allObjects = new ConcurrentHashMap<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>>();
        private final AtomicLong numInterested = new AtomicLong(0L);

        public ObjectDeque(boolean fairness) {
            this.idleObjects = new LinkedBlockingDeque(fairness);
        }

        public LinkedBlockingDeque<PooledObject<S>> getIdleObjects() {
            return this.idleObjects;
        }

        public AtomicInteger getCreateCount() {
            return this.createCount;
        }

        public AtomicLong getNumInterested() {
            return this.numInterested;
        }

        public Map<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>> getAllObjects() {
            return this.allObjects;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ObjectDeque [idleObjects=");
            builder.append(this.idleObjects);
            builder.append(", createCount=");
            builder.append(this.createCount);
            builder.append(", allObjects=");
            builder.append(this.allObjects);
            builder.append(", numInterested=");
            builder.append(this.numInterested);
            builder.append("]");
            return builder.toString();
        }
    }
}

