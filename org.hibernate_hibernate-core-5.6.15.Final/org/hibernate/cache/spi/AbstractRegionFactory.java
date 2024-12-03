/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.StandardCacheTransactionSynchronization;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.support.RegionNameQualifier;
import org.hibernate.cache.spi.support.SimpleTimestamper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class AbstractRegionFactory
implements RegionFactory {
    private final AtomicBoolean started = new AtomicBoolean(false);
    public static final List<String> LEGACY_QUERY_RESULTS_REGION_UNQUALIFIED_NAMES = Collections.unmodifiableList(Arrays.asList("org.hibernate.cache.spi.QueryResultsRegion", "org.hibernate.cache.internal.StandardQueryCache"));
    public static final List<String> LEGACY_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAMES = Collections.unmodifiableList(Arrays.asList("org.hibernate.cache.spi.TimestampsRegion", "org.hibernate.cache.spi.UpdateTimestampsCache"));
    private Exception startingException;
    private SessionFactoryOptions options;

    protected boolean isStarted() {
        if (this.started.get()) {
            assert (this.options != null);
            return true;
        }
        assert (this.options == null);
        throw new IllegalStateException("Cache provider not started", this.startingException);
    }

    protected void verifyStarted() {
        if (!this.verifiedStartStatus()) {
            throw new IllegalStateException("Cache provider not started", this.startingException);
        }
    }

    protected boolean verifiedStartStatus() {
        if (this.started.get()) {
            assert (this.options != null);
            return true;
        }
        assert (this.options == null);
        return false;
    }

    protected SessionFactoryOptions getOptions() {
        this.verifyStarted();
        return this.options;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void start(SessionFactoryOptions settings, Map configValues) throws CacheException {
        if (this.started.compareAndSet(false, true)) {
            AbstractRegionFactory abstractRegionFactory = this;
            synchronized (abstractRegionFactory) {
                this.options = settings;
                try {
                    this.prepareForUse(settings, configValues);
                    this.startingException = null;
                }
                catch (Exception e) {
                    this.options = null;
                    this.started.set(false);
                    this.startingException = e;
                }
            }
        }
        SecondLevelCacheLogger.INSTANCE.attemptToStartAlreadyStartedCacheProvider();
    }

    protected abstract void prepareForUse(SessionFactoryOptions var1, Map var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void stop() {
        if (this.started.compareAndSet(true, false)) {
            AbstractRegionFactory abstractRegionFactory = this;
            synchronized (abstractRegionFactory) {
                try {
                    this.releaseFromUse();
                }
                finally {
                    this.options = null;
                    this.startingException = null;
                }
            }
        }
        SecondLevelCacheLogger.INSTANCE.attemptToStopAlreadyStoppedCacheProvider();
    }

    protected abstract void releaseFromUse();

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    public String qualify(String regionName) {
        return RegionNameQualifier.INSTANCE.qualify(regionName, this.options);
    }

    @Override
    public CacheTransactionSynchronization createTransactionContext(SharedSessionContractImplementor session) {
        return new StandardCacheTransactionSynchronization(this);
    }

    @Override
    public long nextTimestamp() {
        return SimpleTimestamper.next();
    }

    @Override
    public long getTimeout() {
        return SimpleTimestamper.timeOut();
    }
}

