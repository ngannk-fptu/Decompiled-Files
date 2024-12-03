/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.PoolUtils;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;
import org.apache.tomcat.dbcp.pool2.UsageTracking;
import org.apache.tomcat.dbcp.pool2.impl.AbandonedConfig;
import org.apache.tomcat.dbcp.pool2.impl.BaseGenericObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObjectInfo;
import org.apache.tomcat.dbcp.pool2.impl.EvictionConfig;
import org.apache.tomcat.dbcp.pool2.impl.EvictionPolicy;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolMXBean;
import org.apache.tomcat.dbcp.pool2.impl.LinkedBlockingDeque;

public class GenericKeyedObjectPool<K, T>
extends BaseGenericObjectPool<T>
implements KeyedObjectPool<K, T>,
GenericKeyedObjectPoolMXBean<K>,
UsageTracking<T> {
    private static final Integer ZERO = 0;
    private static final String ONAME_BASE = "org.apache.commons.pool2:type=GenericKeyedObjectPool,name=";
    private volatile int maxIdlePerKey = 8;
    private volatile int minIdlePerKey = 0;
    private volatile int maxTotalPerKey = 8;
    private final KeyedPooledObjectFactory<K, T> factory;
    private final boolean fairness;
    private final Map<K, ObjectDeque<T>> poolMap = new ConcurrentHashMap<K, ObjectDeque<T>>();
    private final ArrayList<K> poolKeyList = new ArrayList();
    private final ReadWriteLock keyLock = new ReentrantReadWriteLock(true);
    private final AtomicInteger numTotal = new AtomicInteger(0);
    private Iterator<K> evictionKeyIterator;
    private K evictionKey;

    public GenericKeyedObjectPool(KeyedPooledObjectFactory<K, T> factory) {
        this(factory, new GenericKeyedObjectPoolConfig());
    }

    public GenericKeyedObjectPool(KeyedPooledObjectFactory<K, T> factory, GenericKeyedObjectPoolConfig<T> config) {
        super(config, ONAME_BASE, config.getJmxNamePrefix());
        if (factory == null) {
            this.jmxUnregister();
            throw new IllegalArgumentException("Factory may not be null");
        }
        this.factory = factory;
        this.fairness = config.getFairness();
        this.setConfig(config);
    }

    public GenericKeyedObjectPool(KeyedPooledObjectFactory<K, T> factory, GenericKeyedObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        this(factory, config);
        this.setAbandonedConfig(abandonedConfig);
    }

    private void addIdleObject(K key, PooledObject<T> p) throws Exception {
        if (!PooledObject.isNull(p)) {
            this.factory.passivateObject(key, p);
            LinkedBlockingDeque<PooledObject<T>> idleObjects = this.poolMap.get(key).getIdleObjects();
            if (this.getLifo()) {
                idleObjects.addFirst(p);
            } else {
                idleObjects.addLast(p);
            }
        }
    }

    @Override
    public void addObject(K key) throws Exception {
        this.assertOpen();
        this.register(key);
        try {
            this.addIdleObject(key, this.create(key));
        }
        finally {
            this.deregister(key);
        }
    }

    @Override
    public T borrowObject(K key) throws Exception {
        return this.borrowObject(key, this.getMaxWaitDuration().toMillis());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T borrowObject(K key, long borrowMaxWaitMillis) throws Exception {
        this.assertOpen();
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getRemoveAbandonedOnBorrow() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxTotal() - 3) {
            this.removeAbandoned(ac);
        }
        PooledObject<T> p = null;
        boolean blockWhenExhausted = this.getBlockWhenExhausted();
        Instant waitTime = Instant.now();
        ObjectDeque<T> objectDeque = this.register(key);
        try {
            while (p == null) {
                boolean create;
                block20: {
                    create = false;
                    p = objectDeque.getIdleObjects().pollFirst();
                    if (p == null && !PooledObject.isNull(p = this.create(key))) {
                        create = true;
                    }
                    if (blockWhenExhausted) {
                        if (PooledObject.isNull(p)) {
                            PooledObject<T> pooledObject = p = borrowMaxWaitMillis < 0L ? objectDeque.getIdleObjects().takeFirst() : objectDeque.getIdleObjects().pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
                        }
                        if (PooledObject.isNull(p)) {
                            throw new NoSuchElementException(this.appendStats("Timeout waiting for idle object, borrowMaxWaitMillis=" + borrowMaxWaitMillis));
                        }
                    } else if (PooledObject.isNull(p)) {
                        throw new NoSuchElementException(this.appendStats("Pool exhausted"));
                    }
                    if (!p.allocate()) {
                        p = null;
                    }
                    if (PooledObject.isNull(p)) continue;
                    try {
                        this.factory.activateObject(key, p);
                    }
                    catch (Exception e) {
                        try {
                            this.destroy(key, p, true, DestroyMode.NORMAL);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        p = null;
                        if (!create) break block20;
                        NoSuchElementException nsee = new NoSuchElementException(this.appendStats("Unable to activate object"));
                        nsee.initCause(e);
                        throw nsee;
                    }
                }
                if (PooledObject.isNull(p) || !this.getTestOnBorrow()) continue;
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
                    this.destroy(key, p, true, DestroyMode.NORMAL);
                    this.destroyedByBorrowValidationCount.incrementAndGet();
                }
                catch (Exception t) {
                    // empty catch block
                }
                p = null;
                if (!create) continue;
                NoSuchElementException nsee = new NoSuchElementException(this.appendStats("Unable to validate object"));
                nsee.initCause(validationThrowable);
                throw nsee;
            }
        }
        finally {
            this.deregister(key);
        }
        this.updateStatsBorrow(p, Duration.between(waitTime, Instant.now()));
        return p.getObject();
    }

    private int calculateDeficit(ObjectDeque<T> objectDeque) {
        int growLimit;
        if (objectDeque == null) {
            return this.getMinIdlePerKey();
        }
        int maxTotal = this.getMaxTotal();
        int maxTotalPerKeySave = this.getMaxTotalPerKey();
        int objectDefecit = this.getMinIdlePerKey() - objectDeque.getIdleObjects().size();
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
    public void clear() {
        this.poolMap.keySet().forEach(key -> this.clear(key, false));
    }

    @Override
    public void clear(K key) {
        this.clear(key, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear(K key, boolean reuseCapacity) {
        if (!this.poolMap.containsKey(key)) {
            return;
        }
        ObjectDeque<T> objectDeque = this.register(key);
        int freedCapacity = 0;
        try {
            LinkedBlockingDeque<PooledObject<T>> idleObjects = objectDeque.getIdleObjects();
            PooledObject<T> p = idleObjects.poll();
            while (p != null) {
                try {
                    if (this.destroy(key, p, true, DestroyMode.NORMAL)) {
                        ++freedCapacity;
                    }
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
        if (reuseCapacity) {
            this.reuseCapacity(freedCapacity);
        }
    }

    public void clearOldest() {
        TreeMap map = new TreeMap();
        this.poolMap.forEach((key, value) -> value.getIdleObjects().forEach(p -> map.put(p, key)));
        int itemsToRemove = (int)((double)map.size() * 0.15) + 1;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext() && itemsToRemove > 0) {
            Map.Entry entry = iter.next();
            Object key2 = entry.getValue();
            PooledObject p = (PooledObject)entry.getKey();
            boolean destroyed = true;
            try {
                destroyed = this.destroy(key2, p, false, DestroyMode.NORMAL);
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
            this.stopEvictor();
            this.closed = true;
            this.clear();
            this.jmxUnregister();
            this.poolMap.values().forEach(e -> e.getIdleObjects().interuptTakeWaiters());
            this.clear();
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
            if (PooledObject.isNull(p)) {
                this.numTotal.decrementAndGet();
                objectDeque.getCreateCount().decrementAndGet();
                throw new NullPointerException(String.format("%s.makeObject() = null", this.factory.getClass().getSimpleName()));
            }
            if (this.getTestOnCreate() && !this.factory.validateObject(key, p)) {
                this.numTotal.decrementAndGet();
                objectDeque.getCreateCount().decrementAndGet();
                PooledObject<T> newCreateCount = null;
                return newCreateCount;
            }
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
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getLogAbandoned()) {
            p.setLogAbandoned(true);
            p.setRequireFullStackTrace(ac.getRequireFullStackTrace());
        }
        this.createdCount.incrementAndGet();
        objectDeque.getAllObjects().put(new BaseGenericObjectPool.IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deregister(K k) {
        Lock lock = this.keyLock.readLock();
        try {
            lock.lock();
            ObjectDeque<T> objectDeque = this.poolMap.get(k);
            if (objectDeque == null) {
                throw new IllegalStateException("Attempt to de-register a key for a non-existent pool");
            }
            long numInterested = objectDeque.getNumInterested().decrementAndGet();
            if (numInterested < 0L) {
                throw new IllegalStateException("numInterested count for key " + k + " is less than zero");
            }
            if (numInterested == 0L && objectDeque.getCreateCount().get() == 0) {
                lock.unlock();
                lock = this.keyLock.writeLock();
                lock.lock();
                objectDeque = this.poolMap.get(k);
                if (null != objectDeque && objectDeque.getNumInterested().get() == 0L && objectDeque.getCreateCount().get() == 0) {
                    this.poolMap.remove(k);
                    this.poolKeyList.remove(k);
                }
            }
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean destroy(K key, PooledObject<T> toDestroy, boolean always, DestroyMode destroyMode) throws Exception {
        ObjectDeque<T> objectDeque = this.register(key);
        try {
            boolean isIdle;
            PooledObject<T> pooledObject = toDestroy;
            synchronized (pooledObject) {
                isIdle = toDestroy.getState().equals((Object)PooledObjectState.IDLE);
                if (isIdle || always) {
                    isIdle = objectDeque.getIdleObjects().remove(toDestroy);
                }
            }
            if (isIdle || always) {
                objectDeque.getAllObjects().remove(new BaseGenericObjectPool.IdentityWrapper<T>(toDestroy.getObject()));
                toDestroy.invalidate();
                try {
                    this.factory.destroyObject(key, toDestroy, destroyMode);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void evict() throws Exception {
        AbandonedConfig ac;
        this.assertOpen();
        if (this.getNumIdle() > 0) {
            Object underTest = null;
            EvictionPolicy evictionPolicy = this.getEvictionPolicy();
            Object object = this.evictionLock;
            synchronized (object) {
                EvictionConfig evictionConfig = new EvictionConfig(this.getMinEvictableIdleDuration(), this.getSoftMinEvictableIdleDuration(), this.getMinIdlePerKey());
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
                            this.evictionIterator = new BaseGenericObjectPool.EvictionIterator(idleObjects2);
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
                        this.destroy(this.evictionKey, (PooledObject<T>)underTest, true, DestroyMode.NORMAL);
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
                            this.destroy(this.evictionKey, (PooledObject<T>)underTest, true, DestroyMode.NORMAL);
                            this.destroyedByEvictorCount.incrementAndGet();
                        }
                        if (active) {
                            boolean validate = false;
                            Throwable validationThrowable = null;
                            try {
                                validate = this.factory.validateObject(this.evictionKey, (PooledObject<T>)underTest);
                            }
                            catch (Throwable t) {
                                PoolUtils.checkRethrow(t);
                                validationThrowable = t;
                            }
                            if (!validate) {
                                this.destroy(this.evictionKey, (PooledObject<T>)underTest, true, DestroyMode.NORMAL);
                                this.destroyedByEvictorCount.incrementAndGet();
                                if (validationThrowable != null) {
                                    if (validationThrowable instanceof RuntimeException) {
                                        throw (RuntimeException)validationThrowable;
                                    }
                                    throw (Error)validationThrowable;
                                }
                            } else {
                                try {
                                    this.factory.passivateObject(this.evictionKey, (PooledObject<T>)underTest);
                                }
                                catch (Exception e) {
                                    this.destroy(this.evictionKey, (PooledObject<T>)underTest, true, DestroyMode.NORMAL);
                                    this.destroyedByEvictorCount.incrementAndGet();
                                }
                            }
                        }
                    }
                    underTest.endEvictionTest(idleObjects);
                }
            }
        }
        if ((ac = this.abandonedConfig) != null && ac.getRemoveAbandonedOnMaintenance()) {
            this.removeAbandoned(ac);
        }
    }

    public KeyedPooledObjectFactory<K, T> getFactory() {
        return this.factory;
    }

    @Override
    public List<K> getKeys() {
        return (List)this.poolKeyList.clone();
    }

    @Override
    public int getMaxIdlePerKey() {
        return this.maxIdlePerKey;
    }

    @Override
    public int getMaxTotalPerKey() {
        return this.maxTotalPerKey;
    }

    @Override
    public int getMinIdlePerKey() {
        int maxIdlePerKeySave = this.getMaxIdlePerKey();
        return Math.min(this.minIdlePerKey, maxIdlePerKeySave);
    }

    @Override
    public int getNumActive() {
        return this.numTotal.get() - this.getNumIdle();
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
    public Map<String, Integer> getNumActivePerKey() {
        return this.poolMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> ((ObjectDeque)e.getValue()).getAllObjects().size() - ((ObjectDeque)e.getValue()).getIdleObjects().size(), (t, u) -> u));
    }

    @Override
    public int getNumIdle() {
        return this.poolMap.values().stream().mapToInt(e -> e.getIdleObjects().size()).sum();
    }

    @Override
    public int getNumIdle(K key) {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        return objectDeque != null ? objectDeque.getIdleObjects().size() : 0;
    }

    private int getNumTests() {
        int totalIdle = this.getNumIdle();
        int numTests = this.getNumTestsPerEvictionRun();
        if (numTests >= 0) {
            return Math.min(numTests, totalIdle);
        }
        return (int)Math.ceil((double)totalIdle / Math.abs((double)numTests));
    }

    @Override
    public int getNumWaiters() {
        if (this.getBlockWhenExhausted()) {
            return this.poolMap.values().stream().mapToInt(e -> e.getIdleObjects().getTakeQueueLength()).sum();
        }
        return 0;
    }

    @Override
    public Map<String, Integer> getNumWaitersByKey() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        this.poolMap.forEach((k, deque) -> result.put(k.toString(), this.getBlockWhenExhausted() ? Integer.valueOf(deque.getIdleObjects().getTakeQueueLength()) : ZERO));
        return result;
    }

    @Override
    String getStatsString() {
        return super.getStatsString() + String.format(", fairness=%s, maxIdlePerKey%,d, maxTotalPerKey=%,d, minIdlePerKey=%,d, numTotal=%,d", this.fairness, this.maxIdlePerKey, this.maxTotalPerKey, this.minIdlePerKey, this.numTotal.get());
    }

    private boolean hasBorrowWaiters() {
        return this.getBlockWhenExhausted() && this.poolMap.values().stream().anyMatch(deque -> deque.getIdleObjects().hasTakeWaiters());
    }

    @Override
    public void invalidateObject(K key, T obj) throws Exception {
        this.invalidateObject(key, obj, DestroyMode.NORMAL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateObject(K key, T obj, DestroyMode destroyMode) throws Exception {
        PooledObject<T> p;
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        PooledObject<T> pooledObject = p = objectDeque != null ? objectDeque.getAllObjects().get(new BaseGenericObjectPool.IdentityWrapper<T>(obj)) : null;
        if (p == null) {
            throw new IllegalStateException(this.appendStats("Object not currently part of this pool"));
        }
        PooledObject<T> pooledObject2 = p;
        synchronized (pooledObject2) {
            if (p.getState() != PooledObjectState.INVALID) {
                this.destroy(key, p, true, destroyMode);
                this.reuseCapacity();
            }
        }
    }

    @Override
    public Map<String, List<DefaultPooledObjectInfo>> listAllObjects() {
        return this.poolMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> ((ObjectDeque)e.getValue()).getAllObjects().values().stream().map(DefaultPooledObjectInfo::new).collect(Collectors.toList())));
    }

    public void preparePool(K key) throws Exception {
        int minIdlePerKeySave = this.getMinIdlePerKey();
        if (minIdlePerKeySave < 1) {
            return;
        }
        this.ensureMinIdle(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ObjectDeque<T> register(K k) {
        Lock lock = this.keyLock.readLock();
        ObjectDeque<T> objectDeque = null;
        try {
            lock.lock();
            objectDeque = this.poolMap.get(k);
            if (objectDeque == null) {
                lock.unlock();
                lock = this.keyLock.writeLock();
                lock.lock();
                AtomicBoolean allocated = new AtomicBoolean();
                objectDeque = this.poolMap.computeIfAbsent(k, key -> {
                    allocated.set(true);
                    ObjectDeque deque = new ObjectDeque(this.fairness);
                    deque.getNumInterested().incrementAndGet();
                    this.poolKeyList.add(k);
                    return deque;
                });
                if (!allocated.get()) {
                    objectDeque = this.poolMap.get(k);
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

    private void removeAbandoned(AbandonedConfig abandonedConfig) {
        this.poolMap.forEach((key, value) -> {
            ArrayList remove = this.createRemoveList(abandonedConfig, value.getAllObjects());
            remove.forEach(pooledObject -> {
                if (abandonedConfig.getLogAbandoned()) {
                    pooledObject.printStackTrace(abandonedConfig.getLogWriter());
                }
                try {
                    this.invalidateObject((K)key, (T)pooledObject.getObject(), DestroyMode.ABANDONED);
                }
                catch (Exception e) {
                    this.swallowException(e);
                }
            });
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void returnObject(K key, T obj) {
        ObjectDeque<T> objectDeque = this.poolMap.get(key);
        if (objectDeque == null) {
            throw new IllegalStateException("No keyed pool found under the given key.");
        }
        PooledObject<T> p = objectDeque.getAllObjects().get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
        if (PooledObject.isNull(p)) {
            throw new IllegalStateException("Returned object not currently part of this pool");
        }
        this.markReturningState(p);
        Duration activeTime = p.getActiveDuration();
        try {
            if (this.getTestOnReturn() && !this.factory.validateObject(key, p)) {
                try {
                    this.destroy(key, p, true, DestroyMode.NORMAL);
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
                    this.destroy(key, p, true, DestroyMode.NORMAL);
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
                    this.destroy(key, p, true, DestroyMode.NORMAL);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void reuseCapacity() {
        int maxTotalPerKeySave = this.getMaxTotalPerKey();
        int maxQueueLength = 0;
        LinkedBlockingDeque<PooledObject<T>> mostLoadedPool = null;
        K mostLoadedKey = null;
        for (Map.Entry<K, ObjectDeque<T>> entry : this.poolMap.entrySet()) {
            K k = entry.getKey();
            LinkedBlockingDeque<PooledObject<T>> pool = entry.getValue().getIdleObjects();
            int queueLength = pool.getTakeQueueLength();
            if (this.getNumActive(k) >= maxTotalPerKeySave || queueLength <= maxQueueLength) continue;
            maxQueueLength = queueLength;
            mostLoadedPool = pool;
            mostLoadedKey = k;
        }
        if (mostLoadedPool != null) {
            this.register(mostLoadedKey);
            try {
                this.addIdleObject(mostLoadedKey, this.create(mostLoadedKey));
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            finally {
                this.deregister(mostLoadedKey);
            }
        }
    }

    private void reuseCapacity(int newCapacity) {
        int bound = newCapacity < 1 ? 1 : newCapacity;
        for (int i = 0; i < bound; ++i) {
            this.reuseCapacity();
        }
    }

    @Override
    public void setConfig(GenericKeyedObjectPoolConfig<T> conf) {
        super.setConfig(conf);
        this.setMaxIdlePerKey(conf.getMaxIdlePerKey());
        this.setMaxTotalPerKey(conf.getMaxTotalPerKey());
        this.setMaxTotal(conf.getMaxTotal());
        this.setMinIdlePerKey(conf.getMinIdlePerKey());
    }

    public void setMaxIdlePerKey(int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public void setMinIdlePerKey(int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
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
        builder.append(", abandonedConfig=");
        builder.append(this.abandonedConfig);
    }

    @Override
    public void use(T pooledObject) {
        AbandonedConfig abandonedCfg = this.abandonedConfig;
        if (abandonedCfg != null && abandonedCfg.getUseUsageTracking()) {
            this.poolMap.values().stream().map(pool -> pool.getAllObjects().get(new BaseGenericObjectPool.IdentityWrapper<Object>(pooledObject))).filter(Objects::nonNull).findFirst().ifPresent(PooledObject::use);
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

    private static class ObjectDeque<S> {
        private final LinkedBlockingDeque<PooledObject<S>> idleObjects;
        private final AtomicInteger createCount = new AtomicInteger(0);
        private long makeObjectCount;
        private final Object makeObjectCountLock = new Object();
        private final Map<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>> allObjects = new ConcurrentHashMap<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>>();
        private final AtomicLong numInterested = new AtomicLong();

        ObjectDeque(boolean fairness) {
            this.idleObjects = new LinkedBlockingDeque(fairness);
        }

        public Map<BaseGenericObjectPool.IdentityWrapper<S>, PooledObject<S>> getAllObjects() {
            return this.allObjects;
        }

        public AtomicInteger getCreateCount() {
            return this.createCount;
        }

        public LinkedBlockingDeque<PooledObject<S>> getIdleObjects() {
            return this.idleObjects;
        }

        public AtomicLong getNumInterested() {
            return this.numInterested;
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

