/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.loading.internal;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.entry.CollectionCacheEntry;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.loading.internal.LoadContexts;
import org.hibernate.engine.loading.internal.LoadingCollectionEntry;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;

public class CollectionLoadContext {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(CollectionLoadContext.class);
    private final LoadContexts loadContexts;
    private final ResultSet resultSet;
    private Set<CollectionKey> localLoadingCollectionKeys = new HashSet<CollectionKey>();

    public CollectionLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
        this.loadContexts = loadContexts;
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return this.resultSet;
    }

    public LoadContexts getLoadContext() {
        return this.loadContexts;
    }

    public PersistentCollection getLoadingCollection(CollectionPersister persister, Serializable key) {
        LoadingCollectionEntry loadingCollectionEntry;
        CollectionKey collectionKey = new CollectionKey(persister, key);
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Starting attempt to find loading collection [{0}]", MessageHelper.collectionInfoString(persister.getRole(), key));
        }
        if ((loadingCollectionEntry = this.loadContexts.locateLoadingCollectionEntry(collectionKey)) == null) {
            PersistentCollection collection = this.loadContexts.getPersistenceContext().getCollection(collectionKey);
            if (collection != null) {
                if (collection.wasInitialized()) {
                    LOG.trace("Collection already initialized; ignoring");
                    return null;
                }
                LOG.trace("Collection not yet initialized; initializing");
            } else {
                boolean newlySavedEntity;
                Object owner = this.loadContexts.getPersistenceContext().getCollectionOwner(key, persister);
                boolean bl = newlySavedEntity = owner != null && this.loadContexts.getPersistenceContext().getEntry(owner).getStatus() != Status.LOADING;
                if (newlySavedEntity) {
                    LOG.trace("Owning entity already loaded; ignoring");
                    return null;
                }
                LOG.tracev("Instantiating new collection [key={0}, rs={1}]", key, this.resultSet);
                collection = persister.getCollectionType().instantiate(this.loadContexts.getPersistenceContext().getSession(), persister, key);
            }
            collection.beforeInitialize(persister, -1);
            collection.beginRead();
            this.localLoadingCollectionKeys.add(collectionKey);
            this.loadContexts.registerLoadingCollectionXRef(collectionKey, new LoadingCollectionEntry(this.resultSet, persister, key, collection));
            return collection;
        }
        if (loadingCollectionEntry.getResultSet() == this.resultSet) {
            LOG.trace("Found loading collection bound to current result set processing; reading row");
            return loadingCollectionEntry.getCollection();
        }
        LOG.trace("Collection is already being initialized; ignoring row");
        return null;
    }

    public void endLoadingCollections(CollectionPersister persister) {
        SharedSessionContractImplementor session = this.getLoadContext().getPersistenceContext().getSession();
        if (!this.loadContexts.hasLoadingCollectionEntries() && this.localLoadingCollectionKeys.isEmpty()) {
            return;
        }
        ArrayList<LoadingCollectionEntry> matches = null;
        Iterator<CollectionKey> itr = this.localLoadingCollectionKeys.iterator();
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        while (itr.hasNext()) {
            CollectionKey collectionKey = itr.next();
            LoadingCollectionEntry lce = this.loadContexts.locateLoadingCollectionEntry(collectionKey);
            if (lce == null) {
                LOG.loadingCollectionKeyNotFound(collectionKey);
                continue;
            }
            if (lce.getResultSet() != this.resultSet || lce.getPersister() != persister) continue;
            if (matches == null) {
                matches = new ArrayList<LoadingCollectionEntry>();
            }
            matches.add(lce);
            if (lce.getCollection().getOwner() == null) {
                persistenceContext.addUnownedCollection(new CollectionKey(persister, lce.getKey()), lce.getCollection());
            }
            LOG.tracev("Removing collection load entry [{0}]", lce);
            this.loadContexts.unregisterLoadingCollectionXRef(collectionKey);
            itr.remove();
        }
        this.endLoadingCollections(persister, matches);
        if (this.localLoadingCollectionKeys.isEmpty()) {
            this.loadContexts.cleanup(this.resultSet);
        }
    }

    private void endLoadingCollections(CollectionPersister persister, List<LoadingCollectionEntry> matchedCollectionEntries) {
        if (matchedCollectionEntries == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("No collections were found in result set for role: %s", persister.getRole());
            }
            return;
        }
        int count = matchedCollectionEntries.size();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
        }
        for (LoadingCollectionEntry matchedCollectionEntry : matchedCollectionEntries) {
            this.endLoadingCollection(matchedCollectionEntry, persister);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("%s collections initialized for role: %s", count, persister.getRole());
        }
    }

    private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
        StatisticsImplementor statistics;
        boolean addToCache;
        EntityPersister ownerEntityPersister;
        BytecodeEnhancementMetadata bytecodeEnhancementMetadata;
        CollectionEntry ce;
        LOG.tracev("Ending loading collection [{0}]", lce);
        PersistenceContext persistenceContext = this.getLoadContext().getPersistenceContext();
        SharedSessionContractImplementor session = persistenceContext.getSession();
        PersistentCollection loadingCollection = lce.getCollection();
        boolean hasNoQueuedAdds = loadingCollection.endRead();
        if (persister.getCollectionType().hasHolder()) {
            persistenceContext.addCollectionHolder(loadingCollection);
        }
        if ((ce = persistenceContext.getCollectionEntry(loadingCollection)) == null) {
            ce = persistenceContext.addInitializedCollection(persister, loadingCollection, lce.getKey());
        } else {
            ce.postInitialize(loadingCollection);
        }
        if (loadingCollection.getOwner() != null && (bytecodeEnhancementMetadata = (ownerEntityPersister = persister.getOwnerEntityPersister()).getBytecodeEnhancementMetadata()).isEnhancedForLazyLoading() && StringHelper.qualifier(persister.getRole()).length() == ownerEntityPersister.getEntityName().length()) {
            String propertyName = persister.getRole().substring(ownerEntityPersister.getEntityName().length() + 1);
            if (!bytecodeEnhancementMetadata.isAttributeLoaded(loadingCollection.getOwner(), propertyName)) {
                int propertyIndex = ownerEntityPersister.getEntityMetamodel().getPropertyIndex(propertyName);
                ownerEntityPersister.setPropertyValue(loadingCollection.getOwner(), propertyIndex, loadingCollection);
            }
        }
        boolean bl = addToCache = hasNoQueuedAdds && persister.hasCache() && session.getCacheMode().isPutEnabled() && !ce.isDoremove();
        if (addToCache) {
            this.addCollectionToCache(lce, persister);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Collection fully initialized: %s", MessageHelper.collectionInfoString(persister, loadingCollection, lce.getKey(), session));
        }
        if ((statistics = session.getFactory().getStatistics()).isStatisticsEnabled()) {
            statistics.loadCollection(persister.getRole());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addCollectionToCache(LoadingCollectionEntry lce, CollectionPersister persister) {
        Object version;
        PersistenceContext persistenceContext = this.getLoadContext().getPersistenceContext();
        SharedSessionContractImplementor session = persistenceContext.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Caching collection: %s", MessageHelper.collectionInfoString(persister, lce.getCollection(), lce.getKey(), session));
        }
        if (session.getLoadQueryInfluencers().hasEnabledFilters() && persister.isAffectedByEnabledFilters(session)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Refusing to add to cache due to enabled filters");
            }
            return;
        }
        if (persister.isVersioned()) {
            Object collectionOwner = persistenceContext.getCollectionOwner(lce.getKey(), persister);
            if (collectionOwner == null) {
                Object linkedOwner;
                if (lce.getCollection() != null && (linkedOwner = lce.getCollection().getOwner()) != null) {
                    Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier(linkedOwner, session);
                    collectionOwner = persistenceContext.getCollectionOwner(ownerKey, persister);
                }
                if (collectionOwner == null) {
                    throw new HibernateException("Unable to resolve owner of loading collection [" + MessageHelper.collectionInfoString(persister, lce.getCollection(), lce.getKey(), session) + "] for second level caching");
                }
            }
            version = persistenceContext.getEntry(collectionOwner).getVersion();
        } else {
            version = null;
        }
        CollectionCacheEntry entry = new CollectionCacheEntry(lce.getCollection(), persister);
        CollectionDataAccess cacheAccess = persister.getCacheAccessStrategy();
        Object cacheKey = cacheAccess.generateCacheKey(lce.getKey(), persister, session.getFactory(), session.getTenantIdentifier());
        boolean isPutFromLoad = true;
        if (persister.getElementType().isAssociationType()) {
            for (Serializable id : entry.getState()) {
                EntityPersister entityPersister = ((QueryableCollection)persister).getElementPersister();
                if (!persistenceContext.wasInsertedDuringTransaction(entityPersister, id)) continue;
                isPutFromLoad = false;
                break;
            }
        }
        if (isPutFromLoad) {
            SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
            try {
                eventListenerManager.cachePutStart();
                boolean put = cacheAccess.putFromLoad(session, cacheKey, persister.getCacheEntryStructure().structure(entry), version, factory.getSessionFactoryOptions().isMinimalPutsEnabled() && session.getCacheMode() != CacheMode.REFRESH);
                StatisticsImplementor statistics = factory.getStatistics();
                if (put && statistics.isStatisticsEnabled()) {
                    statistics.collectionCachePut(persister.getNavigableRole(), persister.getCacheAccessStrategy().getRegion().getName());
                }
            }
            finally {
                eventListenerManager.cachePutEnd();
            }
        }
    }

    void cleanup() {
        if (!this.localLoadingCollectionKeys.isEmpty()) {
            LOG.localLoadingCollectionKeysCount(this.localLoadingCollectionKeys.size());
        }
        this.loadContexts.cleanupCollectionXRefs(this.localLoadingCollectionKeys);
        this.localLoadingCollectionKeys.clear();
    }

    public String toString() {
        return super.toString() + "<rs=" + this.resultSet + ">";
    }
}

