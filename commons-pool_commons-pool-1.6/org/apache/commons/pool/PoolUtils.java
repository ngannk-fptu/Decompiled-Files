/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PoolUtils {
    private static Timer MIN_IDLE_TIMER;

    public static void checkRethrow(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }

    public static <V> PoolableObjectFactory<V> adapt(KeyedPoolableObjectFactory<Object, V> keyedFactory) throws IllegalArgumentException {
        return PoolUtils.adapt(keyedFactory, new Object());
    }

    public static <K, V> PoolableObjectFactory<V> adapt(KeyedPoolableObjectFactory<K, V> keyedFactory, K key) throws IllegalArgumentException {
        return new PoolableObjectFactoryAdaptor<K, V>(keyedFactory, key);
    }

    public static <K, V> KeyedPoolableObjectFactory<K, V> adapt(PoolableObjectFactory<V> factory) throws IllegalArgumentException {
        return new KeyedPoolableObjectFactoryAdaptor(factory);
    }

    public static <V> ObjectPool<V> adapt(KeyedObjectPool<Object, V> keyedPool) throws IllegalArgumentException {
        return PoolUtils.adapt(keyedPool, new Object());
    }

    public static <V> ObjectPool<V> adapt(KeyedObjectPool<Object, V> keyedPool, Object key) throws IllegalArgumentException {
        return new ObjectPoolAdaptor<V>(keyedPool, key);
    }

    public static <K, V> KeyedObjectPool<K, V> adapt(ObjectPool<V> pool) throws IllegalArgumentException {
        return new KeyedObjectPoolAdaptor(pool);
    }

    public static <T> ObjectPool<T> checkedPool(ObjectPool<T> pool, Class<T> type) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null.");
        }
        return new CheckedObjectPool<T>(pool, type);
    }

    public static <K, V> KeyedObjectPool<K, V> checkedPool(KeyedObjectPool<K, V> keyedPool, Class<V> type) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null.");
        }
        return new CheckedKeyedObjectPool<K, V>(keyedPool, type);
    }

    public static <T> TimerTask checkMinIdle(ObjectPool<T> pool, int minIdle, long period) throws IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        ObjectPoolMinIdleTimerTask<T> task = new ObjectPoolMinIdleTimerTask<T>(pool, minIdle);
        PoolUtils.getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }

    public static <K, V> TimerTask checkMinIdle(KeyedObjectPool<K, V> keyedPool, K key, int minIdle, long period) throws IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        KeyedObjectPoolMinIdleTimerTask<K, V> task = new KeyedObjectPoolMinIdleTimerTask<K, V>(keyedPool, key, minIdle);
        PoolUtils.getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }

    public static <K, V> Map<K, TimerTask> checkMinIdle(KeyedObjectPool<K, V> keyedPool, Collection<? extends K> keys, int minIdle, long period) throws IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
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
            throw new IllegalArgumentException("pool must not be null.");
        }
        for (int i = 0; i < count; ++i) {
            pool.addObject();
        }
    }

    public static <K, V> void prefill(KeyedObjectPool<K, V> keyedPool, K key, int count) throws Exception, IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        for (int i = 0; i < count; ++i) {
            keyedPool.addObject(key);
        }
    }

    public static <K, V> void prefill(KeyedObjectPool<K, V> keyedPool, Collection<? extends K> keys, int count) throws Exception, IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        Iterator<K> iter = keys.iterator();
        while (iter.hasNext()) {
            PoolUtils.prefill(keyedPool, iter.next(), count);
        }
    }

    public static <T> ObjectPool<T> synchronizedPool(ObjectPool<T> pool) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        return new SynchronizedObjectPool<T>(pool);
    }

    public static <K, V> KeyedObjectPool<K, V> synchronizedPool(KeyedObjectPool<K, V> keyedPool) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        return new SynchronizedKeyedObjectPool<K, V>(keyedPool);
    }

    public static <T> PoolableObjectFactory<T> synchronizedPoolableFactory(PoolableObjectFactory<T> factory) {
        return new SynchronizedPoolableObjectFactory<T>(factory);
    }

    public static <K, V> KeyedPoolableObjectFactory<K, V> synchronizedPoolableFactory(KeyedPoolableObjectFactory<K, V> keyedFactory) {
        return new SynchronizedKeyedPoolableObjectFactory<K, V>(keyedFactory);
    }

    public static <T> ObjectPool<T> erodingPool(ObjectPool<T> pool) {
        return PoolUtils.erodingPool(pool, 1.0f);
    }

    public static <T> ObjectPool<T> erodingPool(ObjectPool<T> pool, float factor) {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException("factor must be positive.");
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
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (factor <= 0.0f) {
            throw new IllegalArgumentException("factor must be positive.");
        }
        if (perKey) {
            return new ErodingPerKeyKeyedObjectPool<K, V>(keyedPool, factor);
        }
        return new ErodingKeyedObjectPool<K, V>(keyedPool, factor);
    }

    private static synchronized Timer getMinIdleTimer() {
        if (MIN_IDLE_TIMER == null) {
            MIN_IDLE_TIMER = new Timer(true);
        }
        return MIN_IDLE_TIMER;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ErodingPerKeyKeyedObjectPool<K, V>
    extends ErodingKeyedObjectPool<K, V> {
        private final float factor;
        private final Map<K, ErodingFactor> factors = Collections.synchronizedMap(new HashMap());

        public ErodingPerKeyKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, float factor) {
            super(keyedPool, null);
            this.factor = factor;
        }

        @Override
        protected int numIdle(K key) {
            return this.getKeyedPool().getNumIdle(key);
        }

        @Override
        protected ErodingFactor getErodingFactor(K key) {
            ErodingFactor factor = this.factors.get(key);
            if (factor == null) {
                factor = new ErodingFactor(this.factor);
                this.factors.put(key, factor);
            }
            return factor;
        }

        @Override
        public String toString() {
            return "ErodingPerKeyKeyedObjectPool{factor=" + this.factor + ", keyedPool=" + this.getKeyedPool() + '}';
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ErodingKeyedObjectPool<K, V>
    implements KeyedObjectPool<K, V> {
        private final KeyedObjectPool<K, V> keyedPool;
        private final ErodingFactor erodingFactor;

        public ErodingKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, float factor) {
            this(keyedPool, new ErodingFactor(factor));
        }

        protected ErodingKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, ErodingFactor erodingFactor) {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
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
                    int numIdle = this.numIdle(key);
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
            catch (Exception e) {
                // empty catch block
            }
        }

        protected int numIdle(K key) {
            return this.getKeyedPool().getNumIdle();
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
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle();
        }

        @Override
        public int getNumIdle(K key) throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(key);
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive();
        }

        @Override
        public int getNumActive(K key) throws UnsupportedOperationException {
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

        @Override
        @Deprecated
        public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(factory);
        }

        protected KeyedObjectPool<K, V> getKeyedPool() {
            return this.keyedPool;
        }

        public String toString() {
            return "ErodingKeyedObjectPool{erodingFactor=" + this.erodingFactor + ", keyedPool=" + this.keyedPool + '}';
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
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
            catch (Exception e) {
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
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
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

        @Override
        @Deprecated
        public void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(factory);
        }

        public String toString() {
            return "ErodingObjectPool{factor=" + this.factor + ", pool=" + this.pool + '}';
        }
    }

    private static class ErodingFactor {
        private final float factor;
        private volatile transient long nextShrink;
        private volatile transient int idleHighWaterMark;

        public ErodingFactor(float factor) {
            this.factor = factor;
            this.nextShrink = System.currentTimeMillis() + (long)(900000.0f * factor);
            this.idleHighWaterMark = 1;
        }

        public void update(int numIdle) {
            this.update(System.currentTimeMillis(), numIdle);
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SynchronizedKeyedPoolableObjectFactory<K, V>
    implements KeyedPoolableObjectFactory<K, V> {
        private final Object lock;
        private final KeyedPoolableObjectFactory<K, V> keyedFactory;

        SynchronizedKeyedPoolableObjectFactory(KeyedPoolableObjectFactory<K, V> keyedFactory) throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            this.keyedFactory = keyedFactory;
            this.lock = new Object();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V makeObject(K key) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedFactory.makeObject(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void destroyObject(K key, V obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.keyedFactory.destroyObject(key, obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean validateObject(K key, V obj) {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedFactory.validateObject(key, obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void activateObject(K key, V obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.keyedFactory.activateObject(key, obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void passivateObject(K key, V obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.keyedFactory.passivateObject(key, obj);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedKeyedPoolableObjectFactory");
            sb.append("{keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SynchronizedPoolableObjectFactory<T>
    implements PoolableObjectFactory<T> {
        private final Object lock;
        private final PoolableObjectFactory<T> factory;

        SynchronizedPoolableObjectFactory(PoolableObjectFactory<T> factory) throws IllegalArgumentException {
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
            this.lock = new Object();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public T makeObject() throws Exception {
            Object object = this.lock;
            synchronized (object) {
                return this.factory.makeObject();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void destroyObject(T obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.factory.destroyObject(obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean validateObject(T obj) {
            Object object = this.lock;
            synchronized (object) {
                return this.factory.validateObject(obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void activateObject(T obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.factory.activateObject(obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void passivateObject(T obj) throws Exception {
            Object object = this.lock;
            synchronized (object) {
                this.factory.passivateObject(obj);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedPoolableObjectFactory");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SynchronizedKeyedObjectPool<K, V>
    implements KeyedObjectPool<K, V> {
        private final Object lock;
        private final KeyedObjectPool<K, V> keyedPool;

        SynchronizedKeyedObjectPool(KeyedObjectPool<K, V> keyedPool) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.lock = new Object();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedPool.borrowObject(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(K key, V obj) {
            Object object = this.lock;
            synchronized (object) {
                try {
                    this.keyedPool.returnObject(key, obj);
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
        public void invalidateObject(K key, V obj) {
            Object object = this.lock;
            synchronized (object) {
                try {
                    this.keyedPool.invalidateObject(key, obj);
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
        public void addObject(K key) throws Exception, IllegalStateException, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.keyedPool.addObject(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumIdle(K key) throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedPool.getNumIdle(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumActive(K key) throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedPool.getNumActive(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedPool.getNumIdle();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.keyedPool.getNumActive();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.keyedPool.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear(K key) throws Exception, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.keyedPool.clear(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            try {
                Object object = this.lock;
                synchronized (object) {
                    this.keyedPool.close();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.keyedPool.setFactory(factory);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedKeyedObjectPool");
            sb.append("{keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SynchronizedObjectPool<T>
    implements ObjectPool<T> {
        private final Object lock;
        private final ObjectPool<T> pool;

        SynchronizedObjectPool(ObjectPool<T> pool) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
            this.lock = new Object();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            Object object = this.lock;
            synchronized (object) {
                return this.pool.borrowObject();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void returnObject(T obj) {
            Object object = this.lock;
            synchronized (object) {
                try {
                    this.pool.returnObject(obj);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void invalidateObject(T obj) {
            Object object = this.lock;
            synchronized (object) {
                try {
                    this.pool.invalidateObject(obj);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.pool.addObject();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.pool.getNumIdle();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getNumActive() throws UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                return this.pool.getNumActive();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.pool.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            try {
                Object object = this.lock;
                synchronized (object) {
                    this.pool.close();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Deprecated
        public void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException, UnsupportedOperationException {
            Object object = this.lock;
            synchronized (object) {
                this.pool.setFactory(factory);
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("SynchronizedObjectPool");
            sb.append("{pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class KeyedObjectPoolMinIdleTimerTask<K, V>
    extends TimerTask {
        private final int minIdle;
        private final K key;
        private final KeyedObjectPool<K, V> keyedPool;

        KeyedObjectPoolMinIdleTimerTask(KeyedObjectPool<K, V> keyedPool, K key, int minIdle) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
            this.minIdle = minIdle;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
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
            StringBuffer sb = new StringBuffer();
            sb.append("KeyedObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ObjectPoolMinIdleTimerTask<T>
    extends TimerTask {
        private final int minIdle;
        private final ObjectPool<T> pool;

        ObjectPoolMinIdleTimerTask(ObjectPool<T> pool, int minIdle) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
            this.minIdle = minIdle;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
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
            StringBuffer sb = new StringBuffer();
            sb.append("ObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class CheckedKeyedObjectPool<K, V>
    implements KeyedObjectPool<K, V> {
        private final Class<V> type;
        private final KeyedObjectPool<K, V> keyedPool;

        CheckedKeyedObjectPool(KeyedObjectPool<K, V> keyedPool, Class<V> type) {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null.");
            }
            this.keyedPool = keyedPool;
            this.type = type;
        }

        @Override
        public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
            V obj = this.keyedPool.borrowObject(key);
            if (this.type.isInstance(obj)) {
                return obj;
            }
            throw new ClassCastException("Borrowed object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
        }

        @Override
        public void returnObject(K key, V obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.keyedPool.returnObject(key, obj);
                }
                catch (Exception exception) {}
            } else {
                throw new ClassCastException("Returned object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
            }
        }

        @Override
        public void invalidateObject(K key, V obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.keyedPool.invalidateObject(key, obj);
                }
                catch (Exception exception) {}
            } else {
                throw new ClassCastException("Invalidated object for key: " + key + " is not of type: " + this.type.getName() + " was: " + obj);
            }
        }

        @Override
        public void addObject(K key) throws Exception, IllegalStateException, UnsupportedOperationException {
            this.keyedPool.addObject(key);
        }

        @Override
        public int getNumIdle(K key) throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(key);
        }

        @Override
        public int getNumActive(K key) throws UnsupportedOperationException {
            return this.keyedPool.getNumActive(key);
        }

        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle();
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive();
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

        @Override
        @Deprecated
        public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(factory);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("CheckedKeyedObjectPool");
            sb.append("{type=").append(this.type);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class CheckedObjectPool<T>
    implements ObjectPool<T> {
        private final Class<T> type;
        private final ObjectPool<T> pool;

        CheckedObjectPool(ObjectPool<T> pool, Class<T> type) {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            if (type == null) {
                throw new IllegalArgumentException("type must not be null.");
            }
            this.pool = pool;
            this.type = type;
        }

        @Override
        public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            T obj = this.pool.borrowObject();
            if (this.type.isInstance(obj)) {
                return obj;
            }
            throw new ClassCastException("Borrowed object is not of type: " + this.type.getName() + " was: " + obj);
        }

        @Override
        public void returnObject(T obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.pool.returnObject(obj);
                }
                catch (Exception exception) {}
            } else {
                throw new ClassCastException("Returned object is not of type: " + this.type.getName() + " was: " + obj);
            }
        }

        @Override
        public void invalidateObject(T obj) {
            if (this.type.isInstance(obj)) {
                try {
                    this.pool.invalidateObject(obj);
                }
                catch (Exception exception) {}
            } else {
                throw new ClassCastException("Invalidated object is not of type: " + this.type.getName() + " was: " + obj);
            }
        }

        @Override
        public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
            this.pool.addObject();
        }

        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
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

        @Override
        @Deprecated
        public void setFactory(PoolableObjectFactory<T> factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(factory);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("CheckedObjectPool");
            sb.append("{type=").append(this.type);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class KeyedObjectPoolAdaptor<K, V>
    implements KeyedObjectPool<K, V> {
        private final ObjectPool<V> pool;

        KeyedObjectPoolAdaptor(ObjectPool<V> pool) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
        }

        @Override
        public V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException {
            return this.pool.borrowObject();
        }

        @Override
        public void returnObject(K key, V obj) {
            try {
                this.pool.returnObject(obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void invalidateObject(K key, V obj) {
            try {
                this.pool.invalidateObject(obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void addObject(K key) throws Exception, IllegalStateException {
            this.pool.addObject();
        }

        @Override
        public int getNumIdle(K key) throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }

        @Override
        public int getNumActive(K key) throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }

        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.pool.getNumIdle();
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.pool.getNumActive();
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.pool.clear();
        }

        @Override
        public void clear(K key) throws Exception, UnsupportedOperationException {
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

        @Override
        @Deprecated
        public void setFactory(KeyedPoolableObjectFactory<K, V> factory) throws IllegalStateException, UnsupportedOperationException {
            this.pool.setFactory(PoolUtils.adapt(factory));
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("KeyedObjectPoolAdaptor");
            sb.append("{pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ObjectPoolAdaptor<V>
    implements ObjectPool<V> {
        private final Object key;
        private final KeyedObjectPool<Object, V> keyedPool;

        ObjectPoolAdaptor(KeyedObjectPool<Object, V> keyedPool, Object key) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            if (key == null) {
                throw new IllegalArgumentException("key must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
        }

        @Override
        public V borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
            return this.keyedPool.borrowObject(this.key);
        }

        @Override
        public void returnObject(V obj) {
            try {
                this.keyedPool.returnObject(this.key, obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void invalidateObject(V obj) {
            try {
                this.keyedPool.invalidateObject(this.key, obj);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public void addObject() throws Exception, IllegalStateException {
            this.keyedPool.addObject(this.key);
        }

        @Override
        public int getNumIdle() throws UnsupportedOperationException {
            return this.keyedPool.getNumIdle(this.key);
        }

        @Override
        public int getNumActive() throws UnsupportedOperationException {
            return this.keyedPool.getNumActive(this.key);
        }

        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            this.keyedPool.clear();
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

        @Override
        @Deprecated
        public void setFactory(PoolableObjectFactory<V> factory) throws IllegalStateException, UnsupportedOperationException {
            this.keyedPool.setFactory(PoolUtils.adapt(factory));
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("ObjectPoolAdaptor");
            sb.append("{key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class KeyedPoolableObjectFactoryAdaptor<K, V>
    implements KeyedPoolableObjectFactory<K, V> {
        private final PoolableObjectFactory<V> factory;

        KeyedPoolableObjectFactoryAdaptor(PoolableObjectFactory<V> factory) throws IllegalArgumentException {
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
        }

        @Override
        public V makeObject(K key) throws Exception {
            return this.factory.makeObject();
        }

        @Override
        public void destroyObject(K key, V obj) throws Exception {
            this.factory.destroyObject(obj);
        }

        @Override
        public boolean validateObject(K key, V obj) {
            return this.factory.validateObject(obj);
        }

        @Override
        public void activateObject(K key, V obj) throws Exception {
            this.factory.activateObject(obj);
        }

        @Override
        public void passivateObject(K key, V obj) throws Exception {
            this.factory.passivateObject(obj);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("KeyedPoolableObjectFactoryAdaptor");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PoolableObjectFactoryAdaptor<K, V>
    implements PoolableObjectFactory<V> {
        private final K key;
        private final KeyedPoolableObjectFactory<K, V> keyedFactory;

        PoolableObjectFactoryAdaptor(KeyedPoolableObjectFactory<K, V> keyedFactory, K key) throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            if (key == null) {
                throw new IllegalArgumentException("key must not be null.");
            }
            this.keyedFactory = keyedFactory;
            this.key = key;
        }

        @Override
        public V makeObject() throws Exception {
            return this.keyedFactory.makeObject(this.key);
        }

        @Override
        public void destroyObject(V obj) throws Exception {
            this.keyedFactory.destroyObject(this.key, obj);
        }

        @Override
        public boolean validateObject(V obj) {
            return this.keyedFactory.validateObject(this.key, obj);
        }

        @Override
        public void activateObject(V obj) throws Exception {
            this.keyedFactory.activateObject(this.key, obj);
        }

        @Override
        public void passivateObject(V obj) throws Exception {
            this.keyedFactory.passivateObject(this.key, obj);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("PoolableObjectFactoryAdaptor");
            sb.append("{key=").append(this.key);
            sb.append(", keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }
}

