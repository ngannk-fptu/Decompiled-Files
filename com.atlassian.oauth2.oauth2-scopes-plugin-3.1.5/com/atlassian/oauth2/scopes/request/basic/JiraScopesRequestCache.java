/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.jira.permission.GlobalPermissionKey
 *  com.atlassian.jira.permission.ProjectPermissions
 *  com.atlassian.jira.security.plugin.ProjectPermissionKey
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request.basic;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.basic.BasicScopeRequestCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraScopesRequestCache
extends BasicScopeRequestCache {
    private static final Logger log = LoggerFactory.getLogger(JiraScopesRequestCache.class);
    static final String SERVICE_DESK_AGENT_PERMISSION_KEY = "SERVICEDESK_AGENT";
    @VisibleForTesting
    final Set<ProjectPermissionKey> allProjectPermissionKeys = ImmutableSet.copyOf((Collection)Arrays.stream(ProjectPermissions.class.getFields()).filter(projectPermissionField -> projectPermissionField.getType().equals(ProjectPermissionKey.class)).map(projectPermissionField -> {
        try {
            return (ProjectPermissionKey)projectPermissionField.get(null);
        }
        catch (IllegalAccessException e) {
            log.warn("Failed to resolve project permission field [" + projectPermissionField + "]");
            return null;
        }
    }).filter(Objects::nonNull).collect(Collectors.toSet()));
    @VisibleForTesting
    final Set<GlobalPermissionKey> allGlobalPermissionKeys = ImmutableSet.copyOf((Collection)GlobalPermissionKey.GLOBAL_PERMISSION_ID_TRANSLATION.values());

    public JiraScopesRequestCache(ScopeResolver scopeResolver) {
        super(scopeResolver);
    }

    @Override
    protected List<Permission> systemAdminPermission() {
        return ImmutableList.builder().addAll(this.adminPermission()).add((Object)Permission.permission((String)GlobalPermissionKey.SYSTEM_ADMIN.getKey())).build();
    }

    @Override
    protected List<Permission> adminPermission() {
        return ImmutableList.builder().addAll(this.writePermission()).add((Object)Permission.permission((String)GlobalPermissionKey.ADMINISTER.getKey())).add((Object)Permission.permission((String)ProjectPermissions.ADMINISTER_PROJECTS.permissionKey())).build();
    }

    @Override
    protected List<Permission> writePermission() {
        return ImmutableList.builder().addAll(this.getAllGlobalPermissionsExceptAdmin()).addAll(this.getAllProjectPermissionsExceptAdmin()).build();
    }

    Collection<Permission> getAllProjectPermissionsExceptAdmin() {
        return Stream.concat(this.allProjectPermissionKeys.stream().filter(projectPermissionKey -> !ProjectPermissions.ADMINISTER_PROJECTS.equals(projectPermissionKey)).map(projectPermissionKey -> Permission.permission((String)projectPermissionKey.permissionKey())), Stream.of(Permission.permission((String)SERVICE_DESK_AGENT_PERMISSION_KEY))).collect(Collectors.toSet());
    }

    Collection<Permission> getAllGlobalPermissionsExceptAdmin() {
        return this.allGlobalPermissionKeys.stream().filter(globalPermissionKey -> !GlobalPermissionKey.ADMINISTER.equals(globalPermissionKey) && !GlobalPermissionKey.SYSTEM_ADMIN.equals(globalPermissionKey)).map(globalPermissionKey -> Permission.permission((String)globalPermissionKey.getKey())).collect(Collectors.toSet());
    }

    @Override
    protected List<Permission> readPermission() {
        return ImmutableList.of((Object)Permission.permission((String)ProjectPermissions.VIEW_DEV_TOOLS.permissionKey()), (Object)Permission.permission((String)ProjectPermissions.VIEW_READONLY_WORKFLOW.permissionKey()), (Object)Permission.permission((String)ProjectPermissions.VIEW_VOTERS_AND_WATCHERS.permissionKey()), (Object)Permission.permission((String)ProjectPermissions.BROWSE_PROJECTS.permissionKey()), (Object)Permission.permission((String)ProjectPermissions.BROWSE_ARCHIVE.permissionKey()), (Object)Permission.permission((String)SERVICE_DESK_AGENT_PERMISSION_KEY));
    }
}

