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
import com.atlassian.confluence.plugins.gatekeeper.model.comparator.UserComparator;
import com.atlassian.confluence.plugins.gatekeeper.model.event.EventType;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyUserEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyAnonymous;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.util.CopyOnceMap;
import com.atlassian.confluence.plugins.gatekeeper.util.QueryByIdBatcher;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UserCache
implements SubCache {
    private static final Logger logger = LoggerFactory.getLogger(UserCache.class);
    private static String USER_TABLE_QUERY_INITIAL = "SELECT id, lowerName, displayName, active FROM InternalUser WHERE directory=:directory ORDER BY id";
    private static String USER_TABLE_QUERY_BATCHED = "SELECT id, lowerName, displayName, active FROM InternalUser WHERE directory=:directory AND id > :id ORDER BY id";
    private List<TinyUser> users;
    private Map<String, TinyUser> userMap;
    private CopyOnceMap<TinyUser> updateMap;

    UserCache(Map<String, TinyUser> userMap) {
        this.userMap = userMap;
        this.updateMap = new CopyOnceMap<TinyUser>(userMap);
    }

    UserCache(UserCache userCache) {
        this(userCache.userMap);
        this.users = userCache.users;
    }

    UserCache(EntityManager entityManager, CrowdDirectoryService crowdDirectoryService) {
        this.userMap = new Object2ObjectOpenHashMap();
        List directories = crowdDirectoryService.findAllDirectories();
        directories.forEach(directory -> this.loadUsersFromDirectory(entityManager, (Directory)directory));
        this.updateMap = new CopyOnceMap<TinyUser>(this.userMap);
    }

    private void loadUsersFromDirectory(EntityManager entityManager, Directory directory) {
        List<Object[]> queryResults;
        if (!directory.isActive()) {
            logger.info("Skipping loading users from directory {} with id {}, as directory is not active.", (Object)directory.getName(), (Object)directory.getId());
            return;
        }
        logger.debug("Loading users from directory: {} with id {}", (Object)directory.getName(), (Object)directory.getId());
        int counter = 0;
        int addedCounter = 0;
        QueryByIdBatcher queryByIdBatcher = new QueryByIdBatcher(entityManager, USER_TABLE_QUERY_INITIAL, USER_TABLE_QUERY_BATCHED);
        queryByIdBatcher.addQueryParameter("directory", directory);
        while (!(queryResults = queryByIdBatcher.getBatch()).isEmpty()) {
            for (Object[] row : queryResults) {
                String username = (String)row[1];
                String displayName = (String)row[2];
                boolean active = (Boolean)row[3];
                if (!this.userMap.containsKey(username)) {
                    this.userMap.put(username, new TinyUser(username, displayName, active));
                    ++addedCounter;
                }
                ++counter;
            }
            logger.trace("Finished reading one batch - loading users from database (directory {}:{}): processed {} users so far, {} added to the cache so far, cache size: {} entries", new Object[]{directory.getId(), directory.getName(), counter, addedCounter, this.userMap.size()});
            if (queryResults.size() >= queryByIdBatcher.getBatchSize()) continue;
        }
        logger.debug("Finished loading users from database (directory {}:{}): processed {} users, {} added to the cache, cache size: {} entries", new Object[]{directory.getId(), directory.getName(), counter, addedCounter, this.userMap.size()});
    }

    @Override
    public void update(TinyEvent event) {
        if (!(event instanceof TinyUserEvent)) {
            logger.warn("The event is not a TinyUserEvent. Event type is {}", (Object)event.getEventType().name());
            return;
        }
        TinyUserEvent tinyUserEvent = (TinyUserEvent)event;
        EventType eventType = event.getEventType();
        String username = tinyUserEvent.getUsername();
        switch (eventType) {
            case USER_ADDED: {
                TinyUser user = new TinyUser(username, tinyUserEvent.getDisplayName(), tinyUserEvent.isActive());
                this.updateMap.put(username, user);
            }
            case USER_UPDATED: {
                TinyUser user = this.updateMap.getOrCopy(username);
                user.setDisplayName(tinyUserEvent.getDisplayName());
                break;
            }
            case USER_DELETED: {
                this.updateMap.remove(username);
                break;
            }
            case USER_RENAMED: {
                String oldUsername = tinyUserEvent.getOldUsername();
                TinyUser renamedUser = this.updateMap.getOrCopy(oldUsername);
                renamedUser.setName(username);
                this.updateMap.put(username, renamedUser);
                if (oldUsername.equalsIgnoreCase(username)) break;
                this.updateMap.remove(oldUsername);
                break;
            }
            case USER_ACTIVATED: 
            case USER_DEACTIVATED: {
                TinyUser user = this.updateMap.getOrCopy(username);
                user.setActive(eventType == EventType.USER_ACTIVATED);
            }
        }
    }

    public UserCache finish(EvaluatorCache evaluatorCache) {
        for (TinyUser u : this.updateMap.getUnderlyingMap().values()) {
            boolean newCanUse;
            if (u.isAnonymous()) continue;
            String username = u.getName();
            boolean storedCanUse = u.hasCanUse();
            if (storedCanUse == (newCanUse = evaluatorCache.hasUserCanUse(username))) continue;
            this.updateMap.getOrCopy(username).setCanUse(newCanUse);
        }
        if (this.users == null || this.updateMap.isModified()) {
            this.userMap = this.updateMap.getUnderlyingMap();
            ArrayList<TinyOwner> updatedUsers = new ArrayList<TinyOwner>(this.userMap.values().size() + 1);
            updatedUsers.add(TinyAnonymous.ANONYMOUS);
            updatedUsers.addAll(this.userMap.values());
            updatedUsers.sort(UserComparator.USER_USERNAME_COMPARATOR);
            this.users = Collections.unmodifiableList(updatedUsers);
        }
        return this;
    }

    public List<TinyUser> getUsers() {
        return this.users;
    }

    public TinyUser get(String username) {
        return this.userMap.get(username);
    }
}

