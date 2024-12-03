/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  javax.persistence.EntityManager
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.GlobalPermissionCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.GroupCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.GroupMembersCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SpaceCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.SpacePermissionCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.UserCache;
import com.atlassian.confluence.plugins.gatekeeper.model.event.TinyEvent;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.GroupMembers;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyGroup;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.model.space.SpacePermissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluatorCacheImpl
implements EvaluatorCache {
    private static final Logger logger = LoggerFactory.getLogger(EvaluatorCacheImpl.class);
    private final SpaceCache spaceCache;
    private final GlobalPermissionCache globalPermissionCache;
    private final UserCache userCache;
    private final GroupCache groupCache;
    private final GroupMembersCache groupMembersCache;
    private final SpacePermissionCache spacePermissionsCache;
    private final CrowdDirectoryService crowdDirectoryService;
    private final EvaluatorCacheHolder evaluatorCacheHolder;
    private final UserAccessor userAccessor;

    private EvaluatorCacheImpl(EvaluatorCacheImpl oldCache, CrowdDirectoryService crowdDirectoryService, EvaluatorCacheHolder evaluatorCacheHolder, UserAccessor userAccessor) {
        this.evaluatorCacheHolder = evaluatorCacheHolder;
        this.crowdDirectoryService = crowdDirectoryService;
        this.spaceCache = new SpaceCache(oldCache.spaceCache);
        this.userCache = new UserCache(oldCache.userCache);
        this.groupCache = new GroupCache(oldCache.groupCache);
        this.groupMembersCache = new GroupMembersCache(oldCache.groupMembersCache, userAccessor);
        this.globalPermissionCache = new GlobalPermissionCache(oldCache.globalPermissionCache);
        this.spacePermissionsCache = new SpacePermissionCache(oldCache.spacePermissionsCache);
        this.userAccessor = userAccessor;
    }

    EvaluatorCacheImpl(EntityManagerProvider entityManagerProvider, CrowdDirectoryService crowdDirectoryService, EvaluatorCacheHolder evaluatorCacheHolder, UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
        logger.debug("Initializing evaluator cache from database");
        StopWatch watch = StopWatch.createStarted();
        this.evaluatorCacheHolder = evaluatorCacheHolder;
        this.crowdDirectoryService = crowdDirectoryService;
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        logger.debug("Initializing space cache");
        this.spaceCache = new SpaceCache(entityManager);
        logger.debug("Initializing user cache");
        this.userCache = new UserCache(entityManager, crowdDirectoryService);
        logger.debug("Initializing group cache");
        this.groupCache = new GroupCache(entityManager, crowdDirectoryService);
        logger.debug("Initializing group members cache");
        this.groupMembersCache = new GroupMembersCache(userAccessor);
        logger.debug("Initializing global permission cache");
        this.globalPermissionCache = new GlobalPermissionCache(entityManager);
        logger.debug("Initializing space permission cache");
        this.spacePermissionsCache = new SpacePermissionCache(entityManager);
        this.finish();
        logger.debug("Evaluator cache initialized in {} ms", (Object)watch.getTime());
    }

    @Override
    public void update(List<TinyEvent> events) {
        if (events.isEmpty()) {
            return;
        }
        logger.debug("Updating evaluator cache");
        StopWatch watch = new StopWatch();
        watch.start();
        EvaluatorCacheImpl newCache = new EvaluatorCacheImpl(this, this.crowdDirectoryService, this.evaluatorCacheHolder, this.userAccessor);
        newCache.applyEvents(events);
        newCache.finish();
        this.evaluatorCacheHolder.setEvaluatorCache(newCache);
        watch.stop();
        logger.debug("Evaluator cache updated in {} ms", (Object)watch.getTime());
    }

    private void applyEvents(List<TinyEvent> events) {
        for (TinyEvent event : events) {
            switch (event.getEventCategory()) {
                case USER: {
                    this.userCache.update(event);
                    this.groupMembersCache.update(event);
                    this.spacePermissionsCache.update(event);
                    this.globalPermissionCache.update(event);
                    break;
                }
                case GROUP: {
                    this.groupCache.update(event);
                    this.groupMembersCache.update(event);
                    this.spacePermissionsCache.update(event);
                    this.globalPermissionCache.update(event);
                    break;
                }
                case SPACE: {
                    this.spaceCache.update(event);
                    this.spacePermissionsCache.update(event);
                    break;
                }
                case MEMBERSHIP: {
                    this.groupMembersCache.update(event);
                    break;
                }
                case SPACE_PERMISSION: {
                    this.spacePermissionsCache.update(event);
                    break;
                }
                case GLOBAL_PERMISSION: {
                    this.globalPermissionCache.update(event);
                }
            }
        }
    }

    private void finish() {
        logger.debug("Finishing group members cache");
        this.groupMembersCache.finish();
        logger.debug("Finishing global permission cache");
        this.globalPermissionCache.finish();
        logger.debug("Finishing space cache");
        this.spaceCache.finish();
        logger.debug("Finishing space permission cache");
        this.spacePermissionsCache.finish();
        logger.debug("Finishing user cache");
        this.userCache.finish(this);
        logger.debug("Finishing group cache");
        this.groupCache.finish(this);
    }

    @Override
    public boolean isGlobalAnonymousAccessEnabled() {
        return this.globalPermissionCache.isGlobalAnonymousAccessEnabled();
    }

    @Override
    public TinyOwner getGroup(String groupname) {
        return this.groupCache.get(groupname);
    }

    @Override
    public List<TinyGroup> getGroups() {
        return this.groupCache.getGroups();
    }

    public Set<String> getGroupsWithCanUse() {
        return this.globalPermissionCache.getCanUseGroups();
    }

    @Override
    public boolean hasGroupCanUse(String groupName) {
        return this.globalPermissionCache.hasGroupCanUse(groupName);
    }

    @Override
    public GroupMembers getGroupMembers(String groupName) {
        return this.groupMembersCache.get(groupName);
    }

    @Override
    public TinyOwner getUser(String username) {
        return this.userCache.get(username);
    }

    @Override
    public List<TinyUser> getUsers() {
        return this.userCache.getUsers();
    }

    @Override
    public boolean hasUserCanUse(String username) {
        Set<String> canUseGroups = this.globalPermissionCache.getCanUseGroups();
        for (String groupName : canUseGroups) {
            GroupMembers members = this.groupMembersCache.get(groupName);
            if (!members.contains(username)) continue;
            return true;
        }
        return this.globalPermissionCache.hasUserCanUse(username);
    }

    @Override
    public boolean isUserConfluenceAdministrator(String username) {
        return this.groupMembersCache.get("confluence-administrators").contains(username);
    }

    @Override
    public List<TinySpace> getSpaces() {
        return this.spaceCache.getSpaces();
    }

    public TinySpace getSpace(String spaceKey) {
        return this.spaceCache.get(spaceKey);
    }

    @Override
    public SpacePermissions getSpacePermissions(String spaceKey) {
        return this.spacePermissionsCache.get(spaceKey);
    }

    @Override
    public String getMemoryUsage() {
        return "Not available";
    }
}

