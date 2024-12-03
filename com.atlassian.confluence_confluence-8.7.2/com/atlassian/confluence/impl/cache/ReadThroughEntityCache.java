/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadThroughEntityCache<CACHEKEY extends Serializable, ENTITYID extends Serializable, ENTITY>
implements ReadThroughCache<CACHEKEY, ENTITY> {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughEntityCache.class);
    private final ReadThroughCache<CACHEKEY, ENTITYID> backingCache;
    private final Function<ENTITYID, ENTITY> entityLoader;
    private final Function<ENTITY, ENTITYID> idExtractor;

    public static <E extends ConfluenceEntityObject, K extends Serializable> ReadThroughCache<K, E> forConfluenceEntityObjects(ReadThroughCache<K, Long> cache, Function<Long, E> entityLoader) {
        return new ReadThroughEntityCache<K, Long, ConfluenceEntityObject>(cache, entityLoader, EntityObject::getId);
    }

    public ReadThroughEntityCache(ReadThroughCache<CACHEKEY, ENTITYID> backingCache, Function<ENTITYID, ENTITY> entityLoader, Function<ENTITY, ENTITYID> idExtractor) {
        this.backingCache = backingCache;
        this.entityLoader = entityLoader;
        this.idExtractor = idExtractor;
    }

    @Override
    public ENTITY get(CACHEKEY cacheKey, Supplier<ENTITY> delegateLoader, Predicate<ENTITY> entityTester) {
        return Optional.ofNullable(this.backingCache.get(cacheKey, () -> this.retrieveEntityIdFromDelegate(cacheKey, delegateLoader), (V i) -> this.isValidEntity(i, entityTester))).map(this.entityLoader).orElse(null);
    }

    private boolean isValidEntity(ENTITYID entityId, Predicate<ENTITY> entityTester) {
        ENTITY entity = this.entityLoader.apply(entityId);
        return entity != null && entityTester.test(entity);
    }

    @Nullable
    private ENTITYID retrieveEntityIdFromDelegate(CACHEKEY cacheKey, Supplier<ENTITY> delegateLoader) {
        log.debug("No entity ID cached for key '{}', loading from delegate", cacheKey);
        ENTITY loadedEntity = delegateLoader.get();
        if (loadedEntity == null) {
            log.debug("Delegate returned no entity for key '{}'", cacheKey);
            return null;
        }
        return (ENTITYID)((Serializable)this.idExtractor.apply(loadedEntity));
    }

    @Override
    public Map<CACHEKEY, ENTITY> getBulk(Set<CACHEKEY> keys, Function<Set<CACHEKEY>, Map<CACHEKEY, ENTITY>> entityBulkLoader) {
        Map<CACHEKEY, ENTITYID> entityIds = this.backingCache.getBulk(keys, this.getEntityIdBulkLoader(entityBulkLoader));
        return ImmutableMap.copyOf((Map)Maps.filterValues((Map)Maps.transformValues(entityIds, this.entityLoader::apply), Objects::nonNull));
    }

    private Function<Set<CACHEKEY>, Map<CACHEKEY, ENTITYID>> getEntityIdBulkLoader(Function<Set<CACHEKEY>, Map<CACHEKEY, ENTITY>> entityBulkLoader) {
        return keysToLoad -> ImmutableMap.copyOf((Map)Maps.transformValues((Map)((Map)entityBulkLoader.apply((Set<CACHEKEY>)keysToLoad)), this.idExtractor::apply));
    }

    @Override
    public void remove(CACHEKEY cacheKey) {
        this.backingCache.remove(cacheKey);
    }

    @Override
    public void removeAll() {
        this.backingCache.removeAll();
    }
}

