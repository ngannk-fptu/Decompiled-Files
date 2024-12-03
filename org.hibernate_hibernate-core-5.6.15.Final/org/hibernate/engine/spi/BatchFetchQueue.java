/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.SubselectFetch;
import org.hibernate.internal.CoreLogging;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public class BatchFetchQueue {
    private static final Logger LOG = CoreLogging.logger(BatchFetchQueue.class);
    private final PersistenceContext context;
    private Map<EntityKey, SubselectFetch> subselectsByEntityKey;
    private Map<String, LinkedHashSet<EntityKey>> batchLoadableEntityKeys;
    private Map<String, LinkedHashMap<CollectionEntry, PersistentCollection>> batchLoadableCollections;

    public BatchFetchQueue(PersistenceContext context) {
        this.context = context;
    }

    public void clear() {
        this.batchLoadableEntityKeys = null;
        this.batchLoadableCollections = null;
        this.subselectsByEntityKey = null;
    }

    public SubselectFetch getSubselect(EntityKey key) {
        if (this.subselectsByEntityKey == null) {
            return null;
        }
        return this.subselectsByEntityKey.get(key);
    }

    public void addSubselect(EntityKey key, SubselectFetch subquery) {
        if (this.subselectsByEntityKey == null) {
            this.subselectsByEntityKey = new HashMap<EntityKey, SubselectFetch>(12);
        }
        this.subselectsByEntityKey.put(key, subquery);
    }

    public void removeSubselect(EntityKey key) {
        if (this.subselectsByEntityKey != null) {
            this.subselectsByEntityKey.remove(key);
        }
    }

    public void addBatchLoadableEntityKey(EntityKey key) {
        if (key.isBatchLoadable()) {
            if (this.batchLoadableEntityKeys == null) {
                this.batchLoadableEntityKeys = new HashMap<String, LinkedHashSet<EntityKey>>(12);
            }
            LinkedHashSet keysForEntity = this.batchLoadableEntityKeys.computeIfAbsent(key.getEntityName(), k -> new LinkedHashSet(8));
            keysForEntity.add(key);
        }
    }

    public void removeBatchLoadableEntityKey(EntityKey key) {
        LinkedHashSet<EntityKey> set;
        if (this.batchLoadableEntityKeys != null && key.isBatchLoadable() && (set = this.batchLoadableEntityKeys.get(key.getEntityName())) != null) {
            set.remove(key);
        }
    }

    public boolean containsEntityKey(EntityKey key) {
        LinkedHashSet<EntityKey> set;
        if (this.batchLoadableEntityKeys != null && key.isBatchLoadable() && (set = this.batchLoadableEntityKeys.get(key.getEntityName())) != null) {
            return set.contains(key);
        }
        return false;
    }

    public Serializable[] getEntityBatch(EntityPersister persister, Serializable id, int batchSize, EntityMode entityMode) {
        Serializable[] ids = new Serializable[batchSize];
        ids[0] = id;
        if (this.batchLoadableEntityKeys == null) {
            return ids;
        }
        int i = 1;
        int end = -1;
        boolean checkForEnd = false;
        LinkedHashSet<EntityKey> set = this.batchLoadableEntityKeys.get(persister.getEntityName());
        if (set != null) {
            for (EntityKey key : set) {
                if (checkForEnd && i == end) {
                    return ids;
                }
                if (persister.getIdentifierType().isEqual(id, key.getIdentifier())) {
                    end = i;
                } else if (!this.isCached(key, persister)) {
                    ids[i++] = key.getIdentifier();
                }
                if (i != batchSize) continue;
                i = 1;
                if (end == -1) continue;
                checkForEnd = true;
            }
        }
        return ids;
    }

    private boolean isCached(EntityKey entityKey, EntityPersister persister) {
        SharedSessionContractImplementor session = this.context.getSession();
        if (this.context.getSession().getCacheMode().isGetEnabled() && persister.canReadFromCache()) {
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            Object key = cache.generateCacheKey(entityKey.getIdentifier(), persister, session.getFactory(), session.getTenantIdentifier());
            return CacheHelper.fromSharedCache(session, key, cache) != null;
        }
        return false;
    }

    public void addBatchLoadableCollection(PersistentCollection collection, CollectionEntry ce) {
        CollectionPersister persister = ce.getLoadedPersister();
        if (this.batchLoadableCollections == null) {
            this.batchLoadableCollections = new HashMap<String, LinkedHashMap<CollectionEntry, PersistentCollection>>(12);
        }
        LinkedHashMap map = this.batchLoadableCollections.computeIfAbsent(persister.getRole(), k -> new LinkedHashMap(16));
        map.put(ce, collection);
    }

    public void removeBatchLoadableCollection(CollectionEntry ce) {
        if (this.batchLoadableCollections == null) {
            return;
        }
        LinkedHashMap<CollectionEntry, PersistentCollection> map = this.batchLoadableCollections.get(ce.getLoadedPersister().getRole());
        if (map != null) {
            map.remove(ce);
        }
    }

    public Serializable[] getCollectionBatch(CollectionPersister collectionPersister, Serializable id, int batchSize) {
        Serializable[] keys = new Serializable[batchSize];
        keys[0] = id;
        if (this.batchLoadableCollections == null) {
            return keys;
        }
        int i = 1;
        int end = -1;
        boolean checkForEnd = false;
        LinkedHashMap<CollectionEntry, PersistentCollection> map = this.batchLoadableCollections.get(collectionPersister.getRole());
        if (map != null) {
            for (Map.Entry<CollectionEntry, PersistentCollection> me : map.entrySet()) {
                CollectionEntry ce = me.getKey();
                PersistentCollection collection = me.getValue();
                if (ce.getLoadedKey() == null) continue;
                if (collection.wasInitialized()) {
                    LOG.warn((Object)"Encountered initialized collection in BatchFetchQueue, this should not happen.");
                    continue;
                }
                if (checkForEnd && i == end) {
                    return keys;
                }
                boolean isEqual = collectionPersister.getKeyType().isEqual(id, ce.getLoadedKey(), collectionPersister.getFactory());
                if (isEqual) {
                    end = i;
                } else if (!this.isCached(ce.getLoadedKey(), collectionPersister)) {
                    keys[i++] = ce.getLoadedKey();
                }
                if (i != batchSize) continue;
                i = 1;
                if (end == -1) continue;
                checkForEnd = true;
            }
        }
        return keys;
    }

    private boolean isCached(Serializable collectionKey, CollectionPersister persister) {
        SharedSessionContractImplementor session = this.context.getSession();
        if (session.getCacheMode().isGetEnabled() && persister.hasCache()) {
            CollectionDataAccess cache = persister.getCacheAccessStrategy();
            Object cacheKey = cache.generateCacheKey(collectionKey, persister, session.getFactory(), session.getTenantIdentifier());
            return CacheHelper.fromSharedCache(session, cacheKey, cache) != null;
        }
        return false;
    }
}

