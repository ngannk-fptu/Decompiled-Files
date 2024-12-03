/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.entry.CollectionCacheEntry;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.InitializeCollectionEvent;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;

public class DefaultInitializeCollectionEventListener
implements InitializeCollectionEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultInitializeCollectionEventListener.class);

    @Override
    public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
        PersistentCollection collection = event.getCollection();
        EventSource source = event.getSession();
        CollectionEntry ce = source.getPersistenceContextInternal().getCollectionEntry(collection);
        if (ce == null) {
            throw new HibernateException("collection was evicted");
        }
        if (!collection.wasInitialized()) {
            boolean foundInCache;
            CollectionPersister ceLoadedPersister = ce.getLoadedPersister();
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Initializing collection {0}", MessageHelper.collectionInfoString(ceLoadedPersister, collection, ce.getLoadedKey(), source));
                LOG.trace("Checking second-level cache");
            }
            if (foundInCache = this.initializeCollectionFromCache(ce.getLoadedKey(), ceLoadedPersister, collection, source)) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Collection initialized from cache");
                }
            } else {
                StatisticsImplementor statistics;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Collection not cached");
                }
                ceLoadedPersister.initialize(ce.getLoadedKey(), source);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Collection initialized");
                }
                if ((statistics = source.getFactory().getStatistics()).isStatisticsEnabled()) {
                    statistics.fetchCollection(ceLoadedPersister.getRole());
                }
            }
        }
    }

    private boolean initializeCollectionFromCache(Serializable id, CollectionPersister persister, PersistentCollection collection, SessionImplementor source) {
        boolean useCache;
        if (source.getLoadQueryInfluencers().hasEnabledFilters() && persister.isAffectedByEnabledFilters(source)) {
            LOG.trace("Disregarding cached version (if any) of collection due to enabled filters");
            return false;
        }
        boolean bl = useCache = persister.hasCache() && source.getCacheMode().isGetEnabled();
        if (!useCache) {
            return false;
        }
        SessionFactoryImplementor factory = source.getFactory();
        CollectionDataAccess cacheAccessStrategy = persister.getCacheAccessStrategy();
        Object ck = cacheAccessStrategy.generateCacheKey(id, persister, factory, source.getTenantIdentifier());
        Serializable ce = CacheHelper.fromSharedCache(source, ck, cacheAccessStrategy);
        StatisticsImplementor statistics = factory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            if (ce == null) {
                statistics.collectionCacheMiss(persister.getNavigableRole(), cacheAccessStrategy.getRegion().getName());
            } else {
                statistics.collectionCacheHit(persister.getNavigableRole(), cacheAccessStrategy.getRegion().getName());
            }
        }
        if (ce == null) {
            return false;
        }
        CollectionCacheEntry cacheEntry = (CollectionCacheEntry)persister.getCacheEntryStructure().destructure(ce, factory);
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        cacheEntry.assemble(collection, persister, persistenceContext.getCollectionOwner(id, persister));
        persistenceContext.getCollectionEntry(collection).postInitialize(collection);
        return true;
    }
}

