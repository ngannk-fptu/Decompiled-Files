/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PoolUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.UsageTracking;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.BaseGenericObjectPool;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfo;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolMXBean;
import org.apache.commons.pool2.impl.LinkedBlockingDeque;
import org.apache.commons.pool2.impl.PoolImplUtils;

public class GenericObjectPool<T>
extends BaseGenericObjectPool<T>
implements ObjectPool<T>,
GenericObjectPoolMXBean,
UsageTracking<T> {
    private volatile String factoryType = null;
    private volatile int maxIdle = 8;
    private volatile int minIdle = 0;
    private final PooledObjectFactory<T> factory;
    private final Map<BaseGenericObjectPool.IdentityWrapper<T>, PooledObject<T>> allObjects = new ConcurrentHashMap<BaseGenericObjectPool.IdentityWrapper<T>, PooledObject<T>>();
    private final AtomicLong createCount = new AtomicLong(0L);
    private long makeObjectCount = 0L;
    private final Object makeObjectCountLock = new Object();
    private final LinkedBlockingDeque<PooledObject<T>> idleObjects;
    private static final String ONAME_BASE = "org.apache.commons.pool2:type=GenericObjectPool,name=";
    private volatile AbandonedConfig abandonedConfig = null;

    public GenericObjectPool(PooledObjectFactory<T> factory) {
        this(factory, new GenericObjectPoolConfig());
    }

    public GenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config) {
        super(config, ONAME_BASE, config.getJmxNamePrefix());
        if (factory == null) {
            this.jmxUnregister();
            throw new IllegalArgumentException("factory may not be null");
        }
        this.factory = factory;
        this.idleObjects = new LinkedBlockingDeque(config.getFairness());
        this.setConfig(config);
    }

    public GenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        this(factory, config);
        this.setAbandonedConfig(abandonedConfig);
    }

    @Override
    public int getMaxIdle() {
        return this.maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    @Override
    public int getMinIdle() {
        int maxIdleSave = this.getMaxIdle();
        if (this.minIdle > maxIdleSave) {
            return maxIdleSave;
        }
        return this.minIdle;
    }

    @Override
    public boolean isAbandonedConfig() {
        return this.abandonedConfig != null;
    }

    @Override
    public boolean getLogAbandoned() {
        AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getLogAbandoned();
    }

    @Override
    public boolean getRemoveAbandonedOnBorrow() {
        AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getRemoveAbandonedOnBorrow();
    }

    @Override
    public boolean getRemoveAbandonedOnMaintenance() {
        AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getRemoveAbandonedOnMaintenance();
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        AbandonedConfig ac = this.abandonedConfig;
        return ac != null ? ac.getRemoveAbandonedTimeout() : Integer.MAX_VALUE;
    }

    @Override
    public void setConfig(GenericObjectPoolConfig<T> conf) {
        super.setConfig(conf);
        this.setMaxIdle(conf.getMaxIdle());
        this.setMinIdle(conf.getMinIdle());
        this.setMaxTotal(conf.getMaxTotal());
    }

    public void setAbandonedConfig(AbandonedConfig abandonedConfig) {
        if (abandonedConfig == null) {
            this.abandonedConfig = null;
        } else {
            this.abandonedConfig = new AbandonedConfig();
            this.abandonedConfig.setLogAbandoned(abandonedConfig.getLogAbandoned());
            this.abandonedConfig.setLogWriter(abandonedConfig.getLogWriter());
            this.abandonedConfig.setRemoveAbandonedOnBorrow(abandonedConfig.getRemoveAbandonedOnBorrow());
            this.abandonedConfig.setRemoveAbandonedOnMaintenance(abandonedConfig.getRemoveAbandonedOnMaintenance());
            this.abandonedConfig.setRemoveAbandonedTimeout(abandonedConfig.getRemoveAbandonedTimeout());
            this.abandonedConfig.setUseUsageTracking(abandonedConfig.getUseUsageTracking());
            this.abandonedConfig.setRequireFullStackTrace(abandonedConfig.getRequireFullStackTrace());
        }
    }

    public PooledObjectFactory<T> getFactory() {
        return this.factory;
    }

    @Override
    public T borrowObject() throws Exception {
        return this.borrowObject(this.getMaxWaitMillis());
    }

    public T borrowObject(long borrowMaxWaitMillis) throws Exception {
        this.assertOpen();
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getRemoveAbandonedOnBorrow() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxTotal() - 3) {
            this.removeAbandoned(ac);
        }
        PooledObject<T> p = null;
        boolean blockWhenExhausted = this.getBlockWhenExhausted();
        long waitTime = System.currentTimeMillis();
        while (p == null) {
            boolean create;
            block17: {
                create = false;
                p = this.idleObjects.pollFirst();
                if (p == null && (p = this.create()) != null) {
                    create = true;
                }
                if (blockWhenExhausted) {
                    if (p == null) {
                        p = borrowMaxWaitMillis < 0L ? this.idleObjects.takeFirst() : this.idleObjects.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
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
                    this.factory.activateObject(p);
                }
                catch (Exception e) {
                    try {
                        this.destroy(p);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    p = null;
                    if (!create) break block17;
                    NoSuchElementException nsee = new NoSuchElementException("Unable to activate object");
                    nsee.initCause(e);
                    throw nsee;
                }
            }
            if (p == null || !this.getTestOnBorrow() && (!create || !this.getTestOnCreate())) continue;
            boolean validate = false;
            Throwable validationThrowable = null;
            try {
                validate = this.factory.validateObject(p);
            }
            catch (Throwable t) {
                PoolUtils.checkRethrow(t);
                validationThrowable = t;
            }
            if (validate) continue;
            try {
                this.destroy(p);
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
        this.updateStatsBorrow(p, System.currentTimeMillis() - waitTime);
        return p.getObject();
    }

    @Override
    public void returnObject(T obj) {
        PooledObject<T> p = this.allObjects.get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
        if (p == null) {
            if (!this.isAbandonedConfig()) {
                throw new IllegalStateException("Returned object not currently part of this pool");
            }
            return;
        }
        this.markReturningState(p);
        long activeTime = p.getActiveTimeMillis();
        if (this.getTestOnReturn() && !this.factory.validateObject(p)) {
            try {
                this.destroy(p);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            try {
                this.ensureIdle(1, false);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            this.updateStatsReturn(activeTime);
            return;
        }
        try {
            this.factory.passivateObject(p);
        }
        catch (Exception e1) {
            this.swallowException(e1);
            try {
                this.destroy(p);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            try {
                this.ensureIdle(1, false);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            this.updateStatsReturn(activeTime);
            return;
        }
        if (!p.deallocate()) {
            throw new IllegalStateException("Object has already been returned to this pool or is invalid");
        }
        int maxIdleSave = this.getMaxIdle();
        if (this.isClosed() || maxIdleSave > -1 && maxIdleSave <= this.idleObjects.size()) {
            try {
                this.destroy(p);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
        } else {
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
            } else {
                this.idleObjects.addLast(p);
            }
            if (this.isClosed()) {
                this.clear();
            }
        }
        this.updateStatsReturn(activeTime);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateObject(T obj) throws Exception {
        PooledObject<T> p = this.allObjects.get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
        if (p == null) {
            if (this.isAbandonedConfig()) {
                return;
            }
            throw new IllegalStateException("Invalidated object not currently part of this pool");
        }
        PooledObject<T> pooledObject = p;
        synchronized (pooledObject) {
            if (p.getState() != PooledObjectState.INVALID) {
                this.destroy(p);
            }
        }
        this.ensureIdle(1, false);
    }

    @Override
    public void clear() {
        PooledObject<T> p = this.idleObjects.poll();
        while (p != null) {
            try {
                this.destroy(p);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            p = this.idleObjects.poll();
        }
    }

    @Override
    public int getNumActive() {
        return this.allObjects.size() - this.idleObjects.size();
    }

    @Override
    public int getNumIdle() {
        return this.idleObjects.size();
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
            this.idleObjects.interuptTakeWaiters();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void evict() throws Exception {
        AbandonedConfig ac;
        this.assertOpen();
        if (this.idleObjects.size() > 0) {
            Object underTest = null;
            EvictionPolicy evictionPolicy = this.getEvictionPolicy();
            Object object = this.evictionLock;
            synchronized (object) {
                EvictionConfig evictionConfig = new EvictionConfig(this.getMinEvictableIdleTimeMillis(), this.getSoftMinEvictableIdleTimeMillis(), this.getMinIdle());
                boolean testWhileIdle = this.getTestWhileIdle();
                int m = this.getNumTests();
                for (int i = 0; i < m; ++i) {
                    boolean evict;
                    if (this.evictionIterator == null || !this.evictionIterator.hasNext()) {
                        this.evictionIterator = new BaseGenericObjectPool.EvictionIterator(this.idleObjects);
                    }
                    if (!this.evictionIterator.hasNext()) {
                        return;
                    }
                    try {
                        underTest = this.evictionIterator.next();
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
                        evict = evictionPolicy.evict(evictionConfig, underTest, this.idleObjects.size());
                    }
                    catch (Throwable t) {
                        PoolUtils.checkRethrow(t);
                        this.swallowException(new Exception(t));
                        evict = false;
                    }
                    if (evict) {
                        this.destroy((PooledObject<T>)underTest);
                        this.destroyedByEvictorCount.incrementAndGet();
                        continue;
                    }
                    if (testWhileIdle) {
                        boolean active = false;
                        try {
                            this.factory.activateObject((PooledObject<T>)underTest);
                            active = true;
                        }
                        catch (Exception e) {
                            this.destroy((PooledObject<T>)underTest);
                            this.destroyedByEvictorCount.incrementAndGet();
                        }
                        if (active) {
                            if (!this.factory.validateObject((PooledObject<T>)underTest)) {
                                this.destroy((PooledObject<T>)underTest);
                                this.destroyedByEvictorCount.incrementAndGet();
                            } else {
                                try {
                                    this.factory.passivateObject((PooledObject<T>)underTest);
                                }
                                catch (Exception e) {
                                    this.destroy((PooledObject<T>)underTest);
                                    this.destroyedByEvictorCount.incrementAndGet();
                                }
                            }
                        }
                    }
                    if (underTest.endEvictionTest(this.idleObjects)) continue;
                }
            }
        }
        if ((ac = this.abandonedConfig) != null && ac.getRemoveAbandonedOnMaintenance()) {
            this.removeAbandoned(ac);
        }
    }

    public void preparePool() throws Exception {
        if (this.getMinIdle() < 1) {
            return;
        }
        this.ensureMinIdle();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PooledObject<T> create() throws Exception {
        PooledObject<T> p;
        int localMaxTotal = this.getMaxTotal();
        if (localMaxTotal < 0) {
            localMaxTotal = Integer.MAX_VALUE;
        }
        long localStartTimeMillis = System.currentTimeMillis();
        long localMaxWaitTimeMillis = Math.max(this.getMaxWaitMillis(), 0L);
        Boolean create = null;
        while (create == null) {
            Object object = this.makeObjectCountLock;
            synchronized (object) {
                long newCreateCount = this.createCount.incrementAndGet();
                if (newCreateCount > (long)localMaxTotal) {
                    this.createCount.decrementAndGet();
                    if (this.makeObjectCount == 0L) {
                        create = Boolean.FALSE;
                    } else {
                        this.makeObjectCountLock.wait(localMaxWaitTimeMillis);
                    }
                } else {
                    ++this.makeObjectCount;
                    create = Boolean.TRUE;
                }
            }
            if (create != null || localMaxWaitTimeMillis <= 0L || System.currentTimeMillis() - localStartTimeMillis < localMaxWaitTimeMillis) continue;
            create = Boolean.FALSE;
        }
        if (!create.booleanValue()) {
            return null;
        }
        try {
            p = this.factory.makeObject();
        }
        catch (Throwable e) {
            this.createCount.decrementAndGet();
            throw e;
        }
        finally {
            Object newCreateCount = this.makeObjectCountLock;
            synchronized (newCreateCount) {
                --this.makeObjectCount;
                this.makeObjectCountLock.notifyAll();
            }
        }
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getLogAbandoned()) {
            p.setLogAbandoned(true);
            if (p instanceof DefaultPooledObject) {
                ((DefaultPooledObject)p).setRequireFullStackTrace(ac.getRequireFullStackTrace());
            }
        }
        this.createdCount.incrementAndGet();
        this.allObjects.put(new BaseGenericObjectPool.IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    private void destroy(PooledObject<T> toDestroy) throws Exception {
        toDestroy.invalidate();
        this.idleObjects.remove(toDestroy);
        this.allObjects.remove(new BaseGenericObjectPool.IdentityWrapper<T>(toDestroy.getObject()));
        try {
            this.factory.destroyObject(toDestroy);
        }
        finally {
            this.destroyedCount.incrementAndGet();
            this.createCount.decrementAndGet();
        }
        if (this.idleObjects.isEmpty() && this.idleObjects.hasTakeWaiters()) {
            PooledObject<T> freshPooled = this.create();
            this.idleObjects.put(freshPooled);
        }
    }

    @Override
    void ensureMinIdle() throws Exception {
        this.ensureIdle(this.getMinIdle(), true);
    }

    private void ensureIdle(int idleCount, boolean always) throws Exception {
        PooledObject<T> p;
        if (idleCount < 1 || this.isClosed() || !always && !this.idleObjects.hasTakeWaiters()) {
            return;
        }
        while (this.idleObjects.size() < idleCount && (p = this.create()) != null) {
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
                continue;
            }
            this.idleObjects.addLast(p);
        }
        if (this.isClosed()) {
            this.clear();
        }
    }

    @Override
    public void addObject() throws Exception {
        this.assertOpen();
        if (this.factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        PooledObject<T> p = this.create();
        this.addIdleObject(p);
    }

    private void addIdleObject(PooledObject<T> p) throws Exception {
        if (p != null) {
            this.factory.passivateObject(p);
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
            } else {
                this.idleObjects.addLast(p);
            }
        }
    }

    private int getNumTests() {
        int numTestsPerEvictionRun = this.getNumTestsPerEvictionRun();
        if (numTestsPerEvictionRun >= 0) {
            return Math.min(numTestsPerEvictionRun, this.idleObjects.size());
        }
        return (int)Math.ceil((double)this.idleObjects.size() / Math.abs((double)numTestsPerEvictionRun));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeAbandoned(AbandonedConfig ac) {
        long now = System.currentTimeMillis();
        long timeout = now - (long)ac.getRemoveAbandonedTimeout() * 1000L;
        ArrayList<PooledObject<T>> remove = new ArrayList<PooledObject<T>>();
        Iterator<PooledObject<T>> it = this.allObjects.values().iterator();
        while (it.hasNext()) {
            PooledObject<T> pooledObject;
            PooledObject<T> pooledObject2 = pooledObject = it.next();
            synchronized (pooledObject2) {
                if (pooledObject.getState() == PooledObjectState.ALLOCATED && pooledObject.getLastUsedTime() <= timeout) {
                    pooledObject.markAbandoned();
                    remove.add(pooledObject);
                }
            }
        }
        for (PooledObject pooledObject : remove) {
            if (ac.getLogAbandoned()) {
                pooledObject.printStackTrace(ac.getLogWriter());
            }
            try {
                this.invalidateObject(pooledObject.getObject());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void use(T pooledObject) {
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getUseUsageTracking()) {
            PooledObject<T> wrapper = this.allObjects.get(new BaseGenericObjectPool.IdentityWrapper<T>(pooledObject));
            wrapper.use();
        }
    }

    @Override
    public int getNumWaiters() {
        if (this.getBlockWhenExhausted()) {
            return this.idleObjects.getTakeQueueLength();
        }
        return 0;
    }

    @Override
    public String getFactoryType() {
        if (this.factoryType == null) {
            StringBuilder result = new StringBuilder();
            result.append(this.factory.getClass().getName());
            result.append('<');
            Class<?> pooledObjectType = PoolImplUtils.getFactoryType(this.factory.getClass());
            result.append(pooledObjectType.getName());
            result.append('>');
            this.factoryType = result.toString();
        }
        return this.factoryType;
    }

    @Override
    public Set<DefaultPooledObjectInfo> listAllObjects() {
        HashSet<DefaultPooledObjectInfo> result = new HashSet<DefaultPooledObjectInfo>(this.allObjects.size());
        for (PooledObject<T> p : this.allObjects.values()) {
            result.add(new DefaultPooledObjectInfo(p));
        }
        return result;
    }

    @Override
    protected void toStringAppendFields(StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", factoryType=");
        builder.append(this.factoryType);
        builder.append(", maxIdle=");
        builder.append(this.maxIdle);
        builder.append(", minIdle=");
        builder.append(this.minIdle);
        builder.append(", factory=");
        builder.append(this.factory);
        builder.append(", allObjects=");
        builder.append(this.allObjects);
        builder.append(", createCount=");
        builder.append(this.createCount);
        builder.append(", idleObjects=");
        builder.append(this.idleObjects);
        builder.append(", abandonedConfig=");
        builder.append(this.abandonedConfig);
    }
}

