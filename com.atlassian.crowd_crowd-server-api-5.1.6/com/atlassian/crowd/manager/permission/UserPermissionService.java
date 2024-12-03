/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.permission.DirectoryGroup
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.permission.UserPermission
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.permission.DirectoryGroup;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.permission.UserPermission;
import com.atlassian.crowd.model.user.User;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

public interface UserPermissionService {
    public boolean currentUserHasPermission(UserPermission var1);

    public boolean hasPermission(@Nullable String var1, UserPermission var2);

    public boolean hasPermissionOutsideOfGroups(@Nullable String var1, UserPermission var2, Collection<DirectoryGroup> var3);

    public boolean isGroupLevelAdmin(@Nullable String var1);

    @ExperimentalApi
    default public Set<User> getSysAdmins(Application application, boolean includeLocallyCachedOnly) throws DirectoryNotFoundException, OperationFailedException {
        return this.getUsersWithPermission(application, UserPermission.SYS_ADMIN, includeLocallyCachedOnly);
    }

    @ExperimentalApi
    public Set<User> getUsersWithPermission(Application var1, UserPermission var2, boolean var3) throws DirectoryNotFoundException, OperationFailedException;

    @ExperimentalApi
    public Set<User> getGroupLevelAdmins(boolean var1) throws DirectoryNotFoundException, OperationFailedException;
}

