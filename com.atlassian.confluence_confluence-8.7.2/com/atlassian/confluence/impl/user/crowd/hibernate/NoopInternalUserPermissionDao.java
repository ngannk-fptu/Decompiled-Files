/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.permission.InternalUserPermissionDAO
 *  com.atlassian.crowd.manager.permission.PermittedGroup
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.permission.InternalGrantedPermission
 *  com.atlassian.crowd.model.permission.UserPermission
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.dao.permission.InternalUserPermissionDAO;
import com.atlassian.crowd.manager.permission.PermittedGroup;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.permission.InternalGrantedPermission;
import com.atlassian.crowd.model.permission.UserPermission;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NoopInternalUserPermissionDao
implements InternalUserPermissionDAO {
    public boolean exists(InternalGrantedPermission permission) {
        return false;
    }

    public boolean revoke(InternalGrantedPermission permission) {
        return false;
    }

    public int revokeAll(DirectoryMapping directoryMapping) {
        return 0;
    }

    public void grant(InternalGrantedPermission permission) {
    }

    public Collection<PermittedGroup> getGrantedPermissions(UserPermission permission) {
        return Collections.emptyList();
    }

    public Collection<PermittedGroup> getGrantedPermissions(UserPermission permission, Application application) {
        return Collections.emptyList();
    }

    public List<PermittedGroup> findHighestPermissionPerGroupByPrefix(String prefix, int start, int limit) {
        return Collections.emptyList();
    }

    public List<PermittedGroup> findHighestPermissionPerGroup(int start, int limit) {
        return Collections.emptyList();
    }

    public List<InternalGrantedPermission> findAllPermissionsForGroup(String groupName, long directoryId) {
        return Collections.emptyList();
    }
}

