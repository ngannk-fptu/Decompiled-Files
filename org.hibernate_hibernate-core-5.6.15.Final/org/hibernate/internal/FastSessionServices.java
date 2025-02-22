/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CacheRetrieveMode
 *  javax.persistence.CacheStoreMode
 *  javax.persistence.PessimisticLockScope
 */
package org.hibernate.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.PessimisticLockScope;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.ClearEventListener;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.DirtyCheckEventListener;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.EvictEventListener;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.event.spi.LockEventListener;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;
import org.hibernate.event.spi.PostCollectionRemoveEventListener;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreCollectionRecreateEventListener;
import org.hibernate.event.spi.PreCollectionRemoveEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.RefreshEventListener;
import org.hibernate.event.spi.ReplicateEventListener;
import org.hibernate.event.spi.ResolveNaturalIdEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.ConnectionObserverStatsBridge;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.jpa.internal.util.CacheModeHelper;
import org.hibernate.jpa.internal.util.ConfigurationHelper;
import org.hibernate.jpa.internal.util.LockOptionsHelper;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public final class FastSessionServices {
    final Map<String, Object> defaultSessionProperties;
    public final EventListenerGroup<AutoFlushEventListener> eventListenerGroup_AUTO_FLUSH;
    public final EventListenerGroup<ClearEventListener> eventListenerGroup_CLEAR;
    public final EventListenerGroup<DeleteEventListener> eventListenerGroup_DELETE;
    public final EventListenerGroup<DirtyCheckEventListener> eventListenerGroup_DIRTY_CHECK;
    public final EventListenerGroup<EvictEventListener> eventListenerGroup_EVICT;
    public final EventListenerGroup<FlushEntityEventListener> eventListenerGroup_FLUSH_ENTITY;
    public final EventListenerGroup<FlushEventListener> eventListenerGroup_FLUSH;
    public final EventListenerGroup<InitializeCollectionEventListener> eventListenerGroup_INIT_COLLECTION;
    public final EventListenerGroup<LoadEventListener> eventListenerGroup_LOAD;
    public final EventListenerGroup<LockEventListener> eventListenerGroup_LOCK;
    public final EventListenerGroup<MergeEventListener> eventListenerGroup_MERGE;
    public final EventListenerGroup<PersistEventListener> eventListenerGroup_PERSIST;
    public final EventListenerGroup<PersistEventListener> eventListenerGroup_PERSIST_ONFLUSH;
    public final EventListenerGroup<PostCollectionRecreateEventListener> eventListenerGroup_POST_COLLECTION_RECREATE;
    public final EventListenerGroup<PostCollectionRemoveEventListener> eventListenerGroup_POST_COLLECTION_REMOVE;
    public final EventListenerGroup<PostCollectionUpdateEventListener> eventListenerGroup_POST_COLLECTION_UPDATE;
    public final EventListenerGroup<PostDeleteEventListener> eventListenerGroup_POST_COMMIT_DELETE;
    public final EventListenerGroup<PostDeleteEventListener> eventListenerGroup_POST_DELETE;
    public final EventListenerGroup<PostInsertEventListener> eventListenerGroup_POST_COMMIT_INSERT;
    public final EventListenerGroup<PostInsertEventListener> eventListenerGroup_POST_INSERT;
    public final EventListenerGroup<PostLoadEventListener> eventListenerGroup_POST_LOAD;
    public final EventListenerGroup<PostUpdateEventListener> eventListenerGroup_POST_COMMIT_UPDATE;
    public final EventListenerGroup<PostUpdateEventListener> eventListenerGroup_POST_UPDATE;
    public final EventListenerGroup<PreCollectionRecreateEventListener> eventListenerGroup_PRE_COLLECTION_RECREATE;
    public final EventListenerGroup<PreCollectionRemoveEventListener> eventListenerGroup_PRE_COLLECTION_REMOVE;
    public final EventListenerGroup<PreCollectionUpdateEventListener> eventListenerGroup_PRE_COLLECTION_UPDATE;
    public final EventListenerGroup<PreDeleteEventListener> eventListenerGroup_PRE_DELETE;
    public final EventListenerGroup<PreInsertEventListener> eventListenerGroup_PRE_INSERT;
    public final EventListenerGroup<PreLoadEventListener> eventListenerGroup_PRE_LOAD;
    public final EventListenerGroup<PreUpdateEventListener> eventListenerGroup_PRE_UPDATE;
    public final EventListenerGroup<RefreshEventListener> eventListenerGroup_REFRESH;
    public final EventListenerGroup<ReplicateEventListener> eventListenerGroup_REPLICATE;
    public final EventListenerGroup<ResolveNaturalIdEventListener> eventListenerGroup_RESOLVE_NATURAL_ID;
    public final EventListenerGroup<SaveOrUpdateEventListener> eventListenerGroup_SAVE;
    public final EventListenerGroup<SaveOrUpdateEventListener> eventListenerGroup_SAVE_UPDATE;
    public final EventListenerGroup<SaveOrUpdateEventListener> eventListenerGroup_UPDATE;
    final boolean disallowOutOfTransactionUpdateOperations;
    final boolean useStreamForLobBinding;
    final boolean requiresMultiTenantConnectionProvider;
    final ConnectionProvider connectionProvider;
    final MultiTenantConnectionProvider multiTenantConnectionProvider;
    final ClassLoaderService classLoaderService;
    final TransactionCoordinatorBuilder transactionCoordinatorBuilder;
    final JdbcServices jdbcServices;
    final boolean isJtaTransactionAccessible;
    final CacheMode initialSessionCacheMode;
    final FlushMode initialSessionFlushMode;
    final boolean discardOnClose;
    final BaselineSessionEventsListenerBuilder defaultSessionEventListeners;
    final LockOptions defaultLockOptions;
    final int defaultJdbcBatchSize;
    private final CacheStoreMode defaultCacheStoreMode;
    private final CacheRetrieveMode defaultCacheRetrieveMode;
    private final ConnectionObserverStatsBridge defaultJdbcObservers;
    public final Dialect dialect;
    public final QueryTranslatorFactory queryTranslatorFactory;

    FastSessionServices(SessionFactoryImpl sf) {
        Objects.requireNonNull(sf);
        ServiceRegistryImplementor sr = sf.getServiceRegistry();
        JdbcServices jdbcServices = sf.getJdbcServices();
        SessionFactoryOptions sessionFactoryOptions = sf.getSessionFactoryOptions();
        EventListenerRegistry eventListenerRegistry = sr.getService(EventListenerRegistry.class);
        this.eventListenerGroup_AUTO_FLUSH = FastSessionServices.listeners(eventListenerRegistry, EventType.AUTO_FLUSH);
        this.eventListenerGroup_CLEAR = FastSessionServices.listeners(eventListenerRegistry, EventType.CLEAR);
        this.eventListenerGroup_DELETE = FastSessionServices.listeners(eventListenerRegistry, EventType.DELETE);
        this.eventListenerGroup_DIRTY_CHECK = FastSessionServices.listeners(eventListenerRegistry, EventType.DIRTY_CHECK);
        this.eventListenerGroup_EVICT = FastSessionServices.listeners(eventListenerRegistry, EventType.EVICT);
        this.eventListenerGroup_FLUSH = FastSessionServices.listeners(eventListenerRegistry, EventType.FLUSH);
        this.eventListenerGroup_FLUSH_ENTITY = FastSessionServices.listeners(eventListenerRegistry, EventType.FLUSH_ENTITY);
        this.eventListenerGroup_INIT_COLLECTION = FastSessionServices.listeners(eventListenerRegistry, EventType.INIT_COLLECTION);
        this.eventListenerGroup_LOAD = FastSessionServices.listeners(eventListenerRegistry, EventType.LOAD);
        this.eventListenerGroup_LOCK = FastSessionServices.listeners(eventListenerRegistry, EventType.LOCK);
        this.eventListenerGroup_MERGE = FastSessionServices.listeners(eventListenerRegistry, EventType.MERGE);
        this.eventListenerGroup_PERSIST = FastSessionServices.listeners(eventListenerRegistry, EventType.PERSIST);
        this.eventListenerGroup_PERSIST_ONFLUSH = FastSessionServices.listeners(eventListenerRegistry, EventType.PERSIST_ONFLUSH);
        this.eventListenerGroup_POST_COLLECTION_RECREATE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COLLECTION_RECREATE);
        this.eventListenerGroup_POST_COLLECTION_REMOVE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COLLECTION_REMOVE);
        this.eventListenerGroup_POST_COLLECTION_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COLLECTION_UPDATE);
        this.eventListenerGroup_POST_COMMIT_DELETE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COMMIT_DELETE);
        this.eventListenerGroup_POST_COMMIT_INSERT = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COMMIT_INSERT);
        this.eventListenerGroup_POST_COMMIT_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_COMMIT_UPDATE);
        this.eventListenerGroup_POST_DELETE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_DELETE);
        this.eventListenerGroup_POST_INSERT = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_INSERT);
        this.eventListenerGroup_POST_LOAD = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_LOAD);
        this.eventListenerGroup_POST_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.POST_UPDATE);
        this.eventListenerGroup_PRE_COLLECTION_RECREATE = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_COLLECTION_RECREATE);
        this.eventListenerGroup_PRE_COLLECTION_REMOVE = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_COLLECTION_REMOVE);
        this.eventListenerGroup_PRE_COLLECTION_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_COLLECTION_UPDATE);
        this.eventListenerGroup_PRE_DELETE = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_DELETE);
        this.eventListenerGroup_PRE_INSERT = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_INSERT);
        this.eventListenerGroup_PRE_LOAD = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_LOAD);
        this.eventListenerGroup_PRE_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.PRE_UPDATE);
        this.eventListenerGroup_REFRESH = FastSessionServices.listeners(eventListenerRegistry, EventType.REFRESH);
        this.eventListenerGroup_REPLICATE = FastSessionServices.listeners(eventListenerRegistry, EventType.REPLICATE);
        this.eventListenerGroup_RESOLVE_NATURAL_ID = FastSessionServices.listeners(eventListenerRegistry, EventType.RESOLVE_NATURAL_ID);
        this.eventListenerGroup_SAVE = FastSessionServices.listeners(eventListenerRegistry, EventType.SAVE);
        this.eventListenerGroup_SAVE_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.SAVE_UPDATE);
        this.eventListenerGroup_UPDATE = FastSessionServices.listeners(eventListenerRegistry, EventType.UPDATE);
        this.dialect = jdbcServices.getJdbcEnvironment().getDialect();
        this.disallowOutOfTransactionUpdateOperations = !sessionFactoryOptions.isAllowOutOfTransactionUpdateOperations();
        this.useStreamForLobBinding = Environment.useStreamsForBinary() || this.dialect.useInputStreamToInsertBlob();
        this.requiresMultiTenantConnectionProvider = sf.getSettings().getMultiTenancyStrategy().requiresMultiTenantConnectionProvider();
        this.defaultJdbcBatchSize = sessionFactoryOptions.getJdbcBatchSize();
        this.connectionProvider = this.requiresMultiTenantConnectionProvider ? null : sr.getService(ConnectionProvider.class);
        this.multiTenantConnectionProvider = this.requiresMultiTenantConnectionProvider ? sr.getService(MultiTenantConnectionProvider.class) : null;
        this.classLoaderService = sr.getService(ClassLoaderService.class);
        this.transactionCoordinatorBuilder = sr.getService(TransactionCoordinatorBuilder.class);
        this.jdbcServices = sr.getService(JdbcServices.class);
        this.queryTranslatorFactory = sr.getService(QueryTranslatorFactory.class);
        this.isJtaTransactionAccessible = FastSessionServices.isTransactionAccessible(sf, this.transactionCoordinatorBuilder);
        this.defaultSessionProperties = FastSessionServices.initializeDefaultSessionProperties(sf);
        this.defaultCacheStoreMode = FastSessionServices.determineCacheStoreMode(this.defaultSessionProperties);
        this.defaultCacheRetrieveMode = FastSessionServices.determineCacheRetrieveMode(this.defaultSessionProperties);
        this.initialSessionCacheMode = CacheModeHelper.interpretCacheMode(this.defaultCacheStoreMode, this.defaultCacheRetrieveMode);
        this.discardOnClose = sessionFactoryOptions.isReleaseResourcesOnCloseEnabled();
        this.defaultJdbcObservers = new ConnectionObserverStatsBridge(sf);
        this.defaultSessionEventListeners = sessionFactoryOptions.getBaselineSessionEventsListenerBuilder();
        this.defaultLockOptions = FastSessionServices.initializeDefaultLockOptions(this.defaultSessionProperties);
        this.initialSessionFlushMode = FastSessionServices.initializeDefaultFlushMode(this.defaultSessionProperties);
    }

    private static FlushMode initializeDefaultFlushMode(Map<String, Object> defaultSessionProperties) {
        Object setting = NullnessHelper.coalesceSuppliedValues(() -> defaultSessionProperties.get("org.hibernate.flushMode"), () -> {
            Object oldSetting = defaultSessionProperties.get("org.hibernate.flushMode");
            return oldSetting;
        });
        return ConfigurationHelper.getFlushMode(setting, FlushMode.AUTO);
    }

    private static LockOptions initializeDefaultLockOptions(Map<String, Object> defaultSessionProperties) {
        LockOptions def = new LockOptions();
        LockOptionsHelper.applyPropertiesToLockOptions(defaultSessionProperties, () -> def);
        return def;
    }

    private static <T> EventListenerGroup<T> listeners(EventListenerRegistry elr, EventType<T> type) {
        return elr.getEventListenerGroup(type);
    }

    SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        if (!sqlTypeDescriptor.canBeRemapped()) {
            return sqlTypeDescriptor;
        }
        SqlTypeDescriptor remapped = this.dialect.remapSqlTypeDescriptor(sqlTypeDescriptor);
        return remapped == null ? sqlTypeDescriptor : remapped;
    }

    private static boolean isTransactionAccessible(SessionFactoryImpl sf, TransactionCoordinatorBuilder transactionCoordinatorBuilder) {
        return !sf.getSessionFactoryOptions().getJpaCompliance().isJpaTransactionComplianceEnabled() || !transactionCoordinatorBuilder.isJta() || sf.getSessionFactoryOptions().isJtaTransactionAccessEnabled();
    }

    private static Map<String, Object> initializeDefaultSessionProperties(SessionFactoryImpl sf) {
        HashMap<String, Object> p = new HashMap<String, Object>();
        p.putIfAbsent("org.hibernate.flushMode", FlushMode.AUTO.name());
        p.putIfAbsent("javax.persistence.lock.scope", PessimisticLockScope.EXTENDED.name());
        p.putIfAbsent("jakarta.persistence.lock.scope", PessimisticLockScope.EXTENDED.name());
        p.putIfAbsent("javax.persistence.lock.timeout", -1);
        p.putIfAbsent("jakarta.persistence.lock.timeout", -1);
        p.putIfAbsent("javax.persistence.cache.retrieveMode", CacheModeHelper.DEFAULT_RETRIEVE_MODE);
        p.putIfAbsent("jakarta.persistence.cache.retrieveMode", CacheModeHelper.DEFAULT_RETRIEVE_MODE);
        p.putIfAbsent("javax.persistence.cache.storeMode", CacheModeHelper.DEFAULT_STORE_MODE);
        p.putIfAbsent("jakarta.persistence.cache.storeMode", CacheModeHelper.DEFAULT_STORE_MODE);
        String[] ENTITY_MANAGER_SPECIFIC_PROPERTIES = new String[]{"javax.persistence.lock.scope", "jakarta.persistence.lock.scope", "javax.persistence.lock.timeout", "jakarta.persistence.lock.timeout", "org.hibernate.flushMode", "javax.persistence.cache.retrieveMode", "jakarta.persistence.cache.retrieveMode", "javax.persistence.cache.storeMode", "jakarta.persistence.cache.storeMode", "javax.persistence.query.timeout", "jakarta.persistence.query.timeout"};
        Map<String, Object> properties = sf.getProperties();
        for (String key : ENTITY_MANAGER_SPECIFIC_PROPERTIES) {
            if (!properties.containsKey(key)) continue;
            p.put(key, properties.get(key));
        }
        return Collections.unmodifiableMap(p);
    }

    CacheStoreMode getCacheStoreMode(Map<String, Object> properties) {
        if (properties == null) {
            return this.defaultCacheStoreMode;
        }
        return FastSessionServices.determineCacheStoreMode(properties);
    }

    CacheRetrieveMode getCacheRetrieveMode(Map<String, Object> properties) {
        if (properties == null) {
            return this.defaultCacheRetrieveMode;
        }
        return FastSessionServices.determineCacheRetrieveMode(properties);
    }

    private static CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
        CacheRetrieveMode cacheRetrieveMode = (CacheRetrieveMode)settings.get("javax.persistence.cache.retrieveMode");
        if (cacheRetrieveMode == null) {
            return (CacheRetrieveMode)settings.get("jakarta.persistence.cache.retrieveMode");
        }
        return cacheRetrieveMode;
    }

    private static CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
        CacheStoreMode cacheStoreMode = (CacheStoreMode)settings.get("javax.persistence.cache.storeMode");
        if (cacheStoreMode == null) {
            return (CacheStoreMode)settings.get("jakarta.persistence.cache.storeMode");
        }
        return cacheStoreMode;
    }

    public ConnectionObserverStatsBridge getDefaultJdbcObserver() {
        return this.defaultJdbcObservers;
    }

    public void firePostLoadEvent(PostLoadEvent postLoadEvent) {
        this.eventListenerGroup_POST_LOAD.fireEventOnEachListener(postLoadEvent, PostLoadEventListener::onPostLoad);
    }
}

