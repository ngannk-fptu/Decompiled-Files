/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.security.DefaultSpacePermissionManager;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class CachingSpacePermissionManager
extends DefaultSpacePermissionManager {
    private static final Logger log = LoggerFactory.getLogger(CachingSpacePermissionManager.class);
    private boolean lookAheadEnabled = false;
    private final TransactionAwareCache<SpacePermissionCacheKey, Boolean> spacePermissionCache;
    private final SpacePermissionDao spacePermissionDao;
    private final GroupNamesCache groupNamesCache;
    private final ScopesRequestCacheDelegate scopesRequestCacheDelegate;

    public CachingSpacePermissionManager(SpacePermissionDao spacePermissionDao, PermissionCheckExemptions permissionCheckExemptions, SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory, TransactionAwareCacheFactory cacheFactory, EventPublisher eventPublisher, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, ConfluenceUserResolver userResolver, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager, GroupResolver groupResolver) {
        super(spacePermissionDao, permissionCheckExemptions, spacePermissionDefaultsStoreFactory, eventPublisher, confluenceAccessManager, spacePermissionAccessMapper, crowdService, userResolver, accessModeManager, scopesRequestCacheDelegate, settingsManager, groupResolver);
        this.spacePermissionDao = spacePermissionDao;
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
        this.groupNamesCache = new GroupNamesCache(CoreCache.PERMITTED_GROUP_NAMES_BY_SPACE.resolve(cacheFactory::getTxCache));
        this.spacePermissionCache = CoreCache.SPACE_PERMISSIONS.resolve(cacheFactory::getTxCache);
    }

    @Override
    public boolean permissionExists(SpacePermission permission) {
        return this.findCachedPermissionOrFetchFromDao(permission);
    }

    @Override
    protected void savePermissionToDao(SpacePermission spacePermission) {
        this.executeAndInvalidate(new CacheInvalidation(spacePermission), () -> CachingSpacePermissionManager.super.savePermissionToDao(spacePermission));
    }

    @Override
    protected void removePermissionFromDao(SpacePermission spacePermission) {
        this.executeAndInvalidate(new CacheInvalidation(spacePermission), () -> CachingSpacePermissionManager.super.removePermissionFromDao(spacePermission));
    }

    @Override
    protected void removeAllPermissionsFromDao(Space space) {
        List<SpacePermission> existingSpacePermissions = this.getAllSpacePermissions(space);
        this.executeAndInvalidate(new CacheInvalidation(existingSpacePermissions), () -> {
            log.debug("remove all permissions for space: {}", (Object)space.getName());
            CachingSpacePermissionManager.super.removeAllPermissionsFromDao(space);
        });
    }

    private List<SpacePermission> getAllSpacePermissions(Space space) {
        this.spacePermissionDao.refresh(space);
        return Lists.newArrayList(space.getPermissions());
    }

    private void addPermissionsToCache(List permissions, boolean exists) {
        for (Object permission : permissions) {
            SpacePermission spacePermission = (SpacePermission)permission;
            this.spacePermissionCache.put(new SpacePermissionCacheKey(spacePermission), exists);
        }
    }

    private boolean findCachedPermissionOrFetchFromDao(SpacePermission permission) {
        boolean result = this.spacePermissionCache.get(new SpacePermissionCacheKey(permission), (Supplier<Boolean>)((Supplier)() -> this.spacePermissionDao.hasPermission(permission)));
        if (this.isLookAheadEnabled()) {
            this.doLookAheadCache(permission);
        }
        return result;
    }

    private void doLookAheadCache(SpacePermission permission) {
        List permissions = this.spacePermissionDao.findPermissionTypes(permission);
        log.debug("Permissions Look Ahead - found {} permissions for {}", (Object)permissions.size(), (Object)permission);
        this.addPermissionsToCache(permissions, true);
    }

    @Override
    public void flushCaches() {
        super.flushCaches();
        try {
            this.spacePermissionCache.removeAll();
            this.groupNamesCache.invalidateAll();
        }
        catch (Exception e) {
            log.error("Exception while flushing permissions cache", (Throwable)e);
            throw new RuntimeException("Exception while flushing permissions cache", e);
        }
    }

    public boolean isLookAheadEnabled() {
        return this.lookAheadEnabled;
    }

    public void setLookAheadEnabled(boolean lookAheadEnabled) {
        this.lookAheadEnabled = lookAheadEnabled;
    }

    @Override
    public boolean hasPermissionNoExemptions(String permissionType, @Nullable Space space, @Nullable User remoteUser) {
        Boolean hasPermission = ThreadLocalPermissionsCacheInternal.hasSpacePermission(permissionType, space, remoteUser);
        if (hasPermission == null) {
            hasPermission = super.hasPermissionNoExemptions(permissionType, space, remoteUser);
            ThreadLocalPermissionsCacheInternal.cacheSpacePermission(remoteUser, permissionType, space, hasPermission);
        }
        return this.scopesRequestCacheDelegate.hasPermission(permissionType, (Object)space) && hasPermission != false;
    }

    @Override
    protected Set<String> getGroupNamesWithPermission(@Nullable Space targetSpace, String permissionType) {
        return this.groupNamesCache.getGroupNamesWithPermission(targetSpace, permissionType, () -> super.getGroupNamesWithPermission(targetSpace, permissionType));
    }

    private void executeAndInvalidate(CacheInvalidation cacheInvalidation, Runnable action) {
        try {
            action.run();
        }
        finally {
            cacheInvalidation.perform();
        }
    }

    @VisibleForTesting
    static class GroupNamesCache {
        private final TransactionAwareCache<CacheKey, Set<String>> cache;

        private GroupNamesCache(TransactionAwareCache<CacheKey, Set<String>> cache) {
            this.cache = (TransactionAwareCache)Preconditions.checkNotNull(cache);
        }

        public @NonNull Set<String> getGroupNamesWithPermission(@Nullable Space space, String permissionType, java.util.function.Supplier<Set<String>> valueLoader) {
            return this.cache.get(new CacheKey(permissionType, space), (Supplier<Set<String>>)((Supplier)() -> ImmutableSet.copyOf((Collection)((Collection)valueLoader.get()))));
        }

        public void invalidate(String permissionType, @Nullable Space space) {
            CacheKey cacheKey = new CacheKey(permissionType, space);
            log.debug("Invalidating group names cache for {}", (Object)cacheKey);
            this.cache.remove(cacheKey);
        }

        public void invalidateAll() {
            log.debug("Invalidating group names cache");
            this.cache.removeAll();
        }

        static final class CacheKey
        implements Serializable {
            final @NonNull String permissionType;
            final @Nullable String spaceKey;

            CacheKey(String permissionType, @Nullable Space space) {
                this.permissionType = permissionType;
                this.spaceKey = space == null ? null : space.getKey();
            }

            public boolean equals(@Nullable Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                CacheKey that = (CacheKey)o;
                return Objects.equals(this.permissionType, that.permissionType) && Objects.equals(this.spaceKey, that.spaceKey);
            }

            public int hashCode() {
                return Objects.hash(this.permissionType, this.spaceKey);
            }

            public String toString() {
                return "{permissionType='" + this.permissionType + "', spaceKey='" + this.spaceKey + "'}";
            }
        }
    }

    private class CacheInvalidation {
        private final ImmutableList<Runnable> tasks;

        private CacheInvalidation(SpacePermission spacePermission) {
            this(Collections.singleton(spacePermission));
        }

        private CacheInvalidation(Iterable<SpacePermission> spacePermissions) {
            this.tasks = ImmutableList.copyOf((Iterable)Iterables.transform(spacePermissions, this::task));
        }

        void perform() {
            for (Runnable task : this.tasks) {
                task.run();
            }
        }

        private Runnable task(SpacePermission spacePermission) {
            SpacePermissionCacheKey spacePermissionCacheKey = new SpacePermissionCacheKey(spacePermission);
            Space space = spacePermission.getSpace();
            return () -> {
                try {
                    CachingSpacePermissionManager.this.spacePermissionCache.remove(spacePermissionCacheKey);
                    if (spacePermission.isGroupPermission()) {
                        CachingSpacePermissionManager.this.groupNamesCache.invalidate(spacePermission.getType(), space);
                    }
                }
                catch (Exception e) {
                    log.error("There was a problem removing the permission '" + spacePermission + "' from the cache: ", (Throwable)e);
                    throw new RuntimeException(e);
                }
            };
        }
    }

    static class SpacePermissionCacheKey
    implements Serializable {
        private final String type;
        private final long spaceId;
        private final @Nullable String groupname;
        private final @Nullable UserKey userKey;
        private final @Nullable String allUsersSubject;

        public SpacePermissionCacheKey(SpacePermission spacePermission) {
            this.type = spacePermission.getType();
            this.spaceId = spacePermission.getSpaceId();
            this.groupname = spacePermission.getGroup();
            ConfluenceUser userSubject = spacePermission.getUserSubject();
            this.userKey = userSubject == null ? null : userSubject.getKey();
            this.allUsersSubject = spacePermission.getAllUsersSubject();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SpacePermissionCacheKey)) {
                return false;
            }
            SpacePermissionCacheKey that = (SpacePermissionCacheKey)obj;
            return Objects.equals(this.type, that.type) && Objects.equals(this.spaceId, that.spaceId) && Objects.equals(this.groupname, that.groupname) && Objects.equals(this.userKey, that.userKey) && Objects.equals(this.allUsersSubject, that.allUsersSubject);
        }

        public int hashCode() {
            return Objects.hash(this.type, this.spaceId, this.groupname, this.userKey, this.allUsersSubject);
        }
    }
}

