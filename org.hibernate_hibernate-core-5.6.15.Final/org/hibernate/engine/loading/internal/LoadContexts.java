/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.loading.internal;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.loading.internal.CollectionLoadContext;
import org.hibernate.engine.loading.internal.EntityLoadContext;
import org.hibernate.engine.loading.internal.LoadingCollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;

public class LoadContexts {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(LoadContexts.class);
    private final PersistenceContext persistenceContext;
    private Map<ResultSet, CollectionLoadContext> collectionLoadContexts;
    private Map<ResultSet, EntityLoadContext> entityLoadContexts;
    private Map<CollectionKey, LoadingCollectionEntry> xrefLoadingCollectionEntries;

    public LoadContexts(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public PersistenceContext getPersistenceContext() {
        return this.persistenceContext;
    }

    private SharedSessionContractImplementor getSession() {
        return this.getPersistenceContext().getSession();
    }

    public void cleanup(ResultSet resultSet) {
        EntityLoadContext entityLoadContext;
        CollectionLoadContext collectionLoadContext;
        if (this.collectionLoadContexts != null && (collectionLoadContext = this.collectionLoadContexts.remove(resultSet)) != null) {
            collectionLoadContext.cleanup();
        }
        if (this.entityLoadContexts != null && (entityLoadContext = this.entityLoadContexts.remove(resultSet)) != null) {
            entityLoadContext.cleanup();
        }
    }

    public void cleanup() {
        if (this.collectionLoadContexts != null) {
            for (CollectionLoadContext collectionLoadContext : this.collectionLoadContexts.values()) {
                LOG.failSafeCollectionsCleanup(collectionLoadContext);
                collectionLoadContext.cleanup();
            }
            this.collectionLoadContexts.clear();
        }
        if (this.entityLoadContexts != null) {
            for (EntityLoadContext entityLoadContext : this.entityLoadContexts.values()) {
                LOG.failSafeEntitiesCleanup(entityLoadContext);
                entityLoadContext.cleanup();
            }
            this.entityLoadContexts.clear();
        }
    }

    public boolean hasLoadingCollectionEntries() {
        return this.collectionLoadContexts != null && !this.collectionLoadContexts.isEmpty();
    }

    public boolean hasRegisteredLoadingCollectionEntries() {
        return this.xrefLoadingCollectionEntries != null && !this.xrefLoadingCollectionEntries.isEmpty();
    }

    public CollectionLoadContext getCollectionLoadContext(ResultSet resultSet) {
        CollectionLoadContext context = null;
        if (this.collectionLoadContexts == null) {
            this.collectionLoadContexts = new IdentityHashMap<ResultSet, CollectionLoadContext>(8);
        } else {
            context = this.collectionLoadContexts.get(resultSet);
        }
        if (context == null) {
            LOG.tracev("Constructing collection load context for result set [{0}]", resultSet);
            context = new CollectionLoadContext(this, resultSet);
            this.collectionLoadContexts.put(resultSet, context);
        }
        return context;
    }

    public PersistentCollection locateLoadingCollection(CollectionPersister persister, CollectionKey key) {
        LoadingCollectionEntry lce = this.locateLoadingCollectionEntry(key);
        if (lce != null) {
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Returning loading collection: %s", MessageHelper.collectionInfoString(persister, key.getKey(), this.getSession().getFactory()));
            }
            return lce.getCollection();
        }
        return null;
    }

    void registerLoadingCollectionXRef(CollectionKey entryKey, LoadingCollectionEntry entry) {
        if (this.xrefLoadingCollectionEntries == null) {
            this.xrefLoadingCollectionEntries = new HashMap<CollectionKey, LoadingCollectionEntry>();
        }
        this.xrefLoadingCollectionEntries.put(entryKey, entry);
    }

    void unregisterLoadingCollectionXRef(CollectionKey key) {
        if (!this.hasRegisteredLoadingCollectionEntries()) {
            return;
        }
        this.xrefLoadingCollectionEntries.remove(key);
    }

    Map getLoadingCollectionXRefs() {
        return this.xrefLoadingCollectionEntries;
    }

    LoadingCollectionEntry locateLoadingCollectionEntry(CollectionKey key) {
        if (this.xrefLoadingCollectionEntries == null) {
            return null;
        }
        LOG.tracev("Attempting to locate loading collection entry [{0}] in any result-set context", key);
        LoadingCollectionEntry rtn = this.xrefLoadingCollectionEntries.get(key);
        if (rtn == null) {
            LOG.tracev("Collection [{0}] not located in load context", key);
        } else {
            LOG.tracev("Collection [{0}] located in load context", key);
        }
        return rtn;
    }

    void cleanupCollectionXRefs(Set<CollectionKey> entryKeys) {
        for (CollectionKey entryKey : entryKeys) {
            this.xrefLoadingCollectionEntries.remove(entryKey);
        }
    }

    public EntityLoadContext getEntityLoadContext(ResultSet resultSet) {
        EntityLoadContext context = null;
        if (this.entityLoadContexts == null) {
            this.entityLoadContexts = new IdentityHashMap<ResultSet, EntityLoadContext>(8);
        } else {
            context = this.entityLoadContexts.get(resultSet);
        }
        if (context == null) {
            context = new EntityLoadContext(this, resultSet);
            this.entityLoadContexts.put(resultSet, context);
        }
        return context;
    }
}

