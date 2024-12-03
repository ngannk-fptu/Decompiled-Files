/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.permission.Permission
 *  com.atlassian.bitbucket.permission.PermissionService
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.permission.PermissionService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.mail.ProductUserLists;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BitbucketUserLists
implements ProductUserLists {
    private final PermissionService permissionService;

    public BitbucketUserLists(PermissionService permissionService) {
        this.permissionService = Objects.requireNonNull(permissionService, "permissionService");
    }

    @Override
    public Set<UserKey> getSystemAdmins() {
        return Collections.unmodifiableSet(this.permissionService.getUsersWithPermission(Permission.SYS_ADMIN).stream().map(UserKey::new).collect(Collectors.toSet()));
    }

    @Override
    public Set<UserKey> getAdminsAndSystemAdmins() {
        return Collections.unmodifiableSet(this.permissionService.getUsersWithPermission(Permission.ADMIN).stream().map(UserKey::new).collect(Collectors.toSet()));
    }
}

