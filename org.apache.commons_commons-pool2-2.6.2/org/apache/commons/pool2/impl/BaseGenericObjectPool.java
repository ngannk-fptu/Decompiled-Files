/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.commons.pool2.BaseObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.SwallowedExceptionListener;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.EvictionTimer;

public abstract class BaseGenericObjectPool<T>
extends BaseObject {
    public static final int MEAN_TIMING_STATS_CACHE_SIZE = 100;
    private static final String EVICTION_POLICY_TYPE_NAME = EvictionPolicy.class.getName();
    private volatile int maxTotal = -1;
    private volatile boolean blockWhenExhausted = true;
    private volatile long maxWaitMillis = -1L;
    private volatile boolean lifo = true;
    private final boolean fairness;
    private volatile boolean testOnCreate = false;
    private volatile boolean testOnBorrow = false;
    private volatile boolean testOnReturn = false;
    private volatile boolean testWhileIdle = false;
    private volatile long timeBetweenEvictionRunsMillis = -1L;
    private volatile int numTestsPerEvictionRun = 3;
    private volatile long minEvictableIdleTimeMillis = 1800000L;
    private volatile long softMinEvictableIdleTimeMillis = -1L;
    private volatile EvictionPolicy<T> evictionPolicy;
    private volatile long evictorShutdownTimeoutMillis = 10000L;
    final Object closeLock = new Object();
    volatile boolean closed = false;
    final Object evictionLock = new Object();
    private Evictor evictor = null;
    EvictionIterator evictionIterator = null;
    private final WeakReference<ClassLoader> factoryClassLoader;
    private final ObjectName objectName;
    private final String creationStackTrace;
    private final AtomicLong borrowedCount = new AtomicLong(0L);
    private final AtomicLong returnedCount = new AtomicLong(0L);
    final AtomicLong createdCount = new AtomicLong(0L);
    final AtomicLong destroyedCount = new AtomicLong(0L);
    final AtomicLong destroyedByEvictorCount = new AtomicLong(0L);
    final AtomicLong destroyedByBorrowValidationCount = new AtomicLong(0L);
    private final StatsStore activeTimes = new StatsStore(100);
    private final StatsStore idleTimes = new StatsStore(100);
    private final StatsStore waitTimes = new StatsStore(100);
    private final AtomicLong maxBorrowWaitTimeMillis = new AtomicLong(0L);
    private volatile SwallowedExceptionListener swallowedExceptionListener = null;

    public BaseGenericObjectPool(BaseObjectPoolConfig<T> config, String jmxNameBase, String jmxNamePrefix) {
        this.objectName = config.getJmxEnabled() ? this.jmxRegister(config, jmxNameBase, jmxNamePrefix) : null;
        this.creationStackTrace = this.getStackTrace(new Exception());
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        this.factoryClassLoader = cl == null ? null : new WeakReference<ClassLoader>(cl);
        this.fairness = config.getFairness();
    }

    public final int getMaxTotal() {
        return this.maxTotal;
    }

    public final void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public final boolean getBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    public final void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    protected void setConfig(BaseObjectPoolConfig<T> conf) {
        this.setLifo(conf.getLifo());
        this.setMaxWaitMillis(conf.getMaxWaitMillis());
        this.setBlockWhenExhausted(conf.getBlockWhenExhausted());
        this.setTestOnCreate(conf.getTestOnCreate());
        this.setTestOnBorrow(conf.getTestOnBorrow());
        this.setTestOnReturn(conf.getTestOnReturn());
        this.setTestWhileIdle(conf.getTestWhileIdle());
        this.setNumTestsPerEvictionRun(conf.getNumTestsPerEvictionRun());
        this.setMinEvictableIdleTimeMillis(conf.getMinEvictableIdleTimeMillis());
        this.setTimeBetweenEvictionRunsMillis(conf.getTimeBetweenEvictionRunsMillis());
        this.setSoftMinEvictableIdleTimeMillis(conf.getSoftMinEvictableIdleTimeMillis());
        EvictionPolicy<T> policy = conf.getEvictionPolicy();
        if (policy == null) {
            this.setEvictionPolicyClassName(conf.getEvictionPolicyClassName());
        } else {
            this.setEvictionPolicy(policy);
        }
        this.setEvictorShutdownTimeoutMillis(conf.getEvictorShutdownTimeoutMillis());
    }

    public final long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }

    public final void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public final boolean getLifo() {
        return this.lifo;
    }

    public final boolean getFairness() {
        return this.fairness;
    }

    public final void setLifo(boolean lifo) {
        this.lifo = lifo;
    }

    public final boolean getTestOnCreate() {
        return this.testOnCreate;
    }

    public final void setTestOnCreate(boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
    }

    public final boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }

    public final void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public final boolean getTestOnReturn() {
        return this.testOnReturn;
    }

    public final void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public final boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }

    public final void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public final long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public final void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        this.startEvictor(timeBetweenEvictionRunsMillis);
    }

    public final int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    public final void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public final long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public final void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public final long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleTimeMillis;
    }

    public final void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }

    public final String getEvictionPolicyClassName() {
        return this.evictionPolicy.getClass().getName();
    }

    public void setEvictionPolicy(EvictionPolicy<T> evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    public final void setEvictionPolicyClassName(String evictionPolicyClassName, ClassLoader classLoader) {
        Class<EvictionPolicy> epClass = EvictionPolicy.class;
        ClassLoader epClassLoader = epClass.getClassLoader();
        try {
            try {
                this.setEvictionPolicy(evictionPolicyClassName, classLoader);
            }
            catch (ClassCastException | ClassNotFoundException e) {
                this.setEvictionPolicy(evictionPolicyClassName, epClassLoader);
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Class " + evictionPolicyClassName + " from class loaders [" + classLoader + ", " + epClassLoader + "] do not implement " + EVICTION_POLICY_TYPE_NAME);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            String exMessage = "Unable to create " + EVICTION_POLICY_TYPE_NAME + " instance of type " + evictionPolicyClassName;
            throw new IllegalArgumentException(exMessage, e);
        }
    }

    private void setEvictionPolicy(String className, ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<?> clazz = Class.forName(className, true, classLoader);
        Object policy = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        this.evictionPolicy = (EvictionPolicy)policy;
    }

    public final void setEvictionPolicyClassName(String evictionPolicyClassName) {
        this.setEvictionPolicyClassName(evictionPolicyClassName, Thread.currentThread().getContextClassLoader());
    }

    public final long getEvictorShutdownTimeoutMillis() {
        return this.evictorShutdownTimeoutMillis;
    }

    public final void setEvictorShutdownTimeoutMillis(long evictorShutdownTimeoutMillis) {
        this.evictorShutdownTimeoutMillis = evictorShutdownTimeoutMillis;
    }

    public abstract void close();

    public final boolean isClosed() {
        return this.closed;
    }

    public abstract void evict() throws Exception;

    public EvictionPolicy<T> getEvictionPolicy() {
        return this.evictionPolicy;
    }

    final void assertOpen() throws IllegalStateException {
        if (this.isClosed()) {
            throw new IllegalStateException("Pool not open");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void startEvictor(long delay) {
        Object object = this.evictionLock;
        synchronized (object) {
            EvictionTimer.cancel(this.evictor, this.evictorShutdownTimeoutMillis, TimeUnit.MILLISECONDS);
            this.evictor = null;
            this.evictionIterator = null;
            if (delay > 0L) {
                this.evictor = new Evictor();
                EvictionTimer.schedule(this.evictor, delay, delay);
            }
        }
    }

    void stopEvitor() {
        this.startEvictor(-1L);
    }

    abstract void ensureMinIdle() throws Exception;

    public final ObjectName getJmxName() {
        return this.objectName;
    }

    public final String getCreationStackTrace() {
        return this.creationStackTrace;
    }

    public final long getBorrowedCount() {
        return this.borrowedCount.get();
    }

    public final long getReturnedCount() {
        return this.returnedCount.get();
    }

    public final long getCreatedCount() {
        return this.createdCount.get();
    }

    public final long getDestroyedCount() {
        return this.destroyedCount.get();
    }

    public final long getDestroyedByEvictorCount() {
        return this.destroyedByEvictorCount.get();
    }

    public final long getDestroyedByBorrowValidationCount() {
        return this.destroyedByBorrowValidationCount.get();
    }

    public final long getMeanActiveTimeMillis() {
        return this.activeTimes.getMean();
    }

    public final long getMeanIdleTimeMillis() {
        return this.idleTimes.getMean();
    }

    public final long getMeanBorrowWaitTimeMillis() {
        return this.waitTimes.getMean();
    }

    public final long getMaxBorrowWaitTimeMillis() {
        return this.maxBorrowWaitTimeMillis.get();
    }

    public abstract int getNumIdle();

    public final SwallowedExceptionListener getSwallowedExceptionListener() {
        return this.swallowedExceptionListener;
    }

    public final void setSwallowedExceptionListener(SwallowedExceptionListener swallowedExceptionListener) {
        this.swallowedExceptionListener = swallowedExceptionListener;
    }

    final void swallowException(Exception swallowException) {
        SwallowedExceptionListener listener = this.getSwallowedExceptionListener();
        if (listener == null) {
            return;
        }
        try {
            listener.onSwallowException(swallowException);
        }
        catch (VirtualMachineError e) {
            throw e;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    final void updateStatsBorrow(PooledObject<T> p, long waitTime) {
        long currentMax;
        this.borrowedCount.incrementAndGet();
        this.idleTimes.add(p.getIdleTimeMillis());
        this.waitTimes.add(waitTime);
        while ((currentMax = this.maxBorrowWaitTimeMillis.get()) < waitTime && !this.maxBorrowWaitTimeMillis.compareAndSet(currentMax, waitTime)) {
        }
    }

    final void updateStatsReturn(long activeTime) {
        this.returnedCount.incrementAndGet();
        this.activeTimes.add(activeTime);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void markReturningState(PooledObject<T> pooledObject) {
        PooledObject<T> pooledObject2 = pooledObject;
        synchronized (pooledObject2) {
            PooledObjectState state = pooledObject.getState();
            if (state != PooledObjectState.ALLOCATED) {
                throw new IllegalStateException("Object has already been returned to this pool or is invalid");
            }
            pooledObject.markReturning();
        }
    }

    final void jmxUnregister() {
        if (this.objectName != null) {
            try {
                ManagementFactory.getPlatformMBeanServer().unregisterMBean(this.objectName);
            }
            catch (InstanceNotFoundException | MBeanRegistrationException e) {
                this.swallowException(e);
            }
        }
    }

    private ObjectName jmxRegister(BaseObjectPoolConfig<T> config, String jmxNameBase, String jmxNamePrefix) {
        ObjectName newObjectName = null;
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        int i = 1;
        boolean registered = false;
        String base = config.getJmxNameBase();
        if (base == null) {
            base = jmxNameBase;
        }
        while (!registered) {
            try {
                ObjectName objName = i == 1 ? new ObjectName(base + jmxNamePrefix) : new ObjectName(base + jmxNamePrefix + i);
                mbs.registerMBean(this, objName);
                newObjectName = objName;
                registered = true;
            }
            catch (MalformedObjectNameException e) {
                if ("pool".equals(jmxNamePrefix) && jmxNameBase.equals(base)) {
                    registered = true;
                    continue;
                }
                jmxNamePrefix = "pool";
                base = jmxNameBase;
            }
            catch (InstanceAlreadyExistsException e) {
                ++i;
            }
            catch (MBeanRegistrationException | NotCompliantMBeanException e) {
                registered = true;
            }
        }
        return newObjectName;
    }

    private String getStackTrace(Exception e) {
        StringWriter w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        return ((Object)w).toString();
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        builder.append("maxTotal=");
        builder.append(this.maxTotal);
        builder.append(", blockWhenExhausted=");
        builder.append(this.blockWhenExhausted);
        builder.append(", maxWaitMillis=");
        builder.append(this.maxWaitMillis);
        builder.append(", lifo=");
        builder.append(this.lifo);
        builder.append(", fairness=");
        builder.append(this.fairness);
        builder.append(", testOnCreate=");
        builder.append(this.testOnCreate);
        builder.append(", testOnBorrow=");
        builder.append(this.testOnBorrow);
        builder.append(", testOnReturn=");
        builder.append(this.testOnReturn);
        builder.append(", testWhileIdle=");
        builder.append(this.testWhileIdle);
        builder.append(", timeBetweenEvictionRunsMillis=");
        builder.append(this.timeBetweenEvictionRunsMillis);
        builder.append(", numTestsPerEvictionRun=");
        builder.append(this.numTestsPerEvictionRun);
        builder.append(", minEvictableIdleTimeMillis=");
        builder.append(this.minEvictableIdleTimeMillis);
        builder.append(", softMinEvictableIdleTimeMillis=");
        builder.append(this.softMinEvictableIdleTimeMillis);
        builder.append(", evictionPolicy=");
        builder.append(this.evictionPolicy);
        builder.append(", closeLock=");
        builder.append(this.closeLock);
        builder.append(", closed=");
        builder.append(this.closed);
        builder.append(", evictionLock=");
        builder.append(this.evictionLock);
        builder.append(", evictor=");
        builder.append(this.evictor);
        builder.append(", evictionIterator=");
        builder.append(this.evictionIterator);
        builder.append(", factoryClassLoader=");
        builder.append(this.factoryClassLoader);
        builder.append(", oname=");
        builder.append(this.objectName);
        builder.append(", creationStackTrace=");
        builder.append(this.creationStackTrace);
        builder.append(", borrowedCount=");
        builder.append(this.borrowedCount);
        builder.append(", returnedCount=");
        builder.append(this.returnedCount);
        builder.append(", createdCount=");
        builder.append(this.createdCount);
        builder.append(", destroyedCount=");
        builder.append(this.destroyedCount);
        builder.append(", destroyedByEvictorCount=");
        builder.append(this.destroyedByEvictorCount);
        builder.append(", destroyedByBorrowValidationCount=");
        builder.append(this.destroyedByBorrowValidationCount);
        builder.append(", activeTimes=");
        builder.append(this.activeTimes);
        builder.append(", idleTimes=");
        builder.append(this.idleTimes);
        builder.append(", waitTimes=");
        builder.append(this.waitTimes);
        builder.append(", maxBorrowWaitTimeMillis=");
        builder.append(this.maxBorrowWaitTimeMillis);
        builder.append(", swallowedExceptionListener=");
        builder.append(this.swallowedExceptionListener);
    }

    static class IdentityWrapper<T> {
        private final T instance;

        public IdentityWrapper(T instance) {
            this.instance = instance;
        }

        public int hashCode() {
            return System.identityHashCode(this.instance);
        }

        public boolean equals(Object other) {
            return other instanceof IdentityWrapper && ((IdentityWrapper)other).instance == this.instance;
        }

        public T getObject() {
            return this.instance;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IdentityWrapper [instance=");
            builder.append(this.instance);
            builder.append("]");
            return builder.toString();
        }
    }

    class EvictionIterator
    implements Iterator<PooledObject<T>> {
        private final Deque<PooledObject<T>> idleObjects;
        private final Iterator<PooledObject<T>> idleObjectIterator;

        EvictionIterator(Deque<PooledObject<T>> idleObjects) {
            this.idleObjects = idleObjects;
            this.idleObjectIterator = BaseGenericObjectPool.this.getLifo() ? idleObjects.descendingIterator() : idleObjects.iterator();
        }

        public Deque<PooledObject<T>> getIdleObjects() {
            return this.idleObjects;
        }

        @Override
        public boolean hasNext() {
            return this.idleObjectIterator.hasNext();
        }

        @Override
        public PooledObject<T> next() {
            return this.idleObjectIterator.next();
        }

        @Override
        public void remove() {
            this.idleObjectIterator.remove();
        }
    }

    private class StatsStore {
        private final AtomicLong[] values;
        private final int size;
        private int index;

        public StatsStore(int size) {
            this.size = size;
            this.values = new AtomicLong[size];
            for (int i = 0; i < size; ++i) {
                this.values[i] = new AtomicLong(-1L);
            }
        }

        public synchronized void add(long value) {
            this.values[this.index].set(value);
            ++this.index;
            if (this.index == this.size) {
                this.index = 0;
            }
        }

        public long getMean() {
            double result = 0.0;
            int counter = 0;
            for (int i = 0; i < this.size; ++i) {
                long value = this.values[i].get();
                if (value == -1L) continue;
                result = result * ((double)(++counter - 1) / (double)counter) + (double)value / (double)counter;
            }
            return (long)result;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("StatsStore [values=");
            builder.append(Arrays.toString(this.values));
            builder.append(", size=");
            builder.append(this.size);
            builder.append(", index=");
            builder.append(this.index);
            builder.append("]");
            return builder.toString();
        }
    }

    class Evictor
    implements Runnable {
        private ScheduledFuture<?> scheduledFuture;

        Evictor() {
        }

        @Override
        public void run() {
            ClassLoader savedClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                if (BaseGenericObjectPool.this.factoryClassLoader != null) {
                    ClassLoader cl = (ClassLoader)BaseGenericObjectPool.this.factoryClassLoader.get();
                    if (cl == null) {
                        this.cancel();
                        return;
                    }
                    Thread.currentThread().setContextClassLoader(cl);
                }
                try {
                    BaseGenericObjectPool.this.evict();
                }
                catch (Exception e) {
                    BaseGenericObjectPool.this.swallowException(e);
                }
                catch (OutOfMemoryError oome) {
                    oome.printStackTrace(System.err);
                }
                try {
                    BaseGenericObjectPool.this.ensureMinIdle();
                }
                catch (Exception e) {
                    BaseGenericObjectPool.this.swallowException(e);
                }
            }
            finally {
                Thread.currentThread().setContextClassLoader(savedClassLoader);
            }
        }

        void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        void cancel() {
            this.scheduledFuture.cancel(false);
        }
    }
}

