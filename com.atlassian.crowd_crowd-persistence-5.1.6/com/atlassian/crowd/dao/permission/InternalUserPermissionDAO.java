/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.permission.PermittedGroup
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.permission.UserPermission
 */
package com.atlassian.crowd.dao.permission;

import com.atlassian.crowd.manager.permission.PermittedGroup;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.permission.InternalGrantedPermission;
import com.atlassian.crowd.model.permission.UserPermission;
import java.util.Collection;
import java.util.List;

public interface InternalUserPermissionDAO {
    public boolean exists(InternalGrantedPermission var1);

    public boolean revoke(InternalGrantedPermission var1);

    public int revokeAll(DirectoryMapping var1);

    public void grant(InternalGrantedPermission var1);

    public Collection<PermittedGroup> getGrantedPermissions(UserPermission var1);

    public Collection<PermittedGroup> getGrantedPermissions(UserPermission var1, Application var2);

    public List<PermittedGroup> findHighestPermissionPerGroupByPrefix(String var1, int var2, int var3);

    public List<PermittedGroup> findHighestPermissionPerGroup(int var1, int var2);

    public List<InternalGrantedPermission> findAllPermissionsForGroup(String var1, long var2);
}

