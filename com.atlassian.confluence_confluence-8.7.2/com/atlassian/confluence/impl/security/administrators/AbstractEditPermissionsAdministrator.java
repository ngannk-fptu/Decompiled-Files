/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.security.administrators;

import com.atlassian.confluence.impl.security.administrators.DefaultPermissionsAdministrator;
import com.atlassian.confluence.security.PermissionsFormHandler;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.EditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEditPermissionsAdministrator
extends DefaultPermissionsAdministrator
implements EditPermissionsAdministrator {
    private static final Logger log = LoggerFactory.getLogger(AbstractEditPermissionsAdministrator.class);
    private List<String> groupsToAdd;
    private List<String> usersToAdd;
    private final SpacePermissionManager spacePermissionManager;
    private final SetSpacePermissionChecker setSpacePermissionChecker;
    private final ConfluenceUserResolver userResolver;
    private final GroupResolver groupResolver;
    protected User remoteUser;
    public static final String PARAMETER_PREFIX = "confluence_";

    protected AbstractEditPermissionsAdministrator(SpacePermissionManager spacePermissionManager, PermissionResolver permissionResolver, SetSpacePermissionChecker setSpacePermissionChecker, ConfluenceUserResolver userResolver, GroupResolver groupResolver) {
        super(permissionResolver, groupResolver);
        this.spacePermissionManager = spacePermissionManager;
        this.setSpacePermissionChecker = setSpacePermissionChecker;
        this.groupResolver = groupResolver;
        this.userResolver = userResolver;
    }

    @Override
    public boolean isGroupsToAddTooLarge(Map requestParams) {
        if (requestParams.containsKey("groupsToAddButton")) {
            return this.groupsToAdd.size() > MAX_ENTRIES;
        }
        return false;
    }

    @Override
    public boolean isGroupsToAddEmpty(Map requestParams) {
        if (requestParams.containsKey("groupsToAddButton")) {
            return this.groupsToAdd.isEmpty();
        }
        return false;
    }

    @Override
    public int getNumOfGroupEntries() {
        return this.groupsToAdd.size();
    }

    @Override
    public boolean isUsersToAddTooLarge(Map requestParams) {
        if (requestParams.containsKey("usersToAddButton")) {
            return this.usersToAdd.size() > MAX_ENTRIES;
        }
        return false;
    }

    @Override
    public boolean isUsersToAddEmpty(Map requestParams) {
        if (requestParams.containsKey("usersToAddButton")) {
            return this.usersToAdd.isEmpty();
        }
        return false;
    }

    @Override
    public int getNumOfUserEntries() {
        return this.usersToAdd.size();
    }

    @Override
    public void splitPermissions(Collection<SpacePermission> existingPermissions, Collection<SpacePermission> initialPermissions, Collection<SpacePermission> requestedPermissions, Set<SpacePermission> permissionsToAdd, Set<SpacePermission> permissionsToRemove) {
        ArrayList<SpacePermission> removedGuardPermissions = new ArrayList<SpacePermission>();
        permissionsToAdd.addAll(requestedPermissions);
        permissionsToAdd.removeAll(initialPermissions);
        permissionsToAdd.removeAll(existingPermissions);
        for (SpacePermission spacePermission : existingPermissions) {
            if (!initialPermissions.contains(spacePermission) || requestedPermissions.contains(spacePermission)) continue;
            permissionsToRemove.add(spacePermission);
            removedGuardPermissions.add(spacePermission);
        }
        this.removeDependentPermissions(removedGuardPermissions, existingPermissions, permissionsToAdd, permissionsToRemove);
    }

    protected Collection<SpacePermission> buildPermissionsFromWebForm(Space space, Map<String, ?> formParameters, String parameterQualifier) {
        PermissionsFormHandler formHandler = new PermissionsFormHandler(this.userResolver);
        ArrayList<SpacePermission> permissions = new ArrayList<SpacePermission>();
        String expectedPrefix = PARAMETER_PREFIX + parameterQualifier + "_";
        for (Map.Entry<String, ?> entry : formParameters.entrySet()) {
            if (!entry.getKey().startsWith(expectedPrefix)) continue;
            try {
                permissions.add(formHandler.fromFormParameterName(entry.getKey(), space, parameterQualifier));
            }
            catch (ParseException e) {
                log.debug("Unrecognised space permission parameter: " + e.getMessage(), (Throwable)e);
            }
        }
        return permissions;
    }

    private void removeDependentPermissions(List<SpacePermission> removedGuardPermissions, Collection<SpacePermission> existingPermissions, Set<SpacePermission> permissionsToAdd, Set<SpacePermission> permissionsToRemove) {
        for (SpacePermission guardPermission : removedGuardPermissions) {
            Iterator<SpacePermission> it2 = permissionsToAdd.iterator();
            while (it2.hasNext()) {
                if (!it2.next().isDependentOn(guardPermission)) continue;
                it2.remove();
            }
            for (SpacePermission existingPermission : existingPermissions) {
                if (!existingPermission.isDependentOn(guardPermission)) continue;
                permissionsToRemove.add(existingPermission);
            }
        }
    }

    @Override
    public boolean isRemoveAllAdminPermissions(Set<SpacePermission> permissionsToRemove) {
        if (permissionsToRemove.isEmpty()) {
            return false;
        }
        int numAdminPermissionsBeingRemoved = this.getAdministrativePermissions(permissionsToRemove).size();
        int numExistingAdministrationPermissions = this.getAdministrativePermissions(this.getPermissions()).size();
        return numExistingAdministrationPermissions > 0 && numAdminPermissionsBeingRemoved == numExistingAdministrationPermissions;
    }

    @Override
    public void denyAnonymousPermissions(Collection<SpacePermission> currentPermissions, Set<SpacePermission> permissionsToAdd, Set<SpacePermission> permissionsToRemove) {
        for (SpacePermission perm : currentPermissions) {
            if (!perm.isAnonymousPermission()) continue;
            permissionsToRemove.add(perm);
        }
        Iterator<SpacePermission> iterator = permissionsToAdd.iterator();
        while (iterator.hasNext()) {
            SpacePermission perm;
            perm = iterator.next();
            if (!perm.isAnonymousPermission()) continue;
            iterator.remove();
        }
    }

    @Override
    public void addAllPermissions(Set<SpacePermission> permissionsToAdd) {
        for (SpacePermission spacePermission : permissionsToAdd) {
            this.addPermission(spacePermission);
        }
    }

    @Override
    public void removeAllPermissions(Set<SpacePermission> permissionsToRemove) {
        for (SpacePermission spacePermission : permissionsToRemove) {
            this.removePermission(spacePermission);
        }
    }

    private Set<SpacePermission> getAdministrativePermissions(Collection<SpacePermission> permissions) {
        HashSet<SpacePermission> result = new HashSet<SpacePermission>();
        String adminPermissionsType = this.getAdministrativePermissionType();
        for (SpacePermission spacePermission : permissions) {
            if (!spacePermission.getType().equals(adminPermissionsType)) continue;
            result.add(spacePermission);
        }
        return result;
    }

    protected boolean canAddPermission(SpacePermission permission) {
        return !this.isPermissionExists(permission) && !permission.isInvalidAnonymousPermission() && !permission.isInvalidAuthenticatedUsersPermission() && this.setSpacePermissionChecker.canSetPermission(this.remoteUser, permission);
    }

    private boolean isPermissionExists(SpacePermission spacePermission) {
        return this.getPermissions().contains(spacePermission);
    }

    @Override
    public void removePermission(SpacePermission permissionToRemove) {
        if (this.setSpacePermissionChecker.canSetPermission(this.remoteUser, permissionToRemove)) {
            this.getPermissions().remove(permissionToRemove);
            this.spacePermissionManager.removePermission(permissionToRemove);
        }
    }

    @Override
    public List<String> addGuardPermissionToGroups(List<String> groupNames, String guardPermission) {
        return this.addGuardPermissionToGroups(groupNames, this.groupResolver, guardPermission);
    }

    @Override
    public List<String> addGuardPermissionToUsers(List<String> userNames, String guardPermission) {
        return this.addGuardPermissionToUsers(userNames, this.userResolver, guardPermission);
    }

    @Override
    public List<String> addGuardPermissionToUsers(List<String> userNames, UserAccessor userAccessor, String guardPermission) {
        return this.addGuardPermissionToUsers(userNames, (ConfluenceUserResolver)userAccessor, guardPermission);
    }

    private List<String> addGuardPermissionToUsers(List<String> userNames, ConfluenceUserResolver userResolver, String guardPermission) {
        ArrayList<String> userNamesThatCouldNotBeAdded = new ArrayList<String>();
        for (String userName : userNames) {
            String userNamedTrimmed = userName.trim();
            ConfluenceUser user = userResolver.getUserByName(userNamedTrimmed);
            if (user != null) {
                SpacePermission userGuardPermission = this.createUserGuardPermission(guardPermission, user);
                this.addPermission(userGuardPermission);
                continue;
            }
            userNamesThatCouldNotBeAdded.add(userName.trim());
        }
        return userNamesThatCouldNotBeAdded;
    }

    @Override
    public List<String> addGuardPermissionToGroups(List<String> groupNames, UserAccessor userAccessor, String guardPermission) {
        return this.addGuardPermissionToGroups(groupNames, (GroupResolver)userAccessor, guardPermission);
    }

    private List<String> addGuardPermissionToGroups(List<String> groupNames, GroupResolver groupResolver, String guardPermission) {
        ArrayList<String> groupNamesThatCouldNotBeAdded = new ArrayList<String>();
        for (String groupName : groupNames) {
            Group group = groupResolver.getGroup(groupName.trim());
            if (group != null) {
                this.addPermission(this.createGroupGuardPermission(guardPermission, group.getName()));
                continue;
            }
            groupNamesThatCouldNotBeAdded.add(groupName.trim());
        }
        return groupNamesThatCouldNotBeAdded;
    }

    public void setUsersToAdd(List<String> usersToAdd) {
        this.usersToAdd = usersToAdd;
    }

    public void setGroupsToAdd(List<String> groupsToAdd) {
        this.groupsToAdd = groupsToAdd;
    }

    public void setRemoteUser(User remoteUser) {
        this.remoteUser = remoteUser;
    }

    @Override
    public void applyPermissionChanges(Collection<SpacePermission> oldPermissions, Collection<SpacePermission> newPermissions) throws IllegalArgumentException {
        List<SpacePermission> currentPermissions = this.getPermissions();
        HashSet<SpacePermission> permissionsToRemove = new HashSet<SpacePermission>();
        HashSet<SpacePermission> permissionsToAdd = new HashSet<SpacePermission>();
        this.splitPermissions(currentPermissions, oldPermissions, newPermissions, permissionsToAdd, permissionsToRemove);
        if (this.isRemoveAllAdminPermissions(permissionsToRemove) && this.getAdministrativePermissions(permissionsToAdd).isEmpty()) {
            throw new IllegalArgumentException("Can't remove all Admin permissions");
        }
        this.addAllPermissions(permissionsToAdd);
        this.removeAllPermissions(permissionsToRemove);
    }
}

