/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  io.atlassian.util.concurrent.Promise
 *  io.atlassian.util.concurrent.Promises
 *  io.atlassian.util.concurrent.Promises$SettablePromise
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.osgi;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsModuleMetaData;
import com.atlassian.activeobjects.external.FailedFastCountException;
import com.atlassian.activeobjects.internal.ActiveObjectsFactory;
import com.atlassian.activeobjects.internal.ActiveObjectsInitException;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ActiveObjectsDelegate
implements ActiveObjects {
    private static final Logger logger = LoggerFactory.getLogger(ActiveObjectsDelegate.class);
    private static final String SINGLETON = "singleton";
    private final Bundle bundle;
    @VisibleForTesting
    final Promises.SettablePromise<ActiveObjectsConfiguration> aoConfigFuture = Promises.settablePromise();
    @VisibleForTesting
    final LoadingCache<String, Promise<ActiveObjects>> aoPromisesBySingleton;

    ActiveObjectsDelegate(final @Nonnull Bundle bundle, final @Nonnull ActiveObjectsFactory factory, final @Nonnull Supplier<ExecutorService> initExecutorSupplier) {
        this.bundle = (Bundle)Preconditions.checkNotNull((Object)bundle);
        Preconditions.checkNotNull((Object)factory);
        Preconditions.checkNotNull(initExecutorSupplier);
        this.aoPromisesBySingleton = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Promise<ActiveObjects>>(){

            public Promise<ActiveObjects> load(@Nonnull String singleton) {
                logger.debug("bundle [{}] loading new AO promise for {}", (Object)bundle.getSymbolicName(), (Object)singleton);
                return ActiveObjectsDelegate.this.aoConfigFuture.flatMap(arg_0 -> 1.lambda$load$1(bundle, (Supplier)initExecutorSupplier, factory, arg_0));
            }

            private static /* synthetic */ Promise lambda$load$1(Bundle bundle2, Supplier initExecutorSupplier2, ActiveObjectsFactory factory2, ActiveObjectsConfiguration aoConfig) {
                logger.debug("bundle [{}] got ActiveObjectsConfiguration ", (Object)bundle2.getSymbolicName());
                Promises.SettablePromise aoPromise = Promises.settablePromise();
                ((ExecutorService)initExecutorSupplier2.get()).submit(() -> {
                    try {
                        logger.debug("bundle [{}] creating ActiveObjects", (Object)bundle2.getSymbolicName());
                        ActiveObjects ao = factory2.create(aoConfig);
                        logger.debug("bundle [{}] created ActiveObjects", (Object)bundle2.getSymbolicName());
                        aoPromise.set((Object)ao);
                    }
                    catch (Throwable t) {
                        ActiveObjectsInitException activeObjectsInitException = new ActiveObjectsInitException("bundle [" + bundle2.getSymbolicName() + "]", t);
                        aoPromise.exception((Throwable)activeObjectsInitException);
                        logger.warn("bundle [{}] failed to create ActiveObjects", (Object)bundle2.getSymbolicName(), (Object)t);
                    }
                    return null;
                });
                return aoPromise;
            }
        });
    }

    public void init() {
        logger.debug("init bundle [{}]", (Object)this.bundle.getSymbolicName());
        this.aoPromisesBySingleton.invalidate((Object)SINGLETON);
        this.startActiveObjects();
    }

    public void destroy() {
        this.aoConfigFuture.cancel(false);
        for (Promise aoPromise : this.aoPromisesBySingleton.asMap().values()) {
            aoPromise.cancel(false);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void setAoConfiguration(@Nonnull ActiveObjectsConfiguration aoConfiguration) {
        ActiveObjectsConfiguration currentAoConfiguration;
        logger.debug("setAoConfiguration [{}]", (Object)this.bundle.getSymbolicName());
        if (!this.aoConfigFuture.isDone()) {
            this.aoConfigFuture.set((Object)aoConfiguration);
            return;
        }
        try {
            currentAoConfiguration = (ActiveObjectsConfiguration)this.aoConfigFuture.get(0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
        if (currentAoConfiguration == aoConfiguration) {
            logger.debug("setAoConfiguration received same <ao> configuration twice [{}]", (Object)aoConfiguration);
            return;
        }
        IllegalStateException e = new IllegalStateException("bundle [" + this.bundle.getSymbolicName() + "] has multiple active objects configurations - only one active objects module descriptor <ao> allowed per plugin!");
        this.aoConfigFuture.exception((Throwable)e);
        throw e;
    }

    void startActiveObjects() {
        this.aoPromisesBySingleton.getUnchecked((Object)SINGLETON);
    }

    Promise<ActiveObjects> restartActiveObjects() {
        this.aoPromisesBySingleton.invalidate((Object)SINGLETON);
        return (Promise)this.aoPromisesBySingleton.getUnchecked((Object)SINGLETON);
    }

    @VisibleForTesting
    protected Promise<ActiveObjects> delegate() {
        if (!this.aoConfigFuture.isDone()) {
            throw new IllegalStateException("plugin [{" + this.bundle.getSymbolicName() + "}] invoking ActiveObjects before <ao> configuration module is enabled or plugin is missing an <ao> configuration module. Note that scanning of entities from the ao.model package is no longer supported.");
        }
        return (Promise)this.aoPromisesBySingleton.getUnchecked((Object)SINGLETON);
    }

    @Override
    public ActiveObjectsModuleMetaData moduleMetaData() {
        return new ActiveObjectsModuleMetaData(){

            @Override
            public void awaitInitialization() throws ExecutionException, InterruptedException {
                ((Promise)ActiveObjectsDelegate.this.aoPromisesBySingleton.getUnchecked((Object)ActiveObjectsDelegate.SINGLETON)).get();
            }

            @Override
            public void awaitInitialization(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                ((Promise)ActiveObjectsDelegate.this.aoPromisesBySingleton.getUnchecked((Object)ActiveObjectsDelegate.SINGLETON)).get(timeout, unit);
            }

            @Override
            public boolean isInitialized() {
                Promise aoPromise = (Promise)ActiveObjectsDelegate.this.aoPromisesBySingleton.getUnchecked((Object)ActiveObjectsDelegate.SINGLETON);
                if (aoPromise.isDone()) {
                    try {
                        aoPromise.claim();
                        return true;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                return false;
            }

            @Override
            public DatabaseType getDatabaseType() {
                return ((ActiveObjects)ActiveObjectsDelegate.this.delegate().claim()).moduleMetaData().getDatabaseType();
            }

            @Override
            public boolean isDataSourcePresent() {
                return true;
            }

            @Override
            public boolean isTablePresent(Class<? extends RawEntity<?>> type) {
                return ((ActiveObjects)ActiveObjectsDelegate.this.delegate().claim()).moduleMetaData().isTablePresent(type);
            }
        };
    }

    @Override
    public void migrate(Class<? extends RawEntity<?>> ... entities) {
        ((ActiveObjects)this.delegate().claim()).migrate(entities);
    }

    @Override
    public void migrateDestructively(Class<? extends RawEntity<?>> ... entities) {
        ((ActiveObjects)this.delegate().claim()).migrateDestructively(entities);
    }

    @Override
    public void flushAll() {
        ((ActiveObjects)this.delegate().claim()).flushAll();
    }

    @Override
    public void flush(RawEntity<?> ... entities) {
        ((ActiveObjects)this.delegate().claim()).flush(entities);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] get(Class<T> type, K ... keys) {
        return ((ActiveObjects)this.delegate().claim()).get(type, keys);
    }

    @Override
    public <T extends RawEntity<K>, K> T get(Class<T> type, K key) {
        return ((ActiveObjects)this.delegate().claim()).get(type, key);
    }

    @Override
    public <T extends RawEntity<K>, K> T create(Class<T> type, DBParam ... params) {
        return ((ActiveObjects)this.delegate().claim()).create(type, params);
    }

    @Override
    public <T extends RawEntity<K>, K> T create(Class<T> type, Map<String, Object> params) {
        return ((ActiveObjects)this.delegate().claim()).create(type, params);
    }

    @Override
    public <T extends RawEntity<K>, K> void create(Class<T> type, List<Map<String, Object>> rows) {
        ((ActiveObjects)this.delegate().claim()).create(type, rows);
    }

    @Override
    public void delete(RawEntity<?> ... entities) {
        ((ActiveObjects)this.delegate().claim()).delete(entities);
    }

    @Override
    public <K> int deleteWithSQL(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) {
        return ((ActiveObjects)this.delegate().claim()).deleteWithSQL(type, criteria, parameters);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] find(Class<T> type) {
        return ((ActiveObjects)this.delegate().claim()).find(type);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] find(Class<T> type, String criteria, Object ... parameters) {
        return ((ActiveObjects)this.delegate().claim()).find(type, criteria, parameters);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] find(Class<T> type, Query query) {
        return ((ActiveObjects)this.delegate().claim()).find(type, query);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] find(Class<T> type, String field, Query query) {
        return ((ActiveObjects)this.delegate().claim()).find(type, field, query);
    }

    @Override
    public <T extends RawEntity<K>, K> T[] findWithSQL(Class<T> type, String keyField, String sql, Object ... parameters) {
        return ((ActiveObjects)this.delegate().claim()).findWithSQL(type, keyField, sql, parameters);
    }

    @Override
    public <T extends RawEntity<K>, K> void stream(Class<T> type, EntityStreamCallback<T, K> streamCallback) {
        ((ActiveObjects)this.delegate().claim()).stream(type, streamCallback);
    }

    @Override
    public <T extends RawEntity<K>, K> void stream(Class<T> type, Query query, EntityStreamCallback<T, K> streamCallback) {
        ((ActiveObjects)this.delegate().claim()).stream(type, query, streamCallback);
    }

    @Override
    public <K> int count(Class<? extends RawEntity<K>> type) {
        return ((ActiveObjects)this.delegate().claim()).count(type);
    }

    @Override
    public <K> int count(Class<? extends RawEntity<K>> type, String criteria, Object ... parameters) {
        return ((ActiveObjects)this.delegate().claim()).count(type, criteria, parameters);
    }

    @Override
    public <K> int count(Class<? extends RawEntity<K>> type, Query query) {
        return ((ActiveObjects)this.delegate().claim()).count(type, query);
    }

    @Override
    public <K> int getFastCountEstimate(Class<? extends RawEntity<K>> type) throws SQLException, FailedFastCountException {
        return ((ActiveObjects)this.delegate().claim()).getFastCountEstimate(type);
    }

    @Override
    public <T> T executeInTransaction(TransactionCallback<T> callback) {
        return ((ActiveObjects)this.delegate().claim()).executeInTransaction(callback);
    }

    public Bundle getBundle() {
        return this.bundle;
    }
}

