/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimerTask;
import java.util.TreeMap;
import org.apache.commons.pool.BaseKeyedObjectPool;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.impl.CursorableLinkedList;
import org.apache.commons.pool.impl.EvictionTimer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenericKeyedObjectPool<K, V>
extends BaseKeyedObjectPool<K, V>
implements KeyedObjectPool<K, V> {
    public static final byte WHEN_EXHAUSTED_FAIL = 0;
    public static final byte WHEN_EXHAUSTED_BLOCK = 1;
    public static final byte WHEN_EXHAUSTED_GROW = 2;
    public static final int DEFAULT_MAX_IDLE = 8;
    public static final int DEFAULT_MAX_ACTIVE = 8;
    public static final int DEFAULT_MAX_TOTAL = -1;
    public static final byte DEFAULT_WHEN_EXHAUSTED_ACTION = 1;
    public static final long DEFAULT_MAX_WAIT = -1L;
    public static final boolean DEFAULT_TEST_ON_BORROW = false;
    public static final boolean DEFAULT_TEST_ON_RETURN = false;
    public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
    public static final int DEFAULT_MIN_IDLE = 0;
    public static final boolean DEFAULT_LIFO = true;
    private int _maxIdle = 8;
    private volatile int _minIdle = 0;
    private int _maxActive = 8;
    private int _maxTotal = -1;
    private long _maxWait = -1L;
    private byte _whenExhaustedAction = 1;
    private volatile boolean _testOnBorrow = false;
    private volatile boolean _testOnReturn = false;
    private boolean _testWhileIdle = false;
    private long _timeBetweenEvictionRunsMillis = -1L;
    private int _numTestsPerEvictionRun = 3;
    private long _minEvictableIdleTimeMillis = 1800000L;
    private Map<K, ObjectQueue> _poolMap = null;
    private int _totalActive = 0;
    private int _totalIdle = 0;
    private int _totalInternalProcessing = 0;
    private KeyedPoolableObjectFactory<K, V> _factory = null;
    private Evictor _evictor = null;
    private CursorableLinkedList<K> _poolList = null;
    private CursorableLinkedList.Cursor _evictionCursor = null;
    private CursorableLinkedList.Cursor _evictionKeyCursor = null;
    private boolean _lifo = true;
    private LinkedList<Latch<K, V>> _allocationQueue = new LinkedList();

    public GenericKeyedObjectPool() {
        this(null, 8, 1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory) {
        this(factory, 8, 1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, Config config) {
        this(factory, config.maxActive, config.whenExhaustedAction, config.maxWait, config.maxIdle, config.maxTotal, config.minIdle, config.testOnBorrow, config.testOnReturn, config.timeBetweenEvictionRunsMillis, config.numTestsPerEvictionRun, config.minEvictableIdleTimeMillis, config.testWhileIdle, config.lifo);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive) {
        this(factory, maxActive, 1, -1L, 8, false, false, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, false, false, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, boolean testOnBorrow, boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, 8, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, false, false, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, boolean testOnBorrow, boolean testOnReturn) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, testOnBorrow, testOnReturn, -1L, 3, 1800000L, false);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, -1, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int maxTotal, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, 0, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int maxTotal, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle) {
        this(factory, maxActive, whenExhaustedAction, maxWait, maxIdle, maxTotal, minIdle, testOnBorrow, testOnReturn, timeBetweenEvictionRunsMillis, numTestsPerEvictionRun, minEvictableIdleTimeMillis, testWhileIdle, true);
    }

    public GenericKeyedObjectPool(KeyedPoolableObjectFactory<K, V> factory, int maxActive, byte whenExhaustedAction, long maxWait, int maxIdle, int maxTotal, int minIdle, boolean testOnBorrow, boolean testOnReturn, long timeBetweenEvictionRunsMillis, int numTestsPerEvictionRun, long minEvictableIdleTimeMillis, boolean testWhileIdle, boolean lifo) {
        this._factory = factory;
        this._maxActive = maxActive;
        this._lifo = lifo;
        switch (whenExhaustedAction) {
            case 0: 
            case 1: 
            case 2: {
                this._whenExhaustedAction = whenExhaustedAction;
                break;
            }
            default: {
                throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
            }
        }
        this._maxWait = maxWait;
        this._maxIdle = maxIdle;
        this._maxTotal = maxTotal;
        this._minIdle = minIdle;
        this._testOnBorrow = testOnBorrow;
        this._testOnReturn = testOnReturn;
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        this._testWhileIdle = testWhileIdle;
        this._poolMap = new HashMap<K, ObjectQueue>();
        this._poolList = new CursorableLinkedList();
        this.startEvictor(this._timeBetweenEvictionRunsMillis);
    }

    public synchronized int getMaxActive() {
        return this._maxActive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxActive(int maxActive) {
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this._maxActive = maxActive;
        }
        this.allocate();
    }

    public synchronized int getMaxTotal() {
        return this._maxTotal;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxTotal(int maxTotal) {
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this._maxTotal = maxTotal;
        }
        this.allocate();
    }

    public synchronized byte getWhenExhaustedAction() {
        return this._whenExhaustedAction;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            switch (whenExhaustedAction) {
                case 0: 
                case 1: 
                case 2: {
                    this._whenExhaustedAction = whenExhaustedAction;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
                }
            }
        }
        this.allocate();
    }

    public synchronized long getMaxWait() {
        return this._maxWait;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxWait(long maxWait) {
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this._maxWait = maxWait;
        }
        this.allocate();
    }

    public synchronized int getMaxIdle() {
        return this._maxIdle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaxIdle(int maxIdle) {
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this._maxIdle = maxIdle;
        }
        this.allocate();
    }

    public void setMinIdle(int poolSize) {
        this._minIdle = poolSize;
    }

    public int getMinIdle() {
        return this._minIdle;
    }

    public boolean getTestOnBorrow() {
        return this._testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this._testOnBorrow = testOnBorrow;
    }

    public boolean getTestOnReturn() {
        return this._testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this._testOnReturn = testOnReturn;
    }

    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return this._timeBetweenEvictionRunsMillis;
    }

    public synchronized void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this._timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.startEvictor(this._timeBetweenEvictionRunsMillis);
    }

    public synchronized int getNumTestsPerEvictionRun() {
        return this._numTestsPerEvictionRun;
    }

    public synchronized void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this._numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public synchronized long getMinEvictableIdleTimeMillis() {
        return this._minEvictableIdleTimeMillis;
    }

    public synchronized void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this._minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public synchronized boolean getTestWhileIdle() {
        return this._testWhileIdle;
    }

    public synchronized void setTestWhileIdle(boolean testWhileIdle) {
        this._testWhileIdle = testWhileIdle;
    }

    public synchronized void setConfig(Config conf) {
        this.setMaxIdle(conf.maxIdle);
        this.setMaxActive(conf.maxActive);
        this.setMaxTotal(conf.maxTotal);
        this.setMinIdle(conf.minIdle);
        this.setMaxWait(conf.maxWait);
        this.setWhenExhaustedAction(conf.whenExhaustedAction);
        this.setTestOnBorrow(conf.testOnBorrow);
        this.setTestOnReturn(conf.testOnReturn);
        this.setTestWhileIdle(conf.testWhileIdle);
        this.setNumTestsPerEvictionRun(conf.numTestsPerEvictionRun);
        this.setMinEvictableIdleTimeMillis(conf.minEvictableIdleTimeMillis);
        this.setTimeBetweenEvictionRunsMillis(conf.timeBetweenEvictionRunsMillis);
    }

    public synchronized boolean getLifo() {
        return this._lifo;
    }

    public synchronized void setLifo(boolean lifo) {
        this._lifo = lifo;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public V borrowObject(K key) throws Exception {
        long maxWait;
        byte whenExhaustedAction;
        long starttime = System.currentTimeMillis();
        Latch latch = new Latch(key);
        Object object = this;
        synchronized (object) {
            whenExhaustedAction = this._whenExhaustedAction;
            maxWait = this._maxWait;
            this._allocationQueue.add(latch);
        }
        this.allocate();
        block47: while (true) {
            GenericKeyedObjectPool genericKeyedObjectPool;
            object = this;
            synchronized (object) {
                this.assertOpen();
            }
            if (null == latch.getPair() && !latch.mayCreate()) {
                switch (whenExhaustedAction) {
                    case 2: {
                        object = this;
                        synchronized (object) {
                            if (latch.getPair() == null && !latch.mayCreate()) {
                                this._allocationQueue.remove(latch);
                                latch.getPool().incrementInternalProcessingCount();
                            }
                            break;
                        }
                    }
                    case 0: {
                        object = this;
                        synchronized (object) {
                            if (latch.getPair() != null || latch.mayCreate()) {
                                break;
                            }
                            this._allocationQueue.remove(latch);
                            throw new NoSuchElementException("Pool exhausted");
                        }
                    }
                    case 1: {
                        try {
                            object = latch;
                            synchronized (object) {
                                if (latch.getPair() != null || latch.mayCreate()) {
                                    break;
                                }
                                if (maxWait <= 0L) {
                                    latch.wait();
                                } else {
                                    long l = System.currentTimeMillis() - starttime;
                                    long waitTime = maxWait - l;
                                    if (waitTime > 0L) {
                                        latch.wait(waitTime);
                                    }
                                }
                            }
                            if (this.isClosed()) {
                                throw new IllegalStateException("Pool closed");
                            }
                        }
                        catch (InterruptedException e) {
                            boolean bl;
                            boolean bl2 = false;
                            genericKeyedObjectPool = this;
                            synchronized (genericKeyedObjectPool) {
                                if (latch.getPair() == null && !latch.mayCreate()) {
                                    this._allocationQueue.remove(latch);
                                } else if (latch.getPair() == null && latch.mayCreate()) {
                                    latch.getPool().decrementInternalProcessingCount();
                                    bl = true;
                                } else {
                                    latch.getPool().decrementInternalProcessingCount();
                                    latch.getPool().incrementActiveCount();
                                    this.returnObject(latch.getkey(), latch.getPair().getValue());
                                }
                            }
                            if (bl) {
                                this.allocate();
                            }
                            Thread.currentThread().interrupt();
                            throw e;
                        }
                        if (maxWait <= 0L || System.currentTimeMillis() - starttime < maxWait) continue block47;
                        GenericKeyedObjectPool e = this;
                        synchronized (e) {
                            if (latch.getPair() != null || latch.mayCreate()) {
                                break;
                            }
                            this._allocationQueue.remove(latch);
                            throw new NoSuchElementException("Timeout waiting for idle object");
                        }
                    }
                    default: {
                        throw new IllegalArgumentException("whenExhaustedAction " + whenExhaustedAction + " not recognized.");
                    }
                }
            }
            boolean newlyCreated = false;
            if (null == latch.getPair()) {
                try {
                    V v = this._factory.makeObject(key);
                    latch.setPair(new ObjectTimestampPair<V>(v));
                    newlyCreated = true;
                }
                finally {
                    if (!newlyCreated) {
                        GenericKeyedObjectPool genericKeyedObjectPool2 = this;
                        synchronized (genericKeyedObjectPool2) {
                            latch.getPool().decrementInternalProcessingCount();
                        }
                        this.allocate();
                    }
                }
            }
            try {
                this._factory.activateObject(key, latch.getPair().value);
                if (this._testOnBorrow && !this._factory.validateObject(key, latch.getPair().value)) {
                    throw new Exception("ValidateObject failed");
                }
                GenericKeyedObjectPool genericKeyedObjectPool3 = this;
                synchronized (genericKeyedObjectPool3) {
                    latch.getPool().decrementInternalProcessingCount();
                    latch.getPool().incrementActiveCount();
                    return (V)latch.getPair().value;
                }
            }
            catch (Throwable throwable) {
                PoolUtils.checkRethrow(throwable);
                try {
                    this._factory.destroyObject(key, latch.getPair().value);
                }
                catch (Throwable e2) {
                    PoolUtils.checkRethrow(e2);
                }
                genericKeyedObjectPool = this;
                synchronized (genericKeyedObjectPool) {
                    latch.getPool().decrementInternalProcessingCount();
                    if (!newlyCreated) {
                        latch.reset();
                        this._allocationQueue.add(0, latch);
                    }
                }
                this.allocate();
                if (newlyCreated) throw new NoSuchElementException("Could not create a validated object, cause: " + throwable.getMessage());
                continue;
            }
            break;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void allocate() {
        boolean clearOldest = false;
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            if (this.isClosed()) {
                return;
            }
            Iterator allocationQueueIter = this._allocationQueue.iterator();
            while (allocationQueueIter.hasNext()) {
                Latch latch;
                Latch latch2 = (Latch)allocationQueueIter.next();
                ObjectQueue pool = this._poolMap.get(latch2.getkey());
                if (null == pool) {
                    pool = new ObjectQueue();
                    this._poolMap.put(latch2.getkey(), pool);
                    this._poolList.add(latch2.getkey());
                }
                latch2.setPool(pool);
                if (!pool.queue.isEmpty()) {
                    allocationQueueIter.remove();
                    latch2.setPair((ObjectTimestampPair)pool.queue.removeFirst());
                    pool.incrementInternalProcessingCount();
                    --this._totalIdle;
                    latch = latch2;
                    synchronized (latch) {
                        latch2.notify();
                        continue;
                    }
                }
                if (this._maxTotal > 0 && this._totalActive + this._totalIdle + this._totalInternalProcessing >= this._maxTotal) {
                    clearOldest = true;
                    break;
                }
                if (!(this._maxActive >= 0 && pool.activeCount + pool.internalProcessingCount >= this._maxActive || this._maxTotal >= 0 && this._totalActive + this._totalIdle + this._totalInternalProcessing >= this._maxTotal)) {
                    allocationQueueIter.remove();
                    latch2.setMayCreate(true);
                    pool.incrementInternalProcessingCount();
                    latch = latch2;
                    synchronized (latch) {
                        latch2.notify();
                        continue;
                    }
                }
                if (this._maxActive >= 0) continue;
                break;
            }
        }
        if (clearOldest) {
            this.clearOldest();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        HashMap toDestroy = new HashMap();
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            Iterator<K> it = this._poolMap.keySet().iterator();
            while (it.hasNext()) {
                K key = it.next();
                ObjectQueue pool = this._poolMap.get(key);
                ArrayList objects = new ArrayList();
                objects.addAll(pool.queue);
                toDestroy.put(key, objects);
                it.remove();
                this._poolList.remove(key);
                this._totalIdle -= pool.queue.size();
                this._totalInternalProcessing += pool.queue.size();
                pool.queue.clear();
            }
        }
        this.destroy(toDestroy, this._factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearOldest() {
        HashMap toDestroy = new HashMap();
        TreeMap map = new TreeMap();
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            for (K key : this._poolMap.keySet()) {
                CursorableLinkedList list = this._poolMap.get(key).queue;
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    map.put(it.next(), key);
                }
            }
            Set setPairKeys = map.entrySet();
            Iterator iter = setPairKeys.iterator();
            for (int itemsToRemove = (int)((double)map.size() * 0.15) + 1; iter.hasNext() && itemsToRemove > 0; --itemsToRemove) {
                Map.Entry entry = iter.next();
                Object key = entry.getValue();
                ObjectTimestampPair pairTimeStamp = (ObjectTimestampPair)entry.getKey();
                ObjectQueue objectQueue = this._poolMap.get(key);
                CursorableLinkedList list = objectQueue.queue;
                list.remove(pairTimeStamp);
                if (toDestroy.containsKey(key)) {
                    ((List)toDestroy.get(key)).add(pairTimeStamp);
                } else {
                    ArrayList<ObjectTimestampPair> listForKey = new ArrayList<ObjectTimestampPair>();
                    listForKey.add(pairTimeStamp);
                    toDestroy.put(key, listForKey);
                }
                objectQueue.incrementInternalProcessingCount();
                --this._totalIdle;
            }
        }
        this.destroy(toDestroy, this._factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear(K key) {
        HashMap toDestroy = new HashMap();
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            ObjectQueue pool = this._poolMap.remove(key);
            if (pool == null) {
                return;
            }
            this._poolList.remove(key);
            ArrayList objects = new ArrayList();
            objects.addAll(pool.queue);
            toDestroy.put(key, objects);
            this._totalIdle -= pool.queue.size();
            this._totalInternalProcessing += pool.queue.size();
            pool.queue.clear();
        }
        this.destroy(toDestroy, this._factory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void destroy(Map<K, List<ObjectTimestampPair<V>>> m, KeyedPoolableObjectFactory<K, V> factory) {
        Iterator<Map.Entry<K, List<ObjectTimestampPair<V>>>> entries = m.entrySet().iterator();
        block8: while (entries.hasNext()) {
            Map.Entry<K, List<ObjectTimestampPair<V>>> entry = entries.next();
            K key = entry.getKey();
            List<ObjectTimestampPair<V>> c = entry.getValue();
            Iterator<ObjectTimestampPair<V>> it = c.iterator();
            while (true) {
                ObjectQueue objectQueue;
                GenericKeyedObjectPool genericKeyedObjectPool;
                Object var10_9;
                if (!it.hasNext()) continue block8;
                try {
                    try {
                        factory.destroyObject(key, it.next().value);
                    }
                    catch (Exception e) {
                        var10_9 = null;
                        genericKeyedObjectPool = this;
                        synchronized (genericKeyedObjectPool) {
                            objectQueue = this._poolMap.get(key);
                            if (objectQueue != null) {
                                objectQueue.decrementInternalProcessingCount();
                                if (objectQueue.internalProcessingCount == 0 && objectQueue.activeCount == 0 && objectQueue.queue.isEmpty()) {
                                    this._poolMap.remove(key);
                                    this._poolList.remove(key);
                                }
                            } else {
                                --this._totalInternalProcessing;
                            }
                        }
                        this.allocate();
                        continue;
                    }
                    var10_9 = null;
                    genericKeyedObjectPool = this;
                }
                catch (Throwable throwable) {
                    var10_9 = null;
                    genericKeyedObjectPool = this;
                    synchronized (genericKeyedObjectPool) {
                        objectQueue = this._poolMap.get(key);
                        if (objectQueue != null) {
                            objectQueue.decrementInternalProcessingCount();
                            if (objectQueue.internalProcessingCount == 0 && objectQueue.activeCount == 0 && objectQueue.queue.isEmpty()) {
                                this._poolMap.remove(key);
                                this._poolList.remove(key);
                            }
                        } else {
                            --this._totalInternalProcessing;
                        }
                    }
                    this.allocate();
                    throw throwable;
                }
                synchronized (genericKeyedObjectPool) {
                    objectQueue = this._poolMap.get(key);
                    if (objectQueue != null) {
                        objectQueue.decrementInternalProcessingCount();
                        if (objectQueue.internalProcessingCount == 0 && objectQueue.activeCount == 0 && objectQueue.queue.isEmpty()) {
                            this._poolMap.remove(key);
                            this._poolList.remove(key);
                        }
                    } else {
                        --this._totalInternalProcessing;
                    }
                }
                this.allocate();
            }
            break;
        }
        return;
    }

    @Override
    public synchronized int getNumActive() {
        return this._totalActive;
    }

    @Override
    public synchronized int getNumIdle() {
        return this._totalIdle;
    }

    @Override
    public synchronized int getNumActive(Object key) {
        ObjectQueue pool = this._poolMap.get(key);
        return pool != null ? pool.activeCount : 0;
    }

    @Override
    public synchronized int getNumIdle(Object key) {
        ObjectQueue pool = this._poolMap.get(key);
        return pool != null ? pool.queue.size() : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void returnObject(K key, V obj) throws Exception {
        block8: {
            try {
                this.addObjectToPool(key, obj, true);
            }
            catch (Exception e) {
                if (this._factory == null) break block8;
                try {
                    this._factory.destroyObject(key, obj);
                }
                catch (Exception e2) {
                    // empty catch block
                }
                ObjectQueue pool = this._poolMap.get(key);
                if (pool == null) break block8;
                GenericKeyedObjectPool genericKeyedObjectPool = this;
                synchronized (genericKeyedObjectPool) {
                    pool.decrementActiveCount();
                    if (pool.queue.isEmpty() && pool.activeCount == 0 && pool.internalProcessingCount == 0) {
                        this._poolMap.remove(key);
                        this._poolList.remove(key);
                    }
                }
                this.allocate();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addObjectToPool(K key, V obj, boolean decrementNumActive) throws Exception {
        ObjectQueue pool;
        boolean success = true;
        if (this._testOnReturn && !this._factory.validateObject(key, obj)) {
            success = false;
        } else {
            this._factory.passivateObject(key, obj);
        }
        boolean shouldDestroy = !success;
        boolean doAllocate = false;
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            pool = this._poolMap.get(key);
            if (null == pool) {
                pool = new ObjectQueue();
                this._poolMap.put(key, pool);
                this._poolList.add(key);
            }
            if (this.isClosed()) {
                shouldDestroy = true;
            } else if (this._maxIdle >= 0 && pool.queue.size() >= this._maxIdle) {
                shouldDestroy = true;
            } else if (success) {
                if (this._lifo) {
                    pool.queue.addFirst(new ObjectTimestampPair<V>(obj));
                } else {
                    pool.queue.addLast(new ObjectTimestampPair<V>(obj));
                }
                ++this._totalIdle;
                if (decrementNumActive) {
                    pool.decrementActiveCount();
                }
                doAllocate = true;
            }
        }
        if (doAllocate) {
            this.allocate();
        }
        if (shouldDestroy) {
            try {
                this._factory.destroyObject(key, obj);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (decrementNumActive) {
                genericKeyedObjectPool = this;
                synchronized (genericKeyedObjectPool) {
                    pool.decrementActiveCount();
                    if (pool.queue.isEmpty() && pool.activeCount == 0 && pool.internalProcessingCount == 0) {
                        this._poolMap.remove(key);
                        this._poolList.remove(key);
                    }
                }
                this.allocate();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateObject(K key, V obj) throws Exception {
        GenericKeyedObjectPool genericKeyedObjectPool;
        try {
            this._factory.destroyObject(key, obj);
            Object var4_3 = null;
            genericKeyedObjectPool = this;
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            GenericKeyedObjectPool genericKeyedObjectPool2 = this;
            synchronized (genericKeyedObjectPool2) {
                ObjectQueue pool = this._poolMap.get(key);
                if (null == pool) {
                    pool = new ObjectQueue();
                    this._poolMap.put(key, pool);
                    this._poolList.add(key);
                }
                pool.decrementActiveCount();
            }
            this.allocate();
            throw throwable;
        }
        synchronized (genericKeyedObjectPool) {
            ObjectQueue pool = this._poolMap.get(key);
            if (null == pool) {
                pool = new ObjectQueue();
                this._poolMap.put(key, pool);
                this._poolList.add(key);
            }
            pool.decrementActiveCount();
        }
        this.allocate();
    }

    @Override
    public void addObject(K key) throws Exception {
        this.assertOpen();
        if (this._factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        V obj = this._factory.makeObject(key);
        try {
            this.assertOpen();
            this.addObjectToPool(key, obj, false);
        }
        catch (IllegalStateException ex) {
            try {
                this._factory.destroyObject(key, obj);
            }
            catch (Exception ex2) {
                // empty catch block
            }
            throw ex;
        }
    }

    public synchronized void preparePool(K key, boolean populateImmediately) {
        ObjectQueue pool = this._poolMap.get(key);
        if (null == pool) {
            pool = new ObjectQueue();
            this._poolMap.put(key, pool);
            this._poolList.add(key);
        }
        if (populateImmediately) {
            try {
                this.ensureMinIdle(key);
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws Exception {
        super.close();
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this.clear();
            if (null != this._evictionCursor) {
                this._evictionCursor.close();
                this._evictionCursor = null;
            }
            if (null != this._evictionKeyCursor) {
                this._evictionKeyCursor.close();
                this._evictionKeyCursor = null;
            }
            this.startEvictor(-1L);
            while (this._allocationQueue.size() > 0) {
                Latch<K, V> l;
                Latch<K, V> latch = l = this._allocationQueue.removeFirst();
                synchronized (latch) {
                    l.notify();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException {
        HashMap toDestroy = new HashMap();
        KeyedPoolableObjectFactory<K, V> oldFactory = this._factory;
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            this.assertOpen();
            if (0 < this.getNumActive()) {
                throw new IllegalStateException("Objects are already active");
            }
            Iterator<K> it = this._poolMap.keySet().iterator();
            while (it.hasNext()) {
                K key = it.next();
                ObjectQueue pool = this._poolMap.get(key);
                if (pool == null) continue;
                ArrayList objects = new ArrayList();
                objects.addAll(pool.queue);
                toDestroy.put(key, objects);
                it.remove();
                this._poolList.remove(key);
                this._totalIdle -= pool.queue.size();
                this._totalInternalProcessing += pool.queue.size();
                pool.queue.clear();
            }
            this._factory = factory;
        }
        this.destroy(toDestroy, oldFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void evict() throws Exception {
        long minEvictableIdleTimeMillis;
        boolean testWhileIdle;
        Object key = null;
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            testWhileIdle = this._testWhileIdle;
            minEvictableIdleTimeMillis = this._minEvictableIdleTimeMillis;
            if (this._evictionKeyCursor != null && this._evictionKeyCursor._lastReturned != null) {
                key = this._evictionKeyCursor._lastReturned.value();
            }
        }
        int m = this.getNumTests();
        for (int i = 0; i < m; ++i) {
            ObjectTimestampPair pair;
            GenericKeyedObjectPool genericKeyedObjectPool2 = this;
            synchronized (genericKeyedObjectPool2) {
                if (this._poolMap == null || this._poolMap.size() == 0) {
                    continue;
                }
                if (null == this._evictionKeyCursor) {
                    this.resetEvictionKeyCursor();
                    key = null;
                }
                if (null == this._evictionCursor) {
                    if (this._evictionKeyCursor.hasNext()) {
                        key = this._evictionKeyCursor.next();
                        this.resetEvictionObjectCursor(key);
                    } else {
                        this.resetEvictionKeyCursor();
                        if (this._evictionKeyCursor != null && this._evictionKeyCursor.hasNext()) {
                            key = this._evictionKeyCursor.next();
                            this.resetEvictionObjectCursor(key);
                        }
                    }
                }
                if (this._evictionCursor == null) {
                    continue;
                }
                if ((this._lifo && !this._evictionCursor.hasPrevious() || !this._lifo && !this._evictionCursor.hasNext()) && this._evictionKeyCursor != null) {
                    if (this._evictionKeyCursor.hasNext()) {
                        key = this._evictionKeyCursor.next();
                        this.resetEvictionObjectCursor(key);
                    } else {
                        this.resetEvictionKeyCursor();
                        if (this._evictionKeyCursor != null && this._evictionKeyCursor.hasNext()) {
                            key = this._evictionKeyCursor.next();
                            this.resetEvictionObjectCursor(key);
                        }
                    }
                }
                if (this._lifo && !this._evictionCursor.hasPrevious() || !this._lifo && !this._evictionCursor.hasNext()) {
                    continue;
                }
                pair = this._lifo ? (ObjectTimestampPair)this._evictionCursor.previous() : (ObjectTimestampPair)this._evictionCursor.next();
                this._evictionCursor.remove();
                ObjectQueue objectQueue = this._poolMap.get(key);
                objectQueue.incrementInternalProcessingCount();
                --this._totalIdle;
            }
            boolean removeObject = false;
            if (minEvictableIdleTimeMillis > 0L && System.currentTimeMillis() - pair.tstamp > minEvictableIdleTimeMillis) {
                removeObject = true;
            }
            if (testWhileIdle && !removeObject) {
                boolean active = false;
                try {
                    this._factory.activateObject(key, pair.value);
                    active = true;
                }
                catch (Exception e) {
                    removeObject = true;
                }
                if (active) {
                    if (!this._factory.validateObject(key, pair.value)) {
                        removeObject = true;
                    } else {
                        try {
                            this._factory.passivateObject(key, pair.value);
                        }
                        catch (Exception e) {
                            removeObject = true;
                        }
                    }
                }
            }
            if (removeObject) {
                try {
                    this._factory.destroyObject(key, pair.value);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
            GenericKeyedObjectPool genericKeyedObjectPool3 = this;
            synchronized (genericKeyedObjectPool3) {
                ObjectQueue objectQueue = this._poolMap.get(key);
                objectQueue.decrementInternalProcessingCount();
                if (removeObject) {
                    if (objectQueue.queue.isEmpty() && objectQueue.activeCount == 0 && objectQueue.internalProcessingCount == 0) {
                        this._poolMap.remove(key);
                        this._poolList.remove(key);
                    }
                } else {
                    this._evictionCursor.add(pair);
                    ++this._totalIdle;
                    if (this._lifo) {
                        this._evictionCursor.previous();
                    }
                }
                continue;
            }
        }
        this.allocate();
    }

    private void resetEvictionKeyCursor() {
        if (this._evictionKeyCursor != null) {
            this._evictionKeyCursor.close();
        }
        this._evictionKeyCursor = this._poolList.cursor();
        if (null != this._evictionCursor) {
            this._evictionCursor.close();
            this._evictionCursor = null;
        }
    }

    private void resetEvictionObjectCursor(Object key) {
        if (this._evictionCursor != null) {
            this._evictionCursor.close();
        }
        if (this._poolMap == null) {
            return;
        }
        ObjectQueue pool = this._poolMap.get(key);
        if (pool != null) {
            CursorableLinkedList queue = pool.queue;
            this._evictionCursor = queue.cursor(this._lifo ? queue.size() : 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureMinIdle() throws Exception {
        if (this._minIdle > 0) {
            Object[] keysCopy;
            GenericKeyedObjectPool genericKeyedObjectPool = this;
            synchronized (genericKeyedObjectPool) {
                keysCopy = this._poolMap.keySet().toArray();
            }
            for (int i = 0; i < keysCopy.length; ++i) {
                this.ensureMinIdle(keysCopy[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureMinIdle(K key) throws Exception {
        ObjectQueue pool;
        GenericKeyedObjectPool genericKeyedObjectPool = this;
        synchronized (genericKeyedObjectPool) {
            pool = this._poolMap.get(key);
        }
        if (pool == null) {
            return;
        }
        int objectDeficit = this.calculateDeficit(pool, false);
        for (int i = 0; i < objectDeficit && this.calculateDeficit(pool, true) > 0; ++i) {
            GenericKeyedObjectPool genericKeyedObjectPool2;
            Object var6_7;
            try {
                this.addObject(key);
                var6_7 = null;
                genericKeyedObjectPool2 = this;
            }
            catch (Throwable throwable) {
                var6_7 = null;
                genericKeyedObjectPool2 = this;
                synchronized (genericKeyedObjectPool2) {
                    pool.decrementInternalProcessingCount();
                }
                this.allocate();
                throw throwable;
            }
            synchronized (genericKeyedObjectPool2) {
                pool.decrementInternalProcessingCount();
            }
            this.allocate();
        }
    }

    protected synchronized void startEvictor(long delay) {
        if (null != this._evictor) {
            EvictionTimer.cancel(this._evictor);
            this._evictor = null;
        }
        if (delay > 0L) {
            this._evictor = new Evictor();
            EvictionTimer.schedule(this._evictor, delay, delay);
        }
    }

    synchronized String debugInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("Active: ").append(this.getNumActive()).append("\n");
        buf.append("Idle: ").append(this.getNumIdle()).append("\n");
        for (K key : this._poolMap.keySet()) {
            buf.append("\t").append(key).append(" ").append(this._poolMap.get(key)).append("\n");
        }
        return buf.toString();
    }

    private synchronized int getNumTests() {
        if (this._numTestsPerEvictionRun >= 0) {
            return Math.min(this._numTestsPerEvictionRun, this._totalIdle);
        }
        return (int)Math.ceil((double)this._totalIdle / Math.abs((double)this._numTestsPerEvictionRun));
    }

    private synchronized int calculateDeficit(ObjectQueue pool, boolean incrementInternal) {
        int growLimit;
        int objectDefecit = 0;
        objectDefecit = this.getMinIdle() - pool.queue.size();
        if (this.getMaxActive() > 0) {
            growLimit = Math.max(0, this.getMaxActive() - pool.activeCount - pool.queue.size() - pool.internalProcessingCount);
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        if (this.getMaxTotal() > 0) {
            growLimit = Math.max(0, this.getMaxTotal() - this.getNumActive() - this.getNumIdle() - this._totalInternalProcessing);
            objectDefecit = Math.min(objectDefecit, growLimit);
        }
        if (incrementInternal && objectDefecit > 0) {
            pool.incrementInternalProcessingCount();
        }
        return objectDefecit;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class Latch<LK, LV> {
        private final LK _key;
        private ObjectQueue _pool;
        private ObjectTimestampPair<LV> _pair;
        private boolean _mayCreate = false;

        private Latch(LK key) {
            this._key = key;
        }

        private synchronized LK getkey() {
            return this._key;
        }

        private synchronized ObjectQueue getPool() {
            return this._pool;
        }

        private synchronized void setPool(ObjectQueue pool) {
            this._pool = pool;
        }

        private synchronized ObjectTimestampPair<LV> getPair() {
            return this._pair;
        }

        private synchronized void setPair(ObjectTimestampPair<LV> pair) {
            this._pair = pair;
        }

        private synchronized boolean mayCreate() {
            return this._mayCreate;
        }

        private synchronized void setMayCreate(boolean mayCreate) {
            this._mayCreate = mayCreate;
        }

        private synchronized void reset() {
            this._pair = null;
            this._mayCreate = false;
        }
    }

    public static class Config {
        public int maxIdle = 8;
        public int maxActive = 8;
        public int maxTotal = -1;
        public int minIdle = 0;
        public long maxWait = -1L;
        public byte whenExhaustedAction = 1;
        public boolean testOnBorrow = false;
        public boolean testOnReturn = false;
        public boolean testWhileIdle = false;
        public long timeBetweenEvictionRunsMillis = -1L;
        public int numTestsPerEvictionRun = 3;
        public long minEvictableIdleTimeMillis = 1800000L;
        public boolean lifo = true;
    }

    private class Evictor
    extends TimerTask {
        private Evictor() {
        }

        public void run() {
            try {
                GenericKeyedObjectPool.this.evict();
            }
            catch (Exception e) {
            }
            catch (OutOfMemoryError oome) {
                oome.printStackTrace(System.err);
            }
            try {
                GenericKeyedObjectPool.this.ensureMinIdle();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ObjectTimestampPair<T>
    implements Comparable<T> {
        @Deprecated
        T value;
        @Deprecated
        long tstamp;

        ObjectTimestampPair(T val) {
            this(val, System.currentTimeMillis());
        }

        ObjectTimestampPair(T val, long time) {
            this.value = val;
            this.tstamp = time;
        }

        public String toString() {
            return this.value + ";" + this.tstamp;
        }

        @Override
        public int compareTo(Object obj) {
            return this.compareTo((ObjectTimestampPair)obj);
        }

        @Override
        public int compareTo(ObjectTimestampPair<T> other) {
            long tstampdiff = this.tstamp - other.tstamp;
            if (tstampdiff == 0L) {
                return System.identityHashCode(this) - System.identityHashCode(other);
            }
            return (int)Math.min(Math.max(tstampdiff, Integer.MIN_VALUE), Integer.MAX_VALUE);
        }

        public T getValue() {
            return this.value;
        }

        public long getTstamp() {
            return this.tstamp;
        }
    }

    private class ObjectQueue {
        private int activeCount = 0;
        private final CursorableLinkedList<ObjectTimestampPair<V>> queue = new CursorableLinkedList();
        private int internalProcessingCount = 0;

        private ObjectQueue() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void incrementActiveCount() {
            GenericKeyedObjectPool genericKeyedObjectPool = GenericKeyedObjectPool.this;
            synchronized (genericKeyedObjectPool) {
                GenericKeyedObjectPool.this._totalActive++;
            }
            ++this.activeCount;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void decrementActiveCount() {
            GenericKeyedObjectPool genericKeyedObjectPool = GenericKeyedObjectPool.this;
            synchronized (genericKeyedObjectPool) {
                GenericKeyedObjectPool.this._totalActive--;
            }
            if (this.activeCount > 0) {
                --this.activeCount;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void incrementInternalProcessingCount() {
            GenericKeyedObjectPool genericKeyedObjectPool = GenericKeyedObjectPool.this;
            synchronized (genericKeyedObjectPool) {
                GenericKeyedObjectPool.this._totalInternalProcessing++;
            }
            ++this.internalProcessingCount;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void decrementInternalProcessingCount() {
            GenericKeyedObjectPool genericKeyedObjectPool = GenericKeyedObjectPool.this;
            synchronized (genericKeyedObjectPool) {
                GenericKeyedObjectPool.this._totalInternalProcessing--;
            }
            --this.internalProcessingCount;
        }
    }
}

