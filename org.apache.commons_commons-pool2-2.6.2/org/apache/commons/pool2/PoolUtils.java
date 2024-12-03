/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

public final class PoolUtils {
    private static final String MSG_FACTOR_NEGATIVE = "factor must be positive.";
    private static final String MSG_MIN_IDLE = "minIdle must be non-negative.";
    private static final String MSG_NULL_KEY = "key must not be null.";
    private static final String MSG_NULL_KEYED_POOL = "keyedPool must not be null.";
    private static final String MSG_NULL_KEYS = "keys must not be null.";
    private static final String MSG_NULL_POOL = "pool must not be null.";

    public static void checkRethrow(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }

    public static <T> TimerTask checkMinIdle(ObjectPool<T> pool, int minIdle, long period) throws IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYED_POOL);
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException(MSG_MIN_IDLE);
        }
        ObjectPoolMinIdleTimerTask<T> task = new ObjectPoolMinIdleTimerTask<T>(pool, minIdle);
        PoolUtils.getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }

    public static <K, V> TimerTask checkMinIdle(KeyedObjectPool<K, V> keyedPool, K key, int minIdle, long period) throws IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYED_POOL);
        }
        if (key == null) {
            throw new IllegalArgumentException(MSG_NULL_KEY);
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException(MSG_MIN_IDLE);
        }
        KeyedObjectPoolMinIdleTimerTask<K, V> task = new KeyedObjectPoolMinIdleTimerTask<K, V>(keyedPool, key, minIdle);
        PoolUtils.getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }

    public static <K, V> Map<K, TimerTask> checkMinIdle(KeyedObjectPool<K, V> keyedPool, Collection<K> keys, int minIdle, long period) throws IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYS);
        }
        HashMap<K, TimerTask> tasks = new HashMap<K, TimerTask>(keys.size());
        for (K key : keys) {
            TimerTask task = PoolUtils.checkMinIdle(keyedPool, key, minIdle, period);
            tasks.put(key, task);
        }
        return tasks;
    }

    public static <T> void prefill(ObjectPool<T> pool, int count) throws Exception, IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException(MSG_NULL_POOL);
        }
        for (int i = 0; i < count; ++i) {
            pool.addObject();
        }
    }

    public static <K, V> void prefill(KeyedObjectPool<K, V> keyedPool, K key, int count) throws Exception, IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYED_POOL);
        }
        if (key == null) {
            throw new IllegalArgumentException(MSG_NULL_KEY);
        }
        for (int i = 0; i < count; ++i) {
            keyedPool.addObject(key);
        }
    }

    public static <K, V> void prefill(KeyedObjectPool<K, V> keyedPool, Collection<K> keys, int count) throws Exception, IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYS);
        }
        Iterator<K> iter = keys.iterator();
        while (iter.hasNext()) {
            PoolUtils.prefill(keyedPool, iter.next(), count);
        }
    }

    public static <T> ObjectPool<T> synchronizedPool(ObjectPool<T> pool) {
        if (pool == null) {
            throw new IllegalArgumentException(MSG_NULL_POOL);
        }
        return new SynchronizedObjectPool<T>(pool);
    }

    public static <K, V> KeyedObjectPool<K, V> synchronizedPool(KeyedObjectPool<K, V> keyedPool) {
        return new SynchronizedKeyedObjectPool<K, V>(keyedPool);
    }

    public static <T> PooledObjectFactory<T> synchronizedPooledFactory(PooledObjectFactory<T> factory) {
        return new SynchronizedPooledObjectFactory<T>(factory);
    }

    public static <K, V> KeyedPooledObjectFactory<K, V> synchronizedKeyedPooledFactory(KeyedPooledObjectFactory<K, V> keyedFactory) {
        return new SynchronizedKeyedPooledObjectFactory<K, V>(keyedFactory);
    }

    public static <T> ObjectPool<T> erodingPool(ObjectPool<T> pool) {
        return PoolUtils.erodingPool(pool, 1.0f);
    }

    public static <T> ObjectPool<T> erodingPool(ObjectPool<T> pool, float factor) {
        if (pool == null) {
            throw new IllegalArgumentException(MSG_NULL_POOL);
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException(MSG_FACTOR_NEGATIVE);
        }
        return new ErodingObjectPool<T>(pool, factor);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(KeyedObjectPool<K, V> keyedPool) {
        return PoolUtils.erodingPool(keyedPool, 1.0f);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(KeyedObjectPool<K, V> keyedPool, float factor) {
        return PoolUtils.erodingPool(keyedPool, factor, false);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(KeyedObjectPool<K, V> keyedPool, float factor, boolean perKey) {
        if (keyedPool == null) {
            throw new IllegalArgumentException(MSG_NULL_KEYED_POOL);
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException(MSG_FACTOR_NEGATIVE);
        }
        if (perKey) {
            return new ErodingPerKeyKeyedObjectPool<K, V>(keyedPool, factor);
        }
        return new ErodingKeyedObjectPool<K, V>(keyedPool, factor);
    }

    private static Timer getMinIdleTimer() {
        return TimerHolder.MIN_IDLE_TIMER;
    }

    private static final class ErodingPerKeyKeyedObjectPool<K, V>
    extends ErodingKeyedObjectPool<K, V> {
        private final float factor;
        private final Map<K, ErodingFactor> factors = Collections.synchronizedMap(new HashMap());

        public ErodingPerKeyKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, float factor) {
            super(keyedPool, null);
            this.factor = factor;
        }

        @Override
        protected ErodingFactor getErodingFactor(K key) {
            ErodingFactor eFactor = this.factors.get(key);
            if (eFactor == null) {
                eFactor = new ErodingFactor(this.factor);
                this.factors.put(key, eFactor);
            }
            return eFactor;
        }

        @Override
        public String toString() {
            return "ErodingPerKeyKeyedObjectPool{factor=" + this.factor + ", keyedPool=" + this.getKeyedPool() + '}';
        }
    }

    private static class ErodingKeyedObjectPool<K, V>
    implements KeyedObjectPool<K, V> {
        private final KeyedObjectPool<K, V> keyedPool;
        private final ErodingFactor erodingFactor;

        public ErodingKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, float factor) {
            this(keyedPool, new ErodingFactor(factor));
        }

        protected ErodingKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, ErodingFactor erodingFactor) {
            if (keyedPool == null) {
                throw new IllegalArgumentException(PoolUtils.MSG_NULL_KEYED_POOL);
            }
            this.keyedPool = keyedPool;
            this.erodingFactor = erodingFactor;
        }

        @Override
        public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
            return this.keyedPool.borrowObject(key);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(K key, V obj) throws Exception {
            boolean discard = false;
            long now = System.currentTimeMillis();
            ErodingFactor factor = this.getErodingFactor(key);
            KeyedObjectPool<K, V> keyedObjectPool = this.keyedPool;
            synchronized (keyedObjectPool) {
                if (factor.getNextShrink() < now) {
                    int numIdle = this.getNumIdle(key);
                    if (numIdle > 0) {
                        discard = true;
                    }
                    factor.update(now, numIdle);
                }
            }
            try {
                if (discard) {
                    this.keyedPool.invalidateObject(key, obj);
                } else {
                    this.keyedPool.returnObject(key, obj);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        protected ErodingFactor getErodingFactor(K key) {
            return this.erodingFactor;
        }

        @Override
        public void invalidateObject(K key, V obj) {
            try {
                this.keyedPool.invalidateObject(key, obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void addObject(K key) throws Exception, IllegalStateException, UnsupportedOperationException {
            this.keyedPool.addObject(key);
        }

        @Override
        public int getNumIdle() {
            return this.keyedPool.getNumIdle();
        }

        @Override
        public int getNumIdle(K key) {
            return this.keyedPool.getNumIdle(key);
        }

        @Override
        public int getNumActive() {
            return this.keyedPool.getNumActive();
        }

        @Override
        public int getNumActive(K key) {
            return this.keyedPool.getNumActive(key);
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.keyedPool.clear();
        }

        @Override
        public void clear(K key) throws Exception, UnsupportedOperationException {
            this.keyedPool.clear(key);
        }

        @Override
        public void close() {
            try {
                this.keyedPool.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        protected KeyedObjectPool<K, V> getKeyedPool() {
            return this.keyedPool;
        }

        public String toString() {
            return "ErodingKeyedObjectPool{factor=" + this.erodingFactor + ", keyedPool=" + this.keyedPool + '}';
        }
    }

    private static class ErodingObjectPool<T>
    implements ObjectPool<T> {
        private final ObjectPool<T> pool;
        private final ErodingFactor factor;

        public ErodingObjectPool(ObjectPool<T> pool, float factor) {
            this.pool = pool;
            this.factor = new ErodingFactor(factor);
        }

        @Override
        public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            return this.pool.borrowObject();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(T obj) {
            boolean discard = false;
            long now = System.currentTimeMillis();
            ObjectPool<T> objectPool = this.pool;
            synchronized (objectPool) {
                if (this.factor.getNextShrink() < now) {
                    int numIdle = this.pool.getNumIdle();
                    if (numIdle > 0) {
                        discard = true;
                    }
                    this.factor.update(now, numIdle);
                }
            }
            try {
                if (discard) {
                    this.pool.invalidateObject(obj);
                } else {
                    this.pool.returnObject(obj);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void invalidateObject(T obj) {
            try {
                this.pool.invalidateObject(obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            this.pool.addObject();
        }

        @Override
        public int getNumIdle() {
            return this.pool.getNumIdle();
        }

        @Override
        public int getNumActive() {
            return this.pool.getNumActive();
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }

        @Override
        public void close() {
            try {
                this.pool.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        public String toString() {
            return "ErodingObjectPool{factor=" + this.factor + ", pool=" + this.pool + '}';
        }
    }

    private static final class ErodingFactor {
        private final float factor;
        private volatile transient long nextShrink;
        private volatile transient int idleHighWaterMark;

        public ErodingFactor(float factor) {
            this.factor = factor;
            this.nextShrink = System.currentTimeMillis() + (long)(900000.0f * factor);
            this.idleHighWaterMark = 1;
        }

        public void update(long now, int numIdle) {
            int idle = Math.max(0, numIdle);
            this.idleHighWaterMark = Math.max(idle, this.idleHighWaterMark);
            float maxInterval = 15.0f;
            float minutes = 15.0f + -14.0f / (float)this.idleHighWaterMark * (float)idle;
            this.nextShrink = now + (long)(minutes * 60000.0f * this.factor);
        }

        public long getNextShrink() {
            return this.nextShrink;
        }

        public String toString() {
            return "ErodingFactor{factor=" + this.factor + ", idleHighWaterMark=" + this.idleHighWaterMark + '}';
        }
    }

    private static final class SynchronizedKeyedPooledObjectFactory<K, V>
    implements KeyedPooledObjectFactory<K, V> {
        private final ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();
        private final KeyedPooledObjectFactory<K, V> keyedFactory;

        SynchronizedKeyedPooledObjectFactory(KeyedPooledObjectFactory<K, V> keyedFactory) throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            this.keyedFactory = keyedFactory;
        }

        @Override
        public PooledObject<V> makeObject(K key) throws Exception {
            this.writeLock.lock();
            try {
                PooledObject<V> pooledObject = this.keyedFactory.makeObject(key);
                return pooledObject;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void destroyObject(K key, PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.destroyObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean validateObject(K key, PooledObject<V> p) {
            this.writeLock.lock();
            try {
                boolean bl = this.keyedFactory.validateObject(key, p);
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void activateObject(K key, PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.activateObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void passivateObject(K key, PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.passivateObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedKeyedPoolableObjectFactory");
            sb.append("{keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class SynchronizedPooledObjectFactory<T>
    implements PooledObjectFactory<T> {
        private final ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();
        private final PooledObjectFactory<T> factory;

        SynchronizedPooledObjectFactory(PooledObjectFactory<T> factory) throws IllegalArgumentException {
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
        }

        @Override
        public PooledObject<T> makeObject() throws Exception {
            this.writeLock.lock();
            try {
                PooledObject<T> pooledObject = this.factory.makeObject();
                return pooledObject;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void destroyObject(PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.destroyObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public boolean validateObject(PooledObject<T> p) {
            this.writeLock.lock();
            try {
                boolean bl = this.factory.validateObject(p);
                return bl;
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void activateObject(PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.activateObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        @Override
        public void passivateObject(PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.passivateObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedPoolableObjectFactory");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class SynchronizedKeyedObjectPool<K, V>
    implements KeyedObjectPool<K, V> {
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final KeyedObjectPool<K, V> keyedPool;

        SynchronizedKeyedObjectPool(KeyedObjectPool<K, V> keyedPool) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException(PoolUtils.MSG_NULL_KEYED_POOL);
            }
            this.keyedPool = keyedPool;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                V v = this.keyedPool.borrowObject(key);
                return v;
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(K key, V obj) {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.returnObject(key, obj);
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void invalidateObject(K key, V obj) {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.invalidateObject(key, obj);
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public void addObject(K key) throws Exception, IllegalStateException, UnsupportedOperationException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.addObject(key);
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumIdle(K key) {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.keyedPool.getNumIdle(key);
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumActive(K key) {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.keyedPool.getNumActive(key);
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        @Override
        public int getNumIdle() {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.keyedPool.getNumIdle();
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        @Override
        public int getNumActive() {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.keyedPool.getNumActive();
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.clear();
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public void clear(K key) throws Exception, UnsupportedOperationException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.clear(key);
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public void close() {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.keyedPool.close();
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedKeyedObjectPool");
            sb.append("{keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class SynchronizedObjectPool<T>
    implements ObjectPool<T> {
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final ObjectPool<T> pool;

        SynchronizedObjectPool(ObjectPool<T> pool) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException(PoolUtils.MSG_NULL_POOL);
            }
            this.pool = pool;
        }

        @Override
        public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                T t = this.pool.borrowObject();
                return t;
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(T obj) {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.pool.returnObject(obj);
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void invalidateObject(T obj) {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.pool.invalidateObject(obj);
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.pool.addObject();
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public int getNumIdle() {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.pool.getNumIdle();
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        @Override
        public int getNumActive() {
            ReentrantReadWriteLock.ReadLock readLock = this.readWriteLock.readLock();
            readLock.lock();
            try {
                int n = this.pool.getNumActive();
                return n;
            }
            finally {
                readLock.unlock();
            }
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.pool.clear();
            }
            finally {
                writeLock.unlock();
            }
        }

        @Override
        public void close() {
            ReentrantReadWriteLock.WriteLock writeLock = this.readWriteLock.writeLock();
            writeLock.lock();
            try {
                this.pool.close();
            }
            catch (Exception exception) {
            }
            finally {
                writeLock.unlock();
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedObjectPool");
            sb.append("{pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class KeyedObjectPoolMinIdleTimerTask<K, V>
    extends TimerTask {
        private final int minIdle;
        private final K key;
        private final KeyedObjectPool<K, V> keyedPool;

        KeyedObjectPoolMinIdleTimerTask(KeyedObjectPool<K, V> keyedPool, K key, int minIdle) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException(PoolUtils.MSG_NULL_KEYED_POOL);
            }
            this.keyedPool = keyedPool;
            this.key = key;
            this.minIdle = minIdle;
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.keyedPool.getNumIdle(this.key) < this.minIdle) {
                    this.keyedPool.addObject(this.key);
                }
                success = true;
            }
            catch (Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("KeyedObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class ObjectPoolMinIdleTimerTask<T>
    extends TimerTask {
        private final int minIdle;
        private final ObjectPool<T> pool;

        ObjectPoolMinIdleTimerTask(ObjectPool<T> pool, int minIdle) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException(PoolUtils.MSG_NULL_POOL);
            }
            this.pool = pool;
            this.minIdle = minIdle;
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.pool.getNumIdle() < this.minIdle) {
                    this.pool.addObject();
                }
                success = true;
            }
            catch (Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    static class TimerHolder {
        static final Timer MIN_IDLE_TIMER = new Timer(true);

        TimerHolder() {
        }
    }
}

