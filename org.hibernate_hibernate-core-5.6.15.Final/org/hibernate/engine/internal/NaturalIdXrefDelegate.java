/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.AssertionFailure;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class NaturalIdXrefDelegate {
    private static final Logger LOG = Logger.getLogger(NaturalIdXrefDelegate.class);
    private final StatefulPersistenceContext persistenceContext;
    private final ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap();

    public NaturalIdXrefDelegate(StatefulPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    protected SharedSessionContractImplementor session() {
        return this.persistenceContext.getSession();
    }

    public boolean cacheNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
        NaturalIdResolutionCache previousInstance;
        this.validateNaturalId(persister, naturalIdValues);
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister);
        if (entityNaturalIdResolutionCache == null && (previousInstance = this.naturalIdResolutionCacheMap.putIfAbsent(persister, entityNaturalIdResolutionCache = new NaturalIdResolutionCache(persister))) != null) {
            entityNaturalIdResolutionCache = previousInstance;
        }
        return entityNaturalIdResolutionCache.cache(pk, naturalIdValues);
    }

    public Object[] removeNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
        CachedNaturalId cachedNaturalId;
        persister = this.locatePersisterForKey(persister);
        this.validateNaturalId(persister, naturalIdValues);
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister);
        Object[] sessionCachedNaturalIdValues = null;
        if (entityNaturalIdResolutionCache != null && (cachedNaturalId = (CachedNaturalId)entityNaturalIdResolutionCache.pkToNaturalIdMap.remove(pk)) != null) {
            entityNaturalIdResolutionCache.naturalIdToPkMap.remove(cachedNaturalId);
            sessionCachedNaturalIdValues = cachedNaturalId.getValues();
        }
        if (persister.hasNaturalIdCache()) {
            NaturalIdDataAccess naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
            Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(naturalIdValues, persister, this.session());
            naturalIdCacheAccessStrategy.evict(naturalIdCacheKey);
            if (sessionCachedNaturalIdValues != null && !Arrays.equals(sessionCachedNaturalIdValues, naturalIdValues)) {
                Object sessionNaturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(sessionCachedNaturalIdValues, persister, this.session());
                naturalIdCacheAccessStrategy.evict(sessionNaturalIdCacheKey);
            }
        }
        return sessionCachedNaturalIdValues;
    }

    public boolean sameAsCached(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister);
        return entityNaturalIdResolutionCache != null && entityNaturalIdResolutionCache.sameAsCached(pk, naturalIdValues);
    }

    protected EntityPersister locatePersisterForKey(EntityPersister persister) {
        return this.persistenceContext.getSession().getFactory().getEntityPersister(persister.getRootEntityName());
    }

    protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
        if (!persister.hasNaturalIdentifier()) {
            throw new IllegalArgumentException("Entity did not define a natrual-id");
        }
        if (persister.getNaturalIdentifierProperties().length != naturalIdValues.length) {
            throw new IllegalArgumentException("Mismatch between expected number of natural-id values and found.");
        }
    }

    public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister = this.locatePersisterForKey(persister));
        if (entityNaturalIdResolutionCache == null) {
            return null;
        }
        CachedNaturalId cachedNaturalId = (CachedNaturalId)entityNaturalIdResolutionCache.pkToNaturalIdMap.get(pk);
        if (cachedNaturalId == null) {
            return null;
        }
        return cachedNaturalId.getValues();
    }

    public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
        Serializable pk;
        persister = this.locatePersisterForKey(persister);
        this.validateNaturalId(persister, naturalIdValues);
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister);
        CachedNaturalId cachedNaturalId = new CachedNaturalId(persister, naturalIdValues);
        if (entityNaturalIdResolutionCache != null) {
            pk = (Serializable)entityNaturalIdResolutionCache.naturalIdToPkMap.get(cachedNaturalId);
            if (pk != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace((Object)("Resolved natural key -> primary key resolution in session cache: " + persister.getRootEntityName() + "#[" + Arrays.toString(naturalIdValues) + "]"));
                }
                return pk;
            }
            if (entityNaturalIdResolutionCache.containsInvalidNaturalIdReference(naturalIdValues)) {
                return PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE;
            }
        }
        if (!persister.hasNaturalIdCache()) {
            return null;
        }
        NaturalIdDataAccess naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
        SharedSessionContractImplementor session = this.session();
        Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(naturalIdValues, persister, session);
        pk = CacheHelper.fromSharedCache(session, naturalIdCacheKey, naturalIdCacheAccessStrategy);
        SessionFactoryImplementor factory = session.getFactory();
        StatisticsImplementor statistics = factory.getStatistics();
        boolean statisticsEnabled = statistics.isStatisticsEnabled();
        if (pk != null) {
            NaturalIdResolutionCache existingCache;
            if (statisticsEnabled) {
                statistics.naturalIdCacheHit(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
            }
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Found natural key [%s] -> primary key [%s] xref in second-level cache for %s", (Object)Arrays.toString(naturalIdValues), (Object)pk, (Object)persister.getRootEntityName());
            }
            if (entityNaturalIdResolutionCache == null && (existingCache = this.naturalIdResolutionCacheMap.putIfAbsent(persister, entityNaturalIdResolutionCache = new NaturalIdResolutionCache(persister))) != null) {
                entityNaturalIdResolutionCache = existingCache;
            }
            entityNaturalIdResolutionCache.pkToNaturalIdMap.put(pk, cachedNaturalId);
            entityNaturalIdResolutionCache.naturalIdToPkMap.put(cachedNaturalId, pk);
        } else if (statisticsEnabled) {
            statistics.naturalIdCacheMiss(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
        }
        return pk;
    }

    public Collection<Serializable> getCachedPkResolutions(EntityPersister persister) {
        persister = this.locatePersisterForKey(persister);
        Set pks = null;
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister);
        if (entityNaturalIdResolutionCache != null) {
            pks = entityNaturalIdResolutionCache.pkToNaturalIdMap.keySet();
        }
        if (pks == null || pks.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(pks);
    }

    public void stashInvalidNaturalIdReference(EntityPersister persister, Object[] invalidNaturalIdValues) {
        NaturalIdResolutionCache entityNaturalIdResolutionCache = this.naturalIdResolutionCacheMap.get(persister = this.locatePersisterForKey(persister));
        if (entityNaturalIdResolutionCache == null) {
            throw new AssertionFailure("Expecting NaturalIdResolutionCache to exist already for entity " + persister.getEntityName());
        }
        entityNaturalIdResolutionCache.stashInvalidNaturalIdReference(invalidNaturalIdValues);
    }

    public void unStashInvalidNaturalIdReferences() {
        for (NaturalIdResolutionCache naturalIdResolutionCache : this.naturalIdResolutionCacheMap.values()) {
            naturalIdResolutionCache.unStashInvalidNaturalIdReferences();
        }
    }

    public void clear() {
        this.naturalIdResolutionCacheMap.clear();
    }

    private static class NaturalIdResolutionCache
    implements Serializable {
        private final EntityPersister persister;
        private Map<Serializable, CachedNaturalId> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, CachedNaturalId>();
        private Map<CachedNaturalId, Serializable> naturalIdToPkMap = new ConcurrentHashMap<CachedNaturalId, Serializable>();
        private List<CachedNaturalId> invalidNaturalIdList;

        private NaturalIdResolutionCache(EntityPersister persister) {
            this.persister = persister;
        }

        public EntityPersister getPersister() {
            return this.persister;
        }

        public boolean sameAsCached(Serializable pk, Object[] naturalIdValues) {
            if (pk == null) {
                return false;
            }
            CachedNaturalId initial = this.pkToNaturalIdMap.get(pk);
            return initial != null && initial.isSame(naturalIdValues);
        }

        public boolean cache(Serializable pk, Object[] naturalIdValues) {
            if (pk == null) {
                return false;
            }
            CachedNaturalId initial = this.pkToNaturalIdMap.get(pk);
            if (initial != null) {
                if (initial.isSame(naturalIdValues)) {
                    return false;
                }
                this.naturalIdToPkMap.remove(initial);
            }
            CachedNaturalId cachedNaturalId = new CachedNaturalId(this.persister, naturalIdValues);
            this.pkToNaturalIdMap.put(pk, cachedNaturalId);
            this.naturalIdToPkMap.put(cachedNaturalId, pk);
            return true;
        }

        public void stashInvalidNaturalIdReference(Object[] invalidNaturalIdValues) {
            if (this.invalidNaturalIdList == null) {
                this.invalidNaturalIdList = new ArrayList<CachedNaturalId>();
            }
            this.invalidNaturalIdList.add(new CachedNaturalId(this.persister, invalidNaturalIdValues));
        }

        public boolean containsInvalidNaturalIdReference(Object[] naturalIdValues) {
            return this.invalidNaturalIdList != null && this.invalidNaturalIdList.contains(new CachedNaturalId(this.persister, naturalIdValues));
        }

        public void unStashInvalidNaturalIdReferences() {
            if (this.invalidNaturalIdList != null) {
                this.invalidNaturalIdList.clear();
            }
        }
    }

    private static class CachedNaturalId
    implements Serializable {
        private final EntityPersister persister;
        private final Object[] values;
        private final Type[] naturalIdTypes;
        private int hashCode;

        public CachedNaturalId(EntityPersister persister, Object[] values) {
            this.persister = persister;
            this.values = values;
            int prime = 31;
            int hashCodeCalculation = 1;
            hashCodeCalculation = 31 * hashCodeCalculation + persister.hashCode();
            int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
            this.naturalIdTypes = new Type[naturalIdPropertyIndexes.length];
            int i = 0;
            for (int naturalIdPropertyIndex : naturalIdPropertyIndexes) {
                Type type;
                this.naturalIdTypes[i] = type = persister.getPropertyType(persister.getPropertyNames()[naturalIdPropertyIndex]);
                int elementHashCode = values[i] == null ? 0 : type.getHashCode(values[i], persister.getFactory());
                hashCodeCalculation = 31 * hashCodeCalculation + elementHashCode;
                ++i;
            }
            this.hashCode = hashCodeCalculation;
        }

        public Object[] getValues() {
            return this.values;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            CachedNaturalId other = (CachedNaturalId)obj;
            return this.persister.equals(other.persister) && this.isSame(other.values);
        }

        private boolean isSame(Object[] otherValues) {
            for (int i = 0; i < this.naturalIdTypes.length; ++i) {
                if (this.naturalIdTypes[i].isEqual(this.values[i], otherValues[i], this.persister.getFactory())) continue;
                return false;
            }
            return true;
        }
    }
}

