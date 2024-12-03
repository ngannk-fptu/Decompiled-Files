/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.impl.security.administrators.DefaultPermissionsAdministrator;
import com.atlassian.confluence.impl.security.administrators.EditGlobalPermissionsAdministrator;
import com.atlassian.confluence.impl.security.administrators.EditSpacePermissionsAdministrator;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.EditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.GlobalPermissionsResolver;
import com.atlassian.confluence.security.administrators.PermissionsAdministrator;
import com.atlassian.confluence.security.administrators.SpacePermissionResolver;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.confluence.user.SearchEntitiesManager;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;

public class PermissionsAdministratorBuilder {
    private SetSpacePermissionChecker setSpacePermissionChecker;
    private SpacePermissionManager spacePermissionManager;
    private UserChecker userChecker;
    private ConfluenceUserResolver userResolver;
    private GroupResolver groupResolver;

    public void setSetSpacePermissionChecker(SetSpacePermissionChecker setSpacePermissionChecker) {
        this.setSpacePermissionChecker = setSpacePermissionChecker;
    }

    @Deprecated
    public void setSearchEntitiesManager(SearchEntitiesManager searchEntitiesManager) {
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    public void setUserResolver(ConfluenceUserResolver userResolver) {
        this.userResolver = userResolver;
    }

    public void setGroupResolver(GroupResolver groupResolver) {
        this.groupResolver = groupResolver;
    }

    public EditPermissionsAdministrator buildEditSpaceAdministrator(Space space, User user, List<String> usersToAdd, List<String> groupsToAdd) {
        EditSpacePermissionsAdministrator spacePermissionsAdministrator = new EditSpacePermissionsAdministrator(this.spacePermissionManager, new SpacePermissionResolver(space), this.setSpacePermissionChecker, this.getUserResolverResolver(), this.getGroupResolver());
        spacePermissionsAdministrator.setRemoteUser(user);
        spacePermissionsAdministrator.setUsersToAdd(usersToAdd);
        spacePermissionsAdministrator.setGroupsToAdd(groupsToAdd);
        spacePermissionsAdministrator.setSpace(space);
        return spacePermissionsAdministrator;
    }

    public EditPermissionsAdministrator buildEditGlobalPermissionAdministrator(User user, List<String> usersToAdd, List<String> groupsToAdd) {
        EditGlobalPermissionsAdministrator admin = new EditGlobalPermissionsAdministrator(this.spacePermissionManager, new GlobalPermissionsResolver(this.spacePermissionManager), this.setSpacePermissionChecker, this.userChecker, this.getUserResolverResolver(), this.getGroupResolver());
        admin.setRemoteUser(user);
        admin.setUsersToAdd(usersToAdd);
        admin.setGroupsToAdd(groupsToAdd);
        return admin;
    }

    public EditPermissionsAdministrator buildEditGlobalPermissionAdministrator(User user) {
        EditGlobalPermissionsAdministrator admin = new EditGlobalPermissionsAdministrator(this.spacePermissionManager, new GlobalPermissionsResolver(this.spacePermissionManager), this.setSpacePermissionChecker, this.userChecker, this.getUserResolverResolver(), this.getGroupResolver());
        admin.setRemoteUser(user);
        admin.setUsersToAdd(new ArrayList<String>());
        admin.setGroupsToAdd(new ArrayList<String>());
        return admin;
    }

    private GroupResolver getGroupResolver() {
        return this.groupResolver;
    }

    private ConfluenceUserResolver getUserResolverResolver() {
        return this.userResolver;
    }

    public PermissionsAdministrator buildGlobalPermissionAdministrator() {
        return new DefaultPermissionsAdministrator(new GlobalPermissionsResolver(this.spacePermissionManager), this.getGroupResolver());
    }

    public PermissionsAdministrator buildSpacePermissionAdministrator(Space space) {
        return new DefaultPermissionsAdministrator(new SpacePermissionResolver(space), this.getGroupResolver());
    }
}

