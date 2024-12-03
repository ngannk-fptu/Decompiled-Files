/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.google.common.base.Preconditions;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
class DirectoryUtil {
    DirectoryUtil() {
    }

    static Iterable<Directory> getActiveDirectories(Application application) {
        Preconditions.checkNotNull((Object)application, (Object)"Application cannot be null");
        return application.getDirectoryMappings().stream().map(DirectoryMapping::getDirectory).filter(Directory::isActive).collect(Collectors.toList());
    }

    static Directory findFirstDirectoryWithCreateUserPermission(Application application, PermissionManager permissionManager) {
        return DirectoryUtil.findFirstDirectoryWithGivenPermission(application, permissionManager, OperationType.CREATE_USER);
    }

    static Directory findFirstDirectoryWithCreateGroupPermission(Application application, PermissionManager permissionManager) {
        return DirectoryUtil.findFirstDirectoryWithGivenPermission(application, permissionManager, OperationType.CREATE_GROUP);
    }

    static Directory findFirstDirectoryWithGivenPermission(Application application, PermissionManager permissionManager, OperationType permission) {
        Preconditions.checkNotNull((Object)permissionManager, (Object)"permissionManager cannot be null");
        for (Directory directory : DirectoryUtil.getActiveDirectories(application)) {
            if (!permissionManager.hasPermission(application, directory, permission)) continue;
            return directory;
        }
        return null;
    }
}

