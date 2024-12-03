/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.impl.backuprestore;

import com.atlassian.confluence.backuprestore.exception.NotPermittedException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.Collection;
import java.util.List;

public class BackupRestorePermissionsHelper {
    private static final String ONLY_SYSADMIN_CAN_PERFORM_THIS_OPERATION = "backup-restore.only-sysadmin-can-perform-this-operation";
    private static final String SPACE_NOT_FOUND = "Space not found. SpaceKey: %s";
    private static final String EMPTY_SPACE_LIST = "backup-restore.empty-space-list";
    private static final String USER_CANNOT_CANCEL_SPACE_EXPORT = "backup-restore.user-does-not-have-permissions-to-cancel-space-export";
    private static final String USER_CANNOT_EXPORT_SPACE = "backup-restore.user-does-not-have-permissions-to-export-space";
    private final PermissionManager permissionManager;
    private final SpaceDao spaceDao;
    private final SpacePermissionManager spacePermissionManager;

    public BackupRestorePermissionsHelper(PermissionManager permissionManager, SpaceDao spaceDao, SpacePermissionManager spacePermissionManager) {
        this.permissionManager = permissionManager;
        this.spaceDao = spaceDao;
        this.spacePermissionManager = spacePermissionManager;
    }

    boolean hasSysadminPermissions() {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    void assertUserHasSystemAdminPermissions() throws NotPermittedException {
        if (!this.hasSysadminPermissions()) {
            throw new NotPermittedException(ONLY_SYSADMIN_CAN_PERFORM_THIS_OPERATION);
        }
    }

    void assertUserCanCancelSpaceBackup(Collection<String> spaceKeys) throws NotPermittedException {
        if (this.hasSysadminPermissions() || this.hasPermissionToBackupSpaces(spaceKeys)) {
            return;
        }
        throw new NotPermittedException(USER_CANNOT_CANCEL_SPACE_EXPORT);
    }

    void assertUserCanBackupSpaces(Collection<String> spaceKeys) throws NotPermittedException {
        if (this.hasPermissionToBackupSpaces(spaceKeys)) {
            return;
        }
        throw new NotPermittedException(USER_CANNOT_EXPORT_SPACE);
    }

    boolean hasPermissionToBackupSpaces(Collection<String> spaceKeys) {
        return this.hasPermissionsToSpaces(spaceKeys, List.of("SETSPACEPERMISSIONS", "EXPORTSPACE"));
    }

    private boolean hasPermissionsToSpaces(Collection<String> spaceKeys, List<String> permissionTypes) throws IllegalArgumentException {
        if (spaceKeys.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_SPACE_LIST);
        }
        boolean hasSysAdminPermissions = this.hasSysadminPermissions();
        for (String spaceKey : spaceKeys) {
            Space space = this.spaceDao.getSpace(spaceKey);
            if (space == null) {
                throw new IllegalArgumentException(String.format(SPACE_NOT_FOUND, spaceKey));
            }
            if (hasSysAdminPermissions || this.spacePermissionManager.hasAllPermissions(permissionTypes, space, AuthenticatedUserThreadLocal.get())) continue;
            return false;
        }
        return true;
    }
}

