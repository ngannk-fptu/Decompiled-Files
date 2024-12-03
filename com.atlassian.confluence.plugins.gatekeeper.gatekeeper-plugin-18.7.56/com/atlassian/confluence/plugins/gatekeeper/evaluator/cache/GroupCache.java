/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  javax.persistence.EntityManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SubCache;
import com.atlassian.confluence.plugins.gatekeeper.model.comparator.GroupComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyGroupEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyGroup;
import com.atlassian.confluence.plugins.gatekeeper.util.CopyOnceMap;
import com.atlassian.confluence.plugins.gatekeeper.util.QueryByIdBatcher;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GroupCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(GroupCache.class);
    private static final String GROUP_TABLE_QUERY_INITIAL = "SELECT id, name FROM InternalGroup WHERE directory=:directory ORDER BY id";
    private static final String GROUP_TABLE_QUERY_BATCHED = "SELECT id, name FROM InternalGroup WHERE directory=:directory AND id > :id ORDER BY id";
    private List<TinyGroup> groups;
    private Map<String, TinyGroup> groupMap;
    private CopyOnceMap<TinyGroup> updateMap;

    GroupCache(Map<String, TinyGroup> groupMap) {
        this.groupMap = groupMap;
        this.updateMap = new CopyOnceMap<TinyGroup>(groupMap);
    }

    GroupCache(GroupCache groupCache) {
        this(groupCache.groupMap);
        this.groups = groupCache.groups;
    }

    GroupCache(EntityManager entityManager, CrowdDirectoryService crowdDirectoryService) {
        this.groupMap = new Object2ObjectOpenHashMap();
        logger.trace("Reading groups info");
        List directories = crowdDirectoryService.findAllDirectories();
        HashSet lowerCaseGroupNames = new HashSet();
        directories.forEach(directory -> this.loadGroupsFromDirectory(entityManager, (Directory)directory, lowerCaseGroupNames));
        this.updateMap = new CopyOnceMap<TinyGroup>(this.groupMap);
    }

    private void loadGroupsFromDirectory(EntityManager entityManager, Directory directory, Set<String> lowerCaseGroupNames) {
        List<Object[]> queryResults;
        if (!directory.isActive()) {
            logger.info("Skipping loading groups from directory {} with id {}, as directory is not active.", (Object)directory.getName(), (Object)directory.getId());
            return;
        }
        logger.debug("Loading groups from directory {} with id {}", (Object)directory.getName(), (Object)directory.getId());
        int counter = 0;
        int addedCounter = 0;
        QueryByIdBatcher queryByIdBatcher = new QueryByIdBatcher(entityManager, GROUP_TABLE_QUERY_INITIAL, GROUP_TABLE_QUERY_BATCHED);
        queryByIdBatcher.addQueryParameter("directory", directory);
        while (!(queryResults = queryByIdBatcher.getBatch()).isEmpty()) {
            for (Object[] row : queryResults) {
                String name = ((String)row[1]).intern();
                String lowerName = name.toLowerCase();
                if (!lowerCaseGroupNames.contains(lowerName)) {
                    lowerCaseGroupNames.add(lowerName);
                    this.groupMap.put(name, new TinyGroup(name));
                    ++addedCounter;
                    logger.trace("Reading group info for group {}", (Object)name);
                } else {
                    logger.trace("Skipped reading group info for group {} - group name already added", (Object)name);
                }
                ++counter;
            }
            logger.trace("Finished reading one batch - Finished loading groups from database (directory {}:{}): processed {} groups, {} added to the cache, cache size: {} entries", new Object[]{directory.getId(), directory.getName(), counter, addedCounter, this.groupMap.size()});
            if (queryResults.size() >= queryByIdBatcher.getBatchSize()) continue;
        }
        this.updateMap = new CopyOnceMap<TinyGroup>(this.groupMap);
        logger.debug("Finished loading groups from database (directory {}:{}): processed {} groups, {} added to the cache, cache size: {} entries", new Object[]{directory.getId(), directory.getName(), counter, addedCounter, this.groupMap.size()});
    }

    @Override
    public void update(TinyEvent event) {
        if (!(event instanceof TinyGroupEvent)) {
            logger.warn("The event is not a TinyGroupEvent. Event type is {}", (Object)event.getEventType().name());
            return;
        }
        String groupName = ((TinyGroupEvent)event).getGroupName();
        switch (event.getEventType()) {
            case GROUP_ADDED: {
                this.updateMap.put(groupName, new TinyGroup(groupName));
                break;
            }
            case GROUP_DELETED: {
                this.updateMap.remove(groupName);
            }
        }
    }

    void finish(EvaluatorCache evaluatorCache) {
        Collection<TinyGroup> currentGroupList = this.updateMap.getUnderlyingMap().values();
        for (TinyGroup g : currentGroupList) {
            boolean newCanUse;
            String groupname = g.getName();
            boolean storedCanUse = g.hasCanUse();
            if (storedCanUse == (newCanUse = evaluatorCache.hasGroupCanUse(groupname))) continue;
            this.updateMap.getOrCopy(groupname).setCanUse(newCanUse);
        }
        if (this.groups == null || this.updateMap.isModified()) {
            this.groupMap = this.updateMap.getUnderlyingMap();
            ArrayList<TinyGroup> updatedGroups = new ArrayList<TinyGroup>(this.groupMap.values());
            updatedGroups.sort(GroupComparator.GROUP_NAME_COMPARATOR);
            this.groups = Collections.unmodifiableList(updatedGroups);
        }
    }

    public List<TinyGroup> getGroups() {
        return this.groups;
    }

    public TinyGroup get(String groupName) {
        return this.groupMap.get(groupName);
    }
}

