/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.permission.AnonymousUserPermissionException
 *  com.atlassian.crowd.manager.permission.DirectoryGroup
 *  com.atlassian.crowd.manager.permission.PermittedGroup
 *  com.atlassian.crowd.manager.permission.UserPermissionDowngradeException
 *  com.atlassian.crowd.model.permission.UserPermission
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.permission.AnonymousUserPermissionException;
import com.atlassian.crowd.manager.permission.DirectoryGroup;
import com.atlassian.crowd.manager.permission.PermittedGroup;
import com.atlassian.crowd.manager.permission.UserPermissionDowngradeException;
import com.atlassian.crowd.manager.permission.UserPermissionException;
import com.atlassian.crowd.model.page.Page;
import com.atlassian.crowd.model.permission.UserPermission;
import java.util.List;
import javax.annotation.Nonnull;

public interface UserPermissionAdminService {
    public void setPermissionForGroups(List<? extends DirectoryGroup> var1, UserPermission var2) throws DirectoryNotFoundException, OperationFailedException, ApplicationNotFoundException, UserPermissionDowngradeException, AnonymousUserPermissionException;

    public void revokePermissionsForGroup(DirectoryGroup var1) throws DirectoryNotFoundException, OperationFailedException, ApplicationNotFoundException, UserPermissionDowngradeException, AnonymousUserPermissionException;

    public Page<PermittedGroup> findGroupsWithPermissionByPrefix(@Nonnull String var1, int var2, int var3) throws UserPermissionException, AnonymousUserPermissionException;

    public Page<PermittedGroup> findGroupsWithPermission(int var1, int var2) throws UserPermissionException, AnonymousUserPermissionException;

    public Page<DirectoryGroup> findGroupsByPrefix(@Nonnull String var1, int var2, int var3) throws AnonymousUserPermissionException;

    public Page<DirectoryGroup> findGroups(int var1, int var2) throws AnonymousUserPermissionException;
}

