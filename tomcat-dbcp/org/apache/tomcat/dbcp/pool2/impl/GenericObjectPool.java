/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.PoolUtils;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;
import org.apache.tomcat.dbcp.pool2.UsageTracking;
import org.apache.tomcat.dbcp.pool2.impl.AbandonedConfig;
import org.apache.tomcat.dbcp.pool2.impl.BaseGenericObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObjectInfo;
import org.apache.tomcat.dbcp.pool2.impl.EvictionConfig;
import org.apache.tomcat.dbcp.pool2.impl.EvictionPolicy;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolConfig;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolMXBean;
import org.apache.tomcat.dbcp.pool2.impl.LinkedBlockingDeque;
import org.apache.tomcat.dbcp.pool2.impl.PoolImplUtils;

public class GenericObjectPool<T>
extends BaseGenericObjectPool<T>
implements ObjectPool<T>,
GenericObjectPoolMXBean,
UsageTracking<T> {
    private static final String ONAME_BASE = "org.apache.commons.pool2:type=GenericObjectPool,name=";
    private volatile String factoryType;
    private volatile int maxIdle = 8;
    private volatile int minIdle = 0;
    private final PooledObjectFactory<T> factory;
    private final ConcurrentHashMap<BaseGenericObjectPool.IdentityWrapper<T>, PooledObject<T>> allObjects = new ConcurrentHashMap();
    private final AtomicLong createCount = new AtomicLong();
    private long makeObjectCount;
    private final Object makeObjectCountLock = new Object();
    private final LinkedBlockingDeque<PooledObject<T>> idleObjects;

    private static void wait(Object obj, Duration duration) throws InterruptedException {
        obj.wait(duration.toMillis(), duration.getNano() % 1000000);
    }

    public GenericObjectPool(PooledObjectFactory<T> factory) {
        this(factory, new GenericObjectPoolConfig());
    }

    public GenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config) {
        super(config, ONAME_BASE, config.getJmxNamePrefix());
        if (factory == null) {
            this.jmxUnregister();
            throw new IllegalArgumentException("Factory may not be null");
        }
        this.factory = factory;
        this.idleObjects = new LinkedBlockingDeque(config.getFairness());
        this.setConfig(config);
    }

    public GenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig<T> config, AbandonedConfig abandonedConfig) {
        this(factory, config);
        this.setAbandonedConfig(abandonedConfig);
    }

    private void addIdleObject(PooledObject<T> p) throws Exception {
        if (!PooledObject.isNull(p)) {
            this.factory.passivateObject(p);
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
            } else {
                this.idleObjects.addLast(p);
            }
        }
    }

    @Override
    public void addObject() throws Exception {
        this.assertOpen();
        if (this.factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        this.addIdleObject(this.create());
    }

    @Override
    public T borrowObject() throws Exception {
        return this.borrowObject(this.getMaxWaitDuration());
    }

    public T borrowObject(Duration borrowMaxWaitDuration) throws Exception {
        this.assertOpen();
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getRemoveAbandonedOnBorrow() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxTotal() - 3) {
            this.removeAbandoned(ac);
        }
        PooledObject<T> p = null;
        boolean blockWhenExhausted = this.getBlockWhenExhausted();
        Instant waitTime = Instant.now();
        while (p == null) {
            boolean create;
            block17: {
                create = false;
                p = this.idleObjects.pollFirst();
                if (p == null && !PooledObject.isNull(p = this.create())) {
                    create = true;
                }
                if (blockWhenExhausted) {
                    if (PooledObject.isNull(p)) {
                        PooledObject<T> pooledObject = p = borrowMaxWaitDuration.isNegative() ? this.idleObjects.takeFirst() : this.idleObjects.pollFirst(borrowMaxWaitDuration);
                    }
                    if (PooledObject.isNull(p)) {
                        throw new NoSuchElementException(this.appendStats("Timeout waiting for idle object, borrowMaxWaitDuration=" + borrowMaxWaitDuration));
                    }
                } else if (PooledObject.isNull(p)) {
                    throw new NoSuchElementException(this.appendStats("Pool exhausted"));
                }
                if (!p.allocate()) {
                    p = null;
                }
                if (PooledObject.isNull(p)) continue;
                try {
                    this.factory.activateObject(p);
                }
                catch (Exception e) {
                    try {
                        this.destroy(p, DestroyMode.NORMAL);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    p = null;
                    if (!create) break block17;
                    NoSuchElementException nsee = new NoSuchElementException(this.appendStats("Unable to activate object"));
                    nsee.initCause(e);
                    throw nsee;
                }
            }
            if (PooledObject.isNull(p) || !this.getTestOnBorrow()) continue;
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
                this.destroy(p, DestroyMode.NORMAL);
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
        this.updateStatsBorrow(p, Duration.between(waitTime, Instant.now()));
        return p.getObject();
    }

    public T borrowObject(long borrowMaxWaitMillis) throws Exception {
        return this.borrowObject(Duration.ofMillis(borrowMaxWaitMillis));
    }

    @Override
    public void clear() {
        PooledObject<T> p = this.idleObjects.poll();
        while (p != null) {
            try {
                this.destroy(p, DestroyMode.NORMAL);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
            p = this.idleObjects.poll();
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
            this.idleObjects.interuptTakeWaiters();
        }
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
        Instant localStartInstant = Instant.now();
        Duration maxWaitDurationRaw = this.getMaxWaitDuration();
        Duration localMaxWaitDuration = maxWaitDurationRaw.isNegative() ? Duration.ZERO : maxWaitDurationRaw;
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
                        GenericObjectPool.wait(this.makeObjectCountLock, localMaxWaitDuration);
                    }
                } else {
                    ++this.makeObjectCount;
                    create = Boolean.TRUE;
                }
            }
            if (create != null || localMaxWaitDuration.compareTo(Duration.ZERO) <= 0 || Duration.between(localStartInstant, Instant.now()).compareTo(localMaxWaitDuration) < 0) continue;
            create = Boolean.FALSE;
        }
        if (!create.booleanValue()) {
            return null;
        }
        try {
            p = this.factory.makeObject();
            if (PooledObject.isNull(p)) {
                this.createCount.decrementAndGet();
                throw new NullPointerException(String.format("%s.makeObject() = null", this.factory.getClass().getSimpleName()));
            }
            if (this.getTestOnCreate() && !this.factory.validateObject(p)) {
                this.createCount.decrementAndGet();
                PooledObject<T> newCreateCount = null;
                return newCreateCount;
            }
        }
        catch (Throwable e) {
            this.createCount.decrementAndGet();
            throw e;
        }
        finally {
            Object object = this.makeObjectCountLock;
            synchronized (object) {
                --this.makeObjectCount;
                this.makeObjectCountLock.notifyAll();
            }
        }
        AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getLogAbandoned()) {
            p.setLogAbandoned(true);
            p.setRequireFullStackTrace(ac.getRequireFullStackTrace());
        }
        this.createdCount.incrementAndGet();
        this.allObjects.put(new BaseGenericObjectPool.IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    private void destroy(PooledObject<T> toDestroy, DestroyMode destroyMode) throws Exception {
        toDestroy.invalidate();
        this.idleObjects.remove(toDestroy);
        this.allObjects.remove(new BaseGenericObjectPool.IdentityWrapper<T>(toDestroy.getObject()));
        try {
            this.factory.destroyObject(toDestroy, destroyMode);
        }
        finally {
            this.destroyedCount.incrementAndGet();
            this.createCount.decrementAndGet();
        }
    }

    private void ensureIdle(int idleCount, boolean always) throws Exception {
        PooledObject<T> p;
        if (idleCount < 1 || this.isClosed() || !always && !this.idleObjects.hasTakeWaiters()) {
            return;
        }
        while (this.idleObjects.size() < idleCount && !PooledObject.isNull(p = this.create())) {
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
    void ensureMinIdle() throws Exception {
        this.ensureIdle(this.getMinIdle(), true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void evict() throws Exception {
        AbandonedConfig ac;
        this.assertOpen();
        if (!this.idleObjects.isEmpty()) {
            Object underTest = null;
            EvictionPolicy evictionPolicy = this.getEvictionPolicy();
            Object object = this.evictionLock;
            synchronized (object) {
                EvictionConfig evictionConfig = new EvictionConfig(this.getMinEvictableIdleDuration(), this.getSoftMinEvictableIdleDuration(), this.getMinIdle());
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
                        this.destroy((PooledObject<T>)underTest, DestroyMode.NORMAL);
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
                            this.destroy((PooledObject<T>)underTest, DestroyMode.NORMAL);
                            this.destroyedByEvictorCount.incrementAndGet();
                        }
                        if (active) {
                            boolean validate = false;
                            Throwable validationThrowable = null;
                            try {
                                validate = this.factory.validateObject((PooledObject<T>)underTest);
                            }
                            catch (Throwable t) {
                                PoolUtils.checkRethrow(t);
                                validationThrowable = t;
                            }
                            if (!validate) {
                                this.destroy((PooledObject<T>)underTest, DestroyMode.NORMAL);
                                this.destroyedByEvictorCount.incrementAndGet();
                                if (validationThrowable != null) {
                                    if (validationThrowable instanceof RuntimeException) {
                                        throw (RuntimeException)validationThrowable;
                                    }
                                    throw (Error)validationThrowable;
                                }
                            } else {
                                try {
                                    this.factory.passivateObject((PooledObject<T>)underTest);
                                }
                                catch (Exception e) {
                                    this.destroy((PooledObject<T>)underTest, DestroyMode.NORMAL);
                                    this.destroyedByEvictorCount.incrementAndGet();
                                }
                            }
                        }
                    }
                    underTest.endEvictionTest(this.idleObjects);
                }
            }
        }
        if ((ac = this.abandonedConfig) != null && ac.getRemoveAbandonedOnMaintenance()) {
            this.removeAbandoned(ac);
        }
    }

    public PooledObjectFactory<T> getFactory() {
        return this.factory;
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
    public int getMaxIdle() {
        return this.maxIdle;
    }

    @Override
    public int getMinIdle() {
        int maxIdleSave = this.getMaxIdle();
        return Math.min(this.minIdle, maxIdleSave);
    }

    @Override
    public int getNumActive() {
        return this.allObjects.size() - this.idleObjects.size();
    }

    @Override
    public int getNumIdle() {
        return this.idleObjects.size();
    }

    private int getNumTests() {
        int numTestsPerEvictionRun = this.getNumTestsPerEvictionRun();
        if (numTestsPerEvictionRun >= 0) {
            return Math.min(numTestsPerEvictionRun, this.idleObjects.size());
        }
        return (int)Math.ceil((double)this.idleObjects.size() / Math.abs((double)numTestsPerEvictionRun));
    }

    @Override
    public int getNumWaiters() {
        if (this.getBlockWhenExhausted()) {
            return this.idleObjects.getTakeQueueLength();
        }
        return 0;
    }

    PooledObject<T> getPooledObject(T obj) {
        return this.allObjects.get(new BaseGenericObjectPool.IdentityWrapper<T>(obj));
    }

    @Override
    String getStatsString() {
        return super.getStatsString() + String.format(", createdCount=%,d, makeObjectCount=%,d, maxIdle=%,d, minIdle=%,d", this.createdCount.get(), this.makeObjectCount, this.maxIdle, this.minIdle);
    }

    @Override
    public void invalidateObject(T obj) throws Exception {
        this.invalidateObject(obj, DestroyMode.NORMAL);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidateObject(T obj, DestroyMode destroyMode) throws Exception {
        PooledObject<T> p = this.getPooledObject(obj);
        if (p == null) {
            if (this.isAbandonedConfig()) {
                return;
            }
            throw new IllegalStateException("Invalidated object not currently part of this pool");
        }
        PooledObject<T> pooledObject = p;
        synchronized (pooledObject) {
            if (p.getState() != PooledObjectState.INVALID) {
                this.destroy(p, destroyMode);
            }
        }
        this.ensureIdle(1, false);
    }

    @Override
    public Set<DefaultPooledObjectInfo> listAllObjects() {
        return this.allObjects.values().stream().map(DefaultPooledObjectInfo::new).collect(Collectors.toSet());
    }

    public void preparePool() throws Exception {
        if (this.getMinIdle() < 1) {
            return;
        }
        this.ensureMinIdle();
    }

    private void removeAbandoned(AbandonedConfig abandonedConfig) {
        ArrayList<PooledObject<T>> remove = this.createRemoveList(abandonedConfig, this.allObjects);
        remove.forEach((Consumer<PooledObject<T>>)((Consumer<PooledObject>)pooledObject -> {
            if (abandonedConfig.getLogAbandoned()) {
                pooledObject.printStackTrace(abandonedConfig.getLogWriter());
            }
            try {
                this.invalidateObject(pooledObject.getObject(), DestroyMode.ABANDONED);
            }
            catch (Exception e) {
                this.swallowException(e);
            }
        }));
    }

    @Override
    public void returnObject(T obj) {
        PooledObject<T> p = this.getPooledObject(obj);
        if (p == null) {
            if (!this.isAbandonedConfig()) {
                throw new IllegalStateException("Returned object not currently part of this pool");
            }
            return;
        }
        this.markReturningState(p);
        Duration activeTime = p.getActiveDuration();
        if (this.getTestOnReturn() && !this.factory.validateObject(p)) {
            try {
                this.destroy(p, DestroyMode.NORMAL);
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
                this.destroy(p, DestroyMode.NORMAL);
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
                this.destroy(p, DestroyMode.NORMAL);
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

    @Override
    public void setConfig(GenericObjectPoolConfig<T> conf) {
        super.setConfig(conf);
        this.setMaxIdle(conf.getMaxIdle());
        this.setMinIdle(conf.getMinIdle());
        this.setMaxTotal(conf.getMaxTotal());
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
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

    @Override
    public void use(T pooledObject) {
        PooledObject<T> po;
        AbandonedConfig abandonedCfg = this.abandonedConfig;
        if (abandonedCfg != null && abandonedCfg.getUseUsageTracking() && (po = this.getPooledObject(pooledObject)) != null) {
            po.use();
        }
    }
}

