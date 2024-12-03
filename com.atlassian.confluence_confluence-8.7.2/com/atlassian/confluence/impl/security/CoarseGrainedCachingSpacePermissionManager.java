/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForGroupEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForUserEvent;
import com.atlassian.confluence.impl.security.AbstractSpacePermissionManager;
import com.atlassian.confluence.impl.security.SpacePermissionCachePrimer;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoarseGrainedCachingSpacePermissionManager
extends AbstractSpacePermissionManager
implements SpacePermissionCachePrimer.Primeable {
    private static final Logger log = LoggerFactory.getLogger(CoarseGrainedCachingSpacePermissionManager.class);
    protected final SpacePermissionDao spacePermissionDao;
    private final SpacePermissionManagerInternal delegate;
    private final PermissionsCache cache;
    private final EventPublisher eventPublisher;

    public CoarseGrainedCachingSpacePermissionManager(PermissionCheckExemptions permissionCheckExemptions, CacheFactory cacheFactory, SpacePermissionManagerInternal delegate, SpacePermissionDao spacePermissionDao, EventPublisher eventPublisher, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager) {
        super(permissionCheckExemptions, confluenceAccessManager, spacePermissionAccessMapper, crowdService, accessModeManager, scopesRequestCacheDelegate, settingsManager);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.spacePermissionDao = Objects.requireNonNull(spacePermissionDao);
        this.delegate = Objects.requireNonNull(delegate);
        this.cache = new PermissionsCache(cacheFactory, spacePermissionDao::findPermissionsForSpace, (Supplier<Collection<SpacePermission>>)((Supplier)spacePermissionDao::findAllGlobalPermissions));
    }

    @Override
    @Deprecated
    public void removeAllPermissions(Space space) {
        this.removeAllPermissions(space, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissions(Space space, SpacePermissionContext context) {
        log.debug("removing all for space {}", (Object)space);
        this.delegate.removeAllPermissions(space, context);
        this.cache.removePermissions(space);
    }

    @Override
    @Deprecated
    public void removePermission(SpacePermission permission) {
        this.removePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void removePermission(SpacePermission permission, SpacePermissionContext context) {
        log.debug("removing for {}", (Object)permission);
        Space space = permission.getSpace();
        this.delegate.removePermission(permission, context);
        this.cache.removePermissions(space);
    }

    @Override
    @Deprecated
    public void removeAllUserPermissions(ConfluenceUser user) {
        this.removeAllUserPermissions(user, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user, SpacePermissionContext context) {
        log.debug("removing all for user {}", (Object)user);
        List<SpacePermission> permissions = this.spacePermissionDao.findPermissionsForUser(user);
        SpacePermissionContext subContext = SpacePermissionContext.builder(context).sendEvents(false).build();
        this.removePermissions(permissions, subContext);
        if (context.shouldSendEvents()) {
            this.eventPublisher.publish((Object)new SpacePermissionsRemoveForUserEvent((Object)this, user, permissions));
        }
    }

    private void removePermissions(Collection<SpacePermission> permissions, SpacePermissionContext context) {
        log.debug("removing permissions {}", permissions);
        permissions.stream().peek(p -> this.delegate.removePermission((SpacePermission)p, context)).map(SpacePermission::getSpace).distinct().forEach(this.cache::removePermissions);
    }

    @Override
    @Deprecated
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType) {
        this.removeGlobalPermissionForUser(user, permissionType, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType, SpacePermissionContext context) {
        log.debug("remove global perm {} for user {}", (Object)permissionType, (Object)user);
        this.delegate.removeGlobalPermissionForUser(user, permissionType, context);
        this.cache.removeGlobalPermissions();
    }

    @Override
    @Deprecated
    public void removeAllPermissionsForGroup(String group) {
        this.removeAllPermissionsForGroup(group, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissionsForGroup(String group, SpacePermissionContext context) {
        log.debug("remove all perms for group {}", (Object)group);
        List<SpacePermission> permissions = this.getAllPermissionsForGroup(group);
        SpacePermissionContext subContext = SpacePermissionContext.builder(context).sendEvents(false).build();
        this.removePermissions(permissions, subContext);
        if (context.shouldSendEvents()) {
            this.eventPublisher.publish((Object)new SpacePermissionsRemoveForGroupEvent((Object)this, group, permissions));
        }
    }

    @Override
    public List<SpacePermission> getAllPermissionsForGroup(String group) {
        return this.delegate.getAllPermissionsForGroup(group);
    }

    @Override
    public List<SpacePermission> getGlobalPermissions() {
        return this.delegate.getGlobalPermissions();
    }

    @Override
    public List<SpacePermission> getGlobalPermissions(String permissionType) {
        return this.delegate.getGlobalPermissions(permissionType);
    }

    @Override
    public void flushCaches() {
        log.debug("Flushing caches");
        this.delegate.flushCaches();
        this.cache.removeAllPermissions();
    }

    @Override
    public void createDefaultSpacePermissions(Space space) {
        log.debug("create default space perms for {}", (Object)space);
        this.delegate.createDefaultSpacePermissions(space);
        this.cache.removePermissions(space);
    }

    @Override
    public void createPrivateSpacePermissions(Space space) {
        log.debug("create private space perms for {}", (Object)space);
        this.delegate.createPrivateSpacePermissions(space);
        this.cache.removePermissions(space);
    }

    @Override
    public Collection<Group> getGroupsWithPermissions(@Nullable Space space) {
        return this.delegate.getGroupsWithPermissions(space);
    }

    @Override
    public Map<String, Long> getGroupsForPermissionType(String permissionType, Space space) {
        return this.delegate.getGroupsForPermissionType(permissionType, space);
    }

    @Override
    public Collection<User> getUsersWithPermissions(@Nullable Space space) {
        return this.delegate.getUsersWithPermissions(space);
    }

    @Override
    public Map<String, Long> getUsersForPermissionType(String permissionType, Space space) {
        return this.delegate.getUsersForPermissionType(permissionType, space);
    }

    @Override
    protected Iterable<String> getGroupNamesWithPermission(@Nullable Space targetSpace, String permissionType) {
        return this.getGroupNamesWithPermission(targetSpace, CoarseGrainedCachingSpacePermissionManager.permissionType(permissionType));
    }

    private Iterable<String> getGroupNamesWithPermission(@Nullable Space targetSpace, PermissionType permissionType) {
        ImmutableSet subjects = this.cache.getPermissions(targetSpace).get((Object)permissionType);
        return subjects.stream().filter(GroupSubject.class::isInstance).map(GroupSubject.class::cast).map(GroupSubject::getGroupName).collect(Collectors.toList());
    }

    @Override
    public boolean permissionExists(SpacePermission permission) {
        return this.permissionExists(permission.getSpace(), CoarseGrainedCachingSpacePermissionManager.permissionType(permission), Subject.of(permission));
    }

    private boolean permissionExists(@Nullable Space space, PermissionType permissionType, Subject subject) {
        return this.cache.getPermissions(space).get((Object)permissionType).contains((Object)subject);
    }

    @Override
    @Deprecated
    public void savePermission(SpacePermission permission) {
        this.savePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void savePermission(SpacePermission permission, SpacePermissionContext context) {
        log.debug("save permission {}", (Object)permission);
        this.delegate.savePermission(permission, context);
        this.cache.removePermissions(permission.getSpace());
    }

    @Override
    public void prime(Iterable<? extends Space> spaces) {
        this.cache.preload(spaces);
    }

    private static PermissionType permissionType(String value) {
        return new PermissionType(value);
    }

    private static PermissionType permissionType(SpacePermission sp) {
        return CoarseGrainedCachingSpacePermissionManager.permissionType(sp.getType());
    }

    @VisibleForTesting
    static class PermissionsCache {
        @VisibleForTesting
        static final String GLOBAL_PERMISSIONS_KEY = "_GLOBAL_";
        private final Cache<String, ImmutableSetMultimap<PermissionType, Subject>> cache;
        private final Supplier<Collection<SpacePermission>> globalPermissionsLoader;
        private final Function<Space, Collection<SpacePermission>> spacePermissionsLoader;

        public PermissionsCache(CacheFactory cacheFactory, Function<Space, Collection<SpacePermission>> spacePermissionsLoader, Supplier<Collection<SpacePermission>> globalPermissionsLoader) {
            this.globalPermissionsLoader = Objects.requireNonNull(globalPermissionsLoader);
            this.spacePermissionsLoader = Objects.requireNonNull(spacePermissionsLoader);
            this.cache = CoreCache.SPACE_PERMISSIONS_BY_SPACE_KEY.getCache(cacheFactory);
        }

        @VisibleForTesting
        static String cacheKey(@Nullable Space space) {
            return space == null ? GLOBAL_PERMISSIONS_KEY : space.getKey();
        }

        public void preload(Iterable<? extends Space> spaces) {
            this.getPermissions(null);
            spaces.forEach(this::getPermissions);
        }

        public void removePermissions(@Nullable Space space) {
            this.cache.remove((Object)PermissionsCache.cacheKey(space));
        }

        public void removeGlobalPermissions() {
            this.cache.remove((Object)GLOBAL_PERMISSIONS_KEY);
        }

        public void removeAllPermissions() {
            this.cache.removeAll();
        }

        public ImmutableSetMultimap<PermissionType, Subject> getPermissions(@Nullable Space space) {
            return (ImmutableSetMultimap)this.cache.get((Object)PermissionsCache.cacheKey(space), () -> PermissionsCache.groupByPermissionType(this.loadPermissions(space)));
        }

        private Collection<SpacePermission> loadPermissions(@Nullable Space space) {
            return space == null ? (Collection<SpacePermission>)this.globalPermissionsLoader.get() : this.spacePermissionsLoader.apply(space);
        }

        private static ImmutableSetMultimap<PermissionType, Subject> groupByPermissionType(Collection<SpacePermission> permissions) {
            ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
            for (SpacePermission permission : permissions) {
                builder.put((Object)CoarseGrainedCachingSpacePermissionManager.permissionType(permission), (Object)Subject.of(permission));
            }
            return builder.build();
        }
    }

    @VisibleForTesting
    static class PermissionType
    implements Serializable {
        private static final long serialVersionUID = 4637347446251241916L;
        private final String value;

        PermissionType(String value) {
            this.value = value;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            PermissionType that = (PermissionType)o;
            return Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return Objects.hash(this.value);
        }
    }

    @VisibleForTesting
    static enum AuthenticatedUsersSubject implements Subject
    {
        INSTANCE;

    }

    @VisibleForTesting
    static enum AnonymousSubject implements Subject
    {
        INSTANCE;

    }

    @VisibleForTesting
    static class GroupSubject
    implements Subject {
        private static final long serialVersionUID = 2238195546091261179L;
        private final String groupName;

        public GroupSubject(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            GroupSubject that = (GroupSubject)o;
            return Objects.equals(this.groupName, that.groupName);
        }

        public int hashCode() {
            return Objects.hash(this.groupName);
        }
    }

    @VisibleForTesting
    static class UserSubject
    implements Subject {
        private static final long serialVersionUID = 913997689396158846L;
        private final UserKey userKey;

        public UserSubject(@NonNull ConfluenceUser user) {
            this.userKey = user.getKey();
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            UserSubject that = (UserSubject)o;
            return Objects.equals(this.userKey, that.userKey);
        }

        public int hashCode() {
            return Objects.hash(this.userKey);
        }
    }

    @VisibleForTesting
    static interface Subject
    extends Serializable {
        public static Subject ofUser(@NonNull User remoteUser) {
            ConfluenceUser user = FindUserHelper.getUser(remoteUser);
            return new UserSubject(user);
        }

        public static Subject ofAnonymousUsers() {
            return AnonymousSubject.INSTANCE;
        }

        public static Subject ofAuthenticatedUsers() {
            return AuthenticatedUsersSubject.INSTANCE;
        }

        public static Subject of(SpacePermission permission) {
            String group = permission.getGroup();
            ConfluenceUser userSubject = permission.getUserSubject();
            if (permission.isAnonymousPermission()) {
                return AnonymousSubject.INSTANCE;
            }
            if (permission.isAuthenticatedUsersPermission()) {
                return AuthenticatedUsersSubject.INSTANCE;
            }
            if (group != null) {
                return new GroupSubject(group);
            }
            if (userSubject != null) {
                return new UserSubject(userSubject);
            }
            throw new IllegalStateException("Unsupported permission subject: " + permission);
        }
    }
}

