/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  javax.persistence.EntityManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SubCache;
import com.atlassian.confluence.plugins.gatekeeper.model.comparator.SpaceComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinySpaceEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.util.CopyOnceMap;
import com.atlassian.confluence.plugins.gatekeeper.util.QueryByIdBatcher;
import com.atlassian.confluence.spaces.SpaceStatus;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SpaceCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(SpaceCache.class);
    private static final String SPACE_TABLE_QUERY_INITIAL = "SELECT id, key, name, spaceStatus FROM Space ORDER BY id";
    private static final String SPACE_TABLE_QUERY_BATCHED = "SELECT id, key, name, spaceStatus FROM Space WHERE id > :id ORDER BY id";
    private List<TinySpace> spaces;
    private Map<String, TinySpace> spaceMap;
    private CopyOnceMap<TinySpace> updateMap;

    SpaceCache(Map<String, TinySpace> spaceMap) {
        this.spaceMap = spaceMap;
        this.updateMap = new CopyOnceMap<TinySpace>(spaceMap);
    }

    SpaceCache(SpaceCache spaceCache) {
        this(spaceCache.spaceMap);
        this.spaces = spaceCache.spaces;
    }

    SpaceCache(EntityManager entityManager) {
        this.spaceMap = new Object2ObjectOpenHashMap();
        logger.trace("Reading space info: started");
        this.initializeCacheInBatchQueries(entityManager);
    }

    private void initializeCacheInBatchQueries(EntityManager entityManager) {
        List<Object[]> queryResults;
        int counter = 0;
        QueryByIdBatcher queryByIdBatcher = new QueryByIdBatcher(entityManager, SPACE_TABLE_QUERY_INITIAL, SPACE_TABLE_QUERY_BATCHED);
        while (!(queryResults = queryByIdBatcher.getBatch()).isEmpty()) {
            for (Object[] row : queryResults) {
                String key = (String)row[1];
                String name = (String)row[2];
                SpaceStatus spaceStatus = (SpaceStatus)row[3];
                this.spaceMap.put(key, new TinySpace(key, name, spaceStatus == SpaceStatus.CURRENT));
                logger.trace("Reading space info: processing space {}", (Object)key);
                ++counter;
            }
            logger.trace("Finished reading one batch - Reading space info: processed {} spaces", (Object)counter);
            if (queryResults.size() >= queryByIdBatcher.getBatchSize()) continue;
        }
        this.updateMap = new CopyOnceMap<TinySpace>(this.spaceMap);
        logger.trace("Reading space info: processed {} spaces", (Object)counter);
    }

    @Override
    @VisibleForTesting
    public void update(TinyEvent event) {
        if (!(event instanceof TinySpaceEvent)) {
            logger.warn("The event is not a TinySpaceEvent. Event type is {}", (Object)event.getEventType().name());
            return;
        }
        TinySpaceEvent tinySpaceEvent = (TinySpaceEvent)event;
        String spaceKey = tinySpaceEvent.getKey();
        switch (event.getEventType()) {
            case SPACE_ADDED: 
            case SPACE_UPDATED: {
                TinySpace space = this.updateMap.getOrCopy(spaceKey);
                if (space == null) {
                    logger.warn("No space with key {} found in the cache. Recreating the space object...", (Object)spaceKey);
                    space = new TinySpace(spaceKey, tinySpaceEvent.getName(), true);
                }
                this.updateMap.put(spaceKey, space);
                space.setName(tinySpaceEvent.getName());
                break;
            }
            case SPACE_DELETED: {
                this.updateMap.remove(spaceKey);
                break;
            }
            case SPACE_ARCHIVED: {
                TinySpace space = this.updateMap.getOrCopy(spaceKey);
                if (space != null) {
                    space.setCurrent(false);
                    break;
                }
                logger.warn("No space with key {} found in the cache.", (Object)spaceKey);
                break;
            }
            case SPACE_UNARCHIVED: {
                TinySpace space = this.updateMap.getOrCopy(spaceKey);
                if (space != null) {
                    space.setCurrent(true);
                    break;
                }
                logger.warn("No space with key {} found in the cache.", (Object)spaceKey);
            }
        }
    }

    public void finish() {
        if (this.spaces == null || this.updateMap.isModified()) {
            this.spaceMap = this.updateMap.getUnderlyingMap();
            ArrayList<TinySpace> updatedSpaces = new ArrayList<TinySpace>(this.spaceMap.values());
            updatedSpaces.sort(SpaceComparator.SPACE_COMPARATOR);
            this.spaces = Collections.unmodifiableList(updatedSpaces);
        }
    }

    public List<TinySpace> getSpaces() {
        return this.spaces;
    }

    public TinySpace get(String key) {
        return this.spaceMap.get(key);
    }
}

