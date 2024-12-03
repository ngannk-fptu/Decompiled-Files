/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.permission.UserPermissionService
 *  com.atlassian.crowd.model.permission.UserPermission
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mail;

import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.permission.UserPermissionService;
import com.atlassian.crowd.model.permission.UserPermission;
import com.atlassian.crowd.model.user.User;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.mail.ProductUserLists;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdUserLists
implements ProductUserLists {
    private static final Logger logger = LoggerFactory.getLogger(CrowdUserLists.class);
    private final UserPermissionService userPermissionService;
    private final ApplicationFactory applicationFactory;

    public CrowdUserLists(UserPermissionService userPermissionService, ApplicationFactory applicationFactory) {
        this.userPermissionService = userPermissionService;
        this.applicationFactory = applicationFactory;
    }

    @Override
    public Set<UserKey> getSystemAdmins() {
        return this.getUsersWithPermission(UserPermission.SYS_ADMIN);
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        return this.getUsersWithPermission(UserPermission.ADMIN);
    }

    private Set<UserKey> getUsersWithPermission(UserPermission permission) {
        try {
            HashSet<UserKey> set = new HashSet<UserKey>();
            for (User user : this.userPermissionService.getUsersWithPermission(this.applicationFactory.getApplication(), permission, false)) {
                String name = user.getName();
                UserKey userKey = new UserKey(name);
                set.add(userKey);
            }
            return set;
        }
        catch (DirectoryNotFoundException | OperationFailedException e) {
            logger.warn("Could not obtain list of Crowd users with {} permission", (Object)permission.name());
            throw new RuntimeException(e);
        }
    }
}

