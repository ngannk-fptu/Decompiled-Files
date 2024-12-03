/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.FeatureInaccessibleException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.FeatureInaccessibleException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.permission.AdministeredGroupsQuery;
import com.atlassian.crowd.manager.permission.UserGroupAdministrationMapping;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface GroupAdministrationPermissionService {
    public List<UserGroupAdministrationMapping> getAdministeredGroupsForCurrentUser(@Nonnull AdministeredGroupsQuery var1) throws OperationFailedException, FeatureInaccessibleException;

    public List<UserGroupAdministrationMapping> getAdministeredGroups(User var1, @Nonnull AdministeredGroupsQuery var2) throws OperationFailedException, FeatureInaccessibleException;

    public boolean isUserGroupLevelAdmin(User var1) throws OperationFailedException;

    public boolean isCurrentUserAdminOfGroup(Group var1) throws GroupNotFoundException, UserNotFoundException, DirectoryNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public boolean isUserAdminOfGroup(User var1, Group var2) throws GroupNotFoundException, UserNotFoundException, DirectoryNotFoundException, OperationFailedException, FeatureInaccessibleException;

    public Set<User> getDirectGroupLevelAdminUsers(boolean var1);

    public Set<Group> getDirectGroupLevelAdminGroups(boolean var1);
}

