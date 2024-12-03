/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.event.events.permission.GlobalPermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.GlobalPermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionRemoveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionSaveEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForGroupEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveForUserEvent;
import com.atlassian.confluence.event.events.permission.SpacePermissionsRemoveFromSpaceEvent;
import com.atlassian.confluence.impl.security.AbstractSpacePermissionManager;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionDefaultsStore;
import com.atlassian.confluence.security.SpacePermissionDefaultsStoreFactory;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultSpacePermissionManager
extends AbstractSpacePermissionManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpacePermissionManager.class);
    private final SpacePermissionDao spacePermissionDao;
    private final SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory;
    private final EventPublisher eventPublisher;
    private final ConfluenceUserResolver userResolver;
    private final GroupResolver groupResolver;

    protected DefaultSpacePermissionManager(SpacePermissionDao spacePermissionDao, PermissionCheckExemptions permissionCheckExemptions, SpacePermissionDefaultsStoreFactory spacePermissionDefaultsStoreFactory, EventPublisher eventPublisher, ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, ConfluenceUserResolver userResolver, AccessModeManager accessModeManager, ScopesRequestCacheDelegate scopesRequestCacheDelegate, GlobalSettingsManager settingsManager, GroupResolver groupResolver) {
        super(permissionCheckExemptions, confluenceAccessManager, spacePermissionAccessMapper, crowdService, accessModeManager, scopesRequestCacheDelegate, settingsManager);
        this.spacePermissionDao = spacePermissionDao;
        this.spacePermissionDefaultsStoreFactory = spacePermissionDefaultsStoreFactory;
        this.eventPublisher = eventPublisher;
        this.userResolver = userResolver;
        this.groupResolver = groupResolver;
    }

    @Override
    public void flushCaches() {
    }

    @Override
    public void savePermission(SpacePermission permission) {
        this.savePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void savePermission(SpacePermission permission, SpacePermissionContext context) {
        if (permission.isInvalidAnonymousPermission()) {
            throw new IllegalArgumentException("You are not allowed to add the " + permission.getType() + " permission to the anonymous user.");
        }
        if (permission.isInvalidAuthenticatedUsersPermission()) {
            throw new IllegalArgumentException("You are not allowed to add the " + permission.getType() + " permission to all authenticated / logged-in users.");
        }
        this.savePermissionToDao(permission);
        if (permission.getType().equals("USECONFLUENCE")) {
            if (permission.isUserPermission() || permission.isAnonymousPermission()) {
                ThreadLocalPermissionsCacheInternal.flushUserAccessStatusForUser(permission.getUserSubject());
            } else {
                ThreadLocalPermissionsCacheInternal.flushUserAccessStatusForAllUsers();
            }
        }
        if (context.shouldSendEvents()) {
            if (permission.getSpace() != null) {
                this.eventPublisher.publish((Object)new SpacePermissionSaveEvent((Object)this, permission));
            } else {
                this.eventPublisher.publish((Object)new GlobalPermissionSaveEvent(this, permission));
            }
        }
    }

    @Override
    public void removePermission(SpacePermission permission) {
        this.removePermission(permission, SpacePermissionContext.createDefault());
    }

    @Override
    public void removePermission(SpacePermission permission, SpacePermissionContext context) {
        Space space;
        SpacePermission realPermission = this.spacePermissionDao.getById(permission.getId());
        if (realPermission == null) {
            log.error("Could not retrieve a space permission from Hibernate with the id '" + permission.getId() + "'");
            return;
        }
        this.removePermissionFromDao(realPermission);
        if (permission.getType().equals("USECONFLUENCE")) {
            if (permission.isUserPermission() || permission.isAnonymousPermission()) {
                ThreadLocalPermissionsCacheInternal.flushUserAccessStatusForUser(permission.getUserSubject());
            } else {
                ThreadLocalPermissionsCacheInternal.flushUserAccessStatusForAllUsers();
            }
        }
        if ((space = realPermission.getSpace()) != null) {
            space.removePermission(realPermission);
        }
        if (context.shouldSendEvents()) {
            if (space != null) {
                this.eventPublisher.publish((Object)new SpacePermissionRemoveEvent((Object)this, realPermission, space));
            } else {
                this.eventPublisher.publish((Object)new GlobalPermissionRemoveEvent(this, realPermission));
            }
        }
    }

    @Override
    public List<SpacePermission> getGlobalPermissions() {
        return this.spacePermissionDao.findAllGlobalPermissions();
    }

    @Override
    public List<SpacePermission> getGlobalPermissions(String permissionType) {
        return this.spacePermissionDao.findAllGlobalPermissionsForType(permissionType);
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user) {
        this.removeAllUserPermissions(user, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllUserPermissions(ConfluenceUser user, SpacePermissionContext context) {
        List<SpacePermission> permissions = this.getAllPermissionsForUser(user);
        SpacePermissionContext subContext = SpacePermissionContext.builder(context).sendEvents(false).build();
        if (context.shouldSendEvents()) {
            this.eventPublisher.publish((Object)new SpacePermissionsRemoveForUserEvent((Object)this, user, permissions));
        }
        this.removePermissions(permissions, subContext);
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType) {
        this.removeGlobalPermissionForUser(user, permissionType, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeGlobalPermissionForUser(ConfluenceUser user, String permissionType, SpacePermissionContext context) {
        List<SpacePermission> globalPermissions = this.getAllPermissionsForUser(user);
        for (SpacePermission permission : globalPermissions) {
            if (!permission.isGlobalPermission() || !permissionType.equals(permission.getType())) continue;
            this.removePermission(permission, context);
        }
    }

    @Override
    public void removeAllPermissionsForGroup(String group) {
        this.removeAllPermissionsForGroup(group, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissionsForGroup(String group, SpacePermissionContext context) {
        List<SpacePermission> permissions = this.getAllPermissionsForGroup(group);
        SpacePermissionContext subContext = SpacePermissionContext.builder(context).sendEvents(false).build();
        this.removePermissions(permissions, subContext);
        if (context.shouldSendEvents()) {
            this.eventPublisher.publish((Object)new SpacePermissionsRemoveForGroupEvent((Object)this, group, permissions));
        }
    }

    private void removePermissions(List<SpacePermission> permissions, SpacePermissionContext context) {
        for (SpacePermission spacePermission : permissions) {
            this.removePermission(spacePermission, context);
        }
    }

    @Override
    public List<SpacePermission> getAllPermissionsForGroup(String group) {
        return this.spacePermissionDao.findPermissionsForGroup(group);
    }

    protected List<SpacePermission> getAllPermissionsForUser(ConfluenceUser user) {
        return this.spacePermissionDao.findPermissionsForUser(user);
    }

    @Override
    public void removeAllPermissions(Space space) {
        this.removeAllPermissions(space, SpacePermissionContext.createDefault());
    }

    @Override
    public void removeAllPermissions(Space space, SpacePermissionContext context) {
        log.debug("CONFDEV-1045 removeAllPermissions for space {}", (Object)space.getName());
        this.spacePermissionDao.refresh(space);
        ImmutableList permissions = ImmutableList.copyOf(space.getPermissions());
        this.removeAllPermissionsFromDao(space);
        space.removeAllPermissions();
        if (context.shouldSendEvents()) {
            this.eventPublisher.publish((Object)new SpacePermissionsRemoveFromSpaceEvent(this, space, (List<SpacePermission>)permissions, context.getUpdateTrigger()));
        }
    }

    protected void removeAllPermissionsFromDao(Space space) {
        this.spacePermissionDao.removePermissionsForSpace(space);
    }

    private void createInitialSpacePermissions(Space space, SpacePermissionContext context) {
        for (String permissionType : SpacePermission.GENERIC_SPACE_PERMISSIONS) {
            SpacePermission permission = SpacePermission.createUserSpacePermission(permissionType, space, space.getCreator());
            space.addPermission(permission);
            this.savePermission(permission, context);
        }
    }

    @Override
    public void createPrivateSpacePermissions(Space space) {
        SpacePermissionContext context = SpacePermissionContext.builder().updateTrigger(SpaceUpdateTrigger.SPACE_CREATED).sendEvents(false).build();
        this.createInitialSpacePermissions(space, context);
    }

    @Override
    public void createDefaultSpacePermissions(Space space) {
        SpacePermissionContext context = SpacePermissionContext.builder().updateTrigger(SpaceUpdateTrigger.SPACE_CREATED).sendEvents(false).build();
        this.createInitialSpacePermissions(space, context);
        SpacePermissionDefaultsStore spacePermissionDefaultsStore = this.spacePermissionDefaultsStoreFactory.createStore();
        Set<SpacePermission> defaultPermissions = spacePermissionDefaultsStore.createPermissionsForSpace(space);
        for (SpacePermission spacePermission : defaultPermissions) {
            this.savePermission(spacePermission, context);
        }
    }

    @Override
    public Collection<User> getUsersWithPermissions(@Nullable Space space) {
        String permissionType = space == null ? "USECONFLUENCE" : "VIEWSPACE";
        ArrayList<User> result = new ArrayList<User>();
        Map<String, Long> users = this.getUsersForPermissionType(permissionType, space);
        for (String userName : users.keySet()) {
            ConfluenceUser user = this.userResolver.getUserByName(userName);
            result.add(user);
        }
        return result;
    }

    @Override
    public Collection<Group> getGroupsWithPermissions(@Nullable Space space) {
        String permissionType = space == null ? "USECONFLUENCE" : "VIEWSPACE";
        ArrayList<Group> result = new ArrayList<Group>();
        Map<String, Long> groups = this.getGroupsForPermissionType(permissionType, space);
        for (String groupName : groups.keySet()) {
            Group group = this.groupResolver.getGroup(groupName);
            if (group == null) continue;
            result.add(group);
        }
        return result;
    }

    @Override
    public Map<String, Long> getUsersForPermissionType(String permissionType, @Nullable Space space) {
        Collator c = Collator.getInstance();
        c.setStrength(1);
        TreeMap<Object, Long> result = new TreeMap<Object, Long>(c);
        ArrayList<SpacePermission> permissions = space == null ? new ArrayList<SpacePermission>(this.getGlobalPermissions()) : new ArrayList<SpacePermission>(space.getPermissions());
        for (SpacePermission spacePermission : permissions) {
            ConfluenceUser user;
            String username;
            if (!spacePermission.isUserPermission() || !spacePermission.getType().equalsIgnoreCase(permissionType) || result.get(username = spacePermission.getUserName()) != null || (user = this.userResolver.getUserByName(username)) == null) continue;
            result.put(username, spacePermission.getId());
        }
        return result;
    }

    @Override
    public Map<String, Long> getGroupsForPermissionType(String permissionType, @Nullable Space space) {
        Collator c = Collator.getInstance();
        c.setStrength(1);
        TreeMap<Object, Long> result = new TreeMap<Object, Long>(c);
        ArrayList<SpacePermission> permissions = space == null ? new ArrayList<SpacePermission>(this.getGlobalPermissions()) : new ArrayList<SpacePermission>(space.getPermissions());
        for (SpacePermission spacePermission : permissions) {
            Group group;
            String groupName;
            if (!spacePermission.isGroupPermission() || !spacePermission.getType().equalsIgnoreCase(permissionType) || result.get(groupName = spacePermission.getGroup()) != null || (group = this.groupResolver.getGroup(groupName)) == null) continue;
            result.put(groupName, spacePermission.getId());
        }
        return result;
    }

    protected Set<String> getGroupNamesWithPermission(@Nullable Space targetSpace, String permissionType) {
        return DefaultSpacePermissionManager.queryDaoForGroupNamesWithPermission(targetSpace, permissionType, this.spacePermissionDao);
    }

    @Internal
    static Set<String> queryDaoForGroupNamesWithPermission(@Nullable Space targetSpace, String permissionType, SpacePermissionDao spacePermissionDao) {
        return targetSpace == null ? DefaultSpacePermissionManager.groupNamesFrom(spacePermissionDao.findGlobalGroupPermissions(permissionType)) : DefaultSpacePermissionManager.groupNamesFrom(spacePermissionDao.findGroupPermissionsForSpace(targetSpace, permissionType));
    }

    private static Set<String> groupNamesFrom(Collection<SpacePermission> permissions) {
        return ImmutableSet.copyOf((Collection)Collections2.transform(permissions, SpacePermission::getGroup));
    }

    @Override
    public boolean permissionExists(SpacePermission permission) {
        Space space = permission.getSpace();
        if (space != null && space.getId() == 0L) {
            return false;
        }
        if (space != null) {
            return space.getPermissions().contains(permission);
        }
        return this.spacePermissionDao.hasPermission(permission);
    }

    protected void savePermissionToDao(SpacePermission permission) {
        log.debug("CONFDEV-1045: savePermissionToDao " + permission);
        this.spacePermissionDao.save(permission);
    }

    protected void removePermissionFromDao(SpacePermission realPermission) {
        log.debug("CONFDEV-1045: removePermissionFromDao " + realPermission);
        this.spacePermissionDao.remove(realPermission);
    }
}

