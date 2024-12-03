/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.loading.internal.LoadContexts;
import org.hibernate.engine.spi.BatchFetchQueue;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

public interface PersistenceContext {
    public static final Object NO_ROW = new MarkerObject("NO_ROW");

    public boolean isStateless();

    public SharedSessionContractImplementor getSession();

    public LoadContexts getLoadContexts();

    public void addUnownedCollection(CollectionKey var1, PersistentCollection var2);

    public PersistentCollection useUnownedCollection(CollectionKey var1);

    public BatchFetchQueue getBatchFetchQueue();

    public void clear();

    public boolean hasNonReadOnlyEntities();

    public void setEntryStatus(EntityEntry var1, Status var2);

    public void afterTransactionCompletion();

    public Object[] getDatabaseSnapshot(Serializable var1, EntityPersister var2);

    public Object[] getCachedDatabaseSnapshot(EntityKey var1);

    public Object[] getNaturalIdSnapshot(Serializable var1, EntityPersister var2);

    public void addEntity(EntityKey var1, Object var2);

    public Object getEntity(EntityKey var1);

    public boolean containsEntity(EntityKey var1);

    public Object removeEntity(EntityKey var1);

    public void addEntity(EntityUniqueKey var1, Object var2);

    public Object getEntity(EntityUniqueKey var1);

    public EntityEntry getEntry(Object var1);

    public EntityEntry removeEntry(Object var1);

    public boolean isEntryFor(Object var1);

    public CollectionEntry getCollectionEntry(PersistentCollection var1);

    public EntityEntry addEntity(Object var1, Status var2, Object[] var3, EntityKey var4, Object var5, LockMode var6, boolean var7, EntityPersister var8, boolean var9);

    public EntityEntry addEntry(Object var1, Status var2, Object[] var3, Object var4, Serializable var5, Object var6, LockMode var7, boolean var8, EntityPersister var9, boolean var10);

    public boolean containsCollection(PersistentCollection var1);

    public boolean containsProxy(Object var1);

    public boolean reassociateIfUninitializedProxy(Object var1) throws MappingException;

    public void reassociateProxy(Object var1, Serializable var2) throws MappingException;

    public Object unproxy(Object var1) throws HibernateException;

    public Object unproxyAndReassociate(Object var1) throws HibernateException;

    public void checkUniqueness(EntityKey var1, Object var2) throws HibernateException;

    public Object narrowProxy(Object var1, EntityPersister var2, EntityKey var3, Object var4) throws HibernateException;

    public Object proxyFor(EntityPersister var1, EntityKey var2, Object var3) throws HibernateException;

    public Object proxyFor(Object var1) throws HibernateException;

    public void addEnhancedProxy(EntityKey var1, PersistentAttributeInterceptable var2);

    public Object getCollectionOwner(Serializable var1, CollectionPersister var2) throws MappingException;

    public Object getLoadedCollectionOwnerOrNull(PersistentCollection var1);

    public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection var1);

    public void addUninitializedCollection(CollectionPersister var1, PersistentCollection var2, Serializable var3);

    public void addUninitializedDetachedCollection(CollectionPersister var1, PersistentCollection var2);

    public void addNewCollection(CollectionPersister var1, PersistentCollection var2) throws HibernateException;

    public void addInitializedDetachedCollection(CollectionPersister var1, PersistentCollection var2) throws HibernateException;

    public CollectionEntry addInitializedCollection(CollectionPersister var1, PersistentCollection var2, Serializable var3) throws HibernateException;

    public PersistentCollection getCollection(CollectionKey var1);

    public void addNonLazyCollection(PersistentCollection var1);

    public void initializeNonLazyCollections() throws HibernateException;

    public PersistentCollection getCollectionHolder(Object var1);

    public void addCollectionHolder(PersistentCollection var1);

    public PersistentCollection removeCollectionHolder(Object var1);

    public Serializable getSnapshot(PersistentCollection var1);

    public CollectionEntry getCollectionEntryOrNull(Object var1);

    public Object getProxy(EntityKey var1);

    public void addProxy(EntityKey var1, Object var2);

    public Object removeProxy(EntityKey var1);

    @Deprecated
    public HashSet getNullifiableEntityKeys();

    @Deprecated
    public Map getEntitiesByKey();

    public Map.Entry<Object, EntityEntry>[] reentrantSafeEntityEntries();

    @Deprecated
    public Map getEntityEntries();

    public int getNumberOfManagedEntities();

    @Deprecated
    public Map getCollectionEntries();

    public void forEachCollectionEntry(BiConsumer<PersistentCollection, CollectionEntry> var1, boolean var2);

    @Deprecated
    public Map getCollectionsByKey();

    public int getCascadeLevel();

    public int incrementCascadeLevel();

    public int decrementCascadeLevel();

    public boolean isFlushing();

    public void setFlushing(boolean var1);

    public void beforeLoad();

    public void afterLoad();

    public boolean isLoadFinished();

    public String toString();

    public Serializable getOwnerId(String var1, String var2, Object var3, Map var4);

    public Object getIndexInOwner(String var1, String var2, Object var3, Map var4);

    public void addNullProperty(EntityKey var1, String var2);

    public boolean isPropertyNull(EntityKey var1, String var2);

    public boolean isDefaultReadOnly();

    public void setDefaultReadOnly(boolean var1);

    public boolean isReadOnly(Object var1);

    public void setReadOnly(Object var1, boolean var2);

    public void replaceDelayedEntityIdentityInsertKeys(EntityKey var1, Serializable var2);

    public void addChildParent(Object var1, Object var2);

    public void removeChildParent(Object var1);

    public void registerInsertedKey(EntityPersister var1, Serializable var2);

    public boolean wasInsertedDuringTransaction(EntityPersister var1, Serializable var2);

    public boolean containsNullifiableEntityKey(Supplier<EntityKey> var1);

    public void registerNullifiableEntityKey(EntityKey var1);

    public boolean isNullifiableEntityKeysEmpty();

    public int getCollectionEntriesSize();

    public CollectionEntry removeCollectionEntry(PersistentCollection var1);

    public void clearCollectionsByKey();

    public PersistentCollection addCollectionByKey(CollectionKey var1, PersistentCollection var2);

    public void removeCollectionByKey(CollectionKey var1);

    public Iterator managedEntitiesIterator();

    public NaturalIdHelper getNaturalIdHelper();

    public static interface NaturalIdHelper {
        public static final Serializable INVALID_NATURAL_ID_REFERENCE = new Serializable(){};

        public Object[] extractNaturalIdValues(Object[] var1, EntityPersister var2);

        public Object[] extractNaturalIdValues(Object var1, EntityPersister var2);

        public void cacheNaturalIdCrossReferenceFromLoad(EntityPersister var1, Serializable var2, Object[] var3);

        public void manageLocalNaturalIdCrossReference(EntityPersister var1, Serializable var2, Object[] var3, Object[] var4, CachedNaturalIdValueSource var5);

        public Object[] removeLocalNaturalIdCrossReference(EntityPersister var1, Serializable var2, Object[] var3);

        public void manageSharedNaturalIdCrossReference(EntityPersister var1, Serializable var2, Object[] var3, Object[] var4, CachedNaturalIdValueSource var5);

        public void removeSharedNaturalIdCrossReference(EntityPersister var1, Serializable var2, Object[] var3);

        public Object[] findCachedNaturalId(EntityPersister var1, Serializable var2);

        public Serializable findCachedNaturalIdResolution(EntityPersister var1, Object[] var2);

        public Collection<Serializable> getCachedPkResolutions(EntityPersister var1);

        public void handleSynchronization(EntityPersister var1, Serializable var2, Object var3);

        public void cleanupFromSynchronizations();

        public void handleEviction(Object var1, EntityPersister var2, Serializable var3);
    }
}

