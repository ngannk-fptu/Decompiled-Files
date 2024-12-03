/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.GroupManager
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.extract.GroupExtractionService;
import com.atlassian.migration.agent.service.extract.UserExtractionService;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.UserService;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.GroupManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class UserGroupExtractFacade {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(UserGroupExtractFacade.class);
    private static final Duration CACHE_TIME = Duration.of(1L, ChronoUnit.HOURS);
    private static final String GLOBAL_ENTITIES_GROUP_KEY = "globalEntitiesGroupKey";
    private final UserService userService;
    private final GroupManager groupManager;
    private final LoadingCache<GlobalEntityType, Set<String>> userWithGlobalEntitiesPermissionCache;
    private final LoadingCache<String, Set<String>> groupCache;
    private final LoadingCache<String, Set<String>> globalEntitiesGroupCache;
    private final LoadingCache<Set<String>, Set<String>> usersWithPermissionsCache;

    public UserGroupExtractFacade(UserService userService, GroupManager groupManager, final UserExtractionService userExtractionService, final GroupExtractionService groupExtractionService) {
        this.userService = userService;
        this.groupManager = groupManager;
        this.groupCache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME.toMillis(), TimeUnit.MILLISECONDS).build((CacheLoader)new CacheLoader<String, Set<String>>(){

            public Set<String> load(String key) {
                return groupExtractionService.getGroupsFromSpace(key);
            }

            public Map<String, Set<String>> loadAll(Iterable<? extends String> keys) {
                return groupExtractionService.getGroupsFromSpaces((List<String>)ImmutableList.copyOf(keys));
            }
        });
        this.usersWithPermissionsCache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME.toMillis(), TimeUnit.MILLISECONDS).build((CacheLoader)new CacheLoader<Set<String>, Set<String>>(){

            public Set<String> load(Set<String> keys) {
                return userExtractionService.getUsersWithPermissionFromSpaces(keys);
            }
        });
        this.globalEntitiesGroupCache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME.toMillis(), TimeUnit.MILLISECONDS).build((CacheLoader)new CacheLoader<String, Set<String>>(){

            public Set<String> load(String key) {
                return groupExtractionService.getGroupsFromGlobalEntities();
            }
        });
        this.userWithGlobalEntitiesPermissionCache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME.toMillis(), TimeUnit.MILLISECONDS).build((CacheLoader)new CacheLoader<GlobalEntityType, Set<String>>(){

            public Set<String> load(GlobalEntityType globalEntityType) {
                return userExtractionService.getUsersFromGlobalEntities(globalEntityType);
            }
        });
    }

    public List<MigrationUser> getAllUsers() {
        return this.userService.getAllUsers();
    }

    public Set<String> getAllGroupNames() {
        try {
            return StreamSupport.stream(this.groupManager.getGroups().spliterator(), false).map(Entity::getName).collect(Collectors.toSet());
        }
        catch (EntityException e) {
            throw new RuntimeException(String.format("Unable to get groups, %s", e.getMessage()), e);
        }
    }

    public Set<String> getUsersFromSpaces(Collection<String> spaceKeys) {
        try {
            return (Set)this.usersWithPermissionsCache.get(new HashSet<String>(spaceKeys));
        }
        catch (ExecutionException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
    }

    public Set<String> getUsersFromSpacesAndGlobalEntities(Collection<String> spaceKeys, Optional<GlobalEntityType> globalEntityType) {
        Set<String> users = this.getUsersFromSpaces(spaceKeys);
        globalEntityType.ifPresent(entityType -> users.addAll(this.getUsersFromGlobalEntities((GlobalEntityType)((Object)entityType))));
        return users;
    }

    @VisibleForTesting
    Set<String> getUsersFromGlobalEntities(GlobalEntityType globalEntityType) {
        try {
            return (Set)this.userWithGlobalEntitiesPermissionCache.get((Object)globalEntityType);
        }
        catch (ExecutionException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
    }

    public Set<String> getGroupsFromSpaces(Collection<String> spaceKeys) {
        try {
            return this.groupCache.getAll(spaceKeys).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        }
        catch (ExecutionException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
    }

    public Set<String> getGroupsFromSpacesAndGlobalEntities(Collection<String> spaceKeys, Optional<GlobalEntityType> globalEntityType) {
        Set<String> groups = this.getGroupsFromSpaces(spaceKeys);
        if (globalEntityType.isPresent()) {
            groups.addAll(this.getGroupsFromGlobalEntities());
        }
        return groups;
    }

    @VisibleForTesting
    Set<String> getGroupsFromGlobalEntities() {
        return Collections.emptySet();
    }

    public void clearCache(String spaceKey) {
        this.groupCache.invalidate((Object)spaceKey);
        this.usersWithPermissionsCache.invalidateAll();
        this.userWithGlobalEntitiesPermissionCache.invalidateAll();
        this.globalEntitiesGroupCache.invalidateAll();
    }

    @VisibleForTesting
    boolean spaceKeyInCache(String spaceKey) {
        return this.groupCache.asMap().containsKey(spaceKey);
    }
}

