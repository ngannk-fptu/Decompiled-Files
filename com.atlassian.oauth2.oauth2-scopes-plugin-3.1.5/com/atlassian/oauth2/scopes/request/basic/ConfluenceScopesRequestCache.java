/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request.basic;

import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.basic.BasicScopeRequestCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceScopesRequestCache
extends BasicScopeRequestCache {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceScopesRequestCache.class);
    private volatile List<com.atlassian.confluence.security.Permission> allGlobalPermissions = Arrays.stream(com.atlassian.confluence.security.Permission.class.getFields()).filter(permissionField -> permissionField.getType().equals(com.atlassian.confluence.security.Permission.class)).map(projectPermissionField -> {
        try {
            return (com.atlassian.confluence.security.Permission)projectPermissionField.get(null);
        }
        catch (IllegalAccessException e) {
            log.warn("Failed to resolve project permission field [" + projectPermissionField + "]");
            return null;
        }
    }).filter(Objects::nonNull).collect(Collectors.toList());

    public ConfluenceScopesRequestCache(ScopeResolver scopeResolver) {
        super(scopeResolver);
    }

    @Override
    protected List<Permission> systemAdminPermission() {
        return ImmutableSet.builder().addAll(this.writePermission()).add((Object)Permission.permissionWithTarget((String)com.atlassian.confluence.security.Permission.ADMINISTER.toString(), (Object)PermissionManager.TARGET_SYSTEM)).add((Object)Permission.permission((String)"SETSPACEPERMISSIONS")).add((Object)Permission.permission((String)"SYSTEMADMINISTRATOR")).add((Object)Permission.permission((String)OperationKey.ADMINISTER.getValue())).build().asList();
    }

    @Override
    protected List<Permission> adminPermission() {
        return ImmutableSet.builder().addAll(this.writePermission()).add((Object)Permission.permissionWithTargetExcluded((String)com.atlassian.confluence.security.Permission.ADMINISTER.toString(), (Object)PermissionManager.TARGET_SYSTEM)).add((Object)Permission.permission((String)"SETSPACEPERMISSIONS")).add((Object)Permission.permission((String)OperationKey.ADMINISTER.getValue())).build().asList();
    }

    @Override
    protected List<Permission> writePermission() {
        return ImmutableSet.builder().addAll(this.getAllGlobalPermissionsExceptAdmin()).addAll(this.getAllSpacePermissionsExceptAdmin()).addAll(this.getAllOperationKeysExceptAdmin()).addAll(this.getAllContentPermissions()).build().asList();
    }

    Collection<Permission> getAllGlobalPermissionsExceptAdmin() {
        return this.allGlobalPermissions.stream().filter(confluencePermission -> !com.atlassian.confluence.security.Permission.ADMINISTER.equals(confluencePermission)).map(confluencePermission -> Permission.permission((String)confluencePermission.toString())).collect(Collectors.toSet());
    }

    Collection<Permission> getAllSpacePermissionsExceptAdmin() {
        return SpacePermission.PERMISSION_TYPES.stream().filter(spacePermission -> !"SYSTEMADMINISTRATOR".equals(spacePermission) && !"SETSPACEPERMISSIONS".equals(spacePermission)).map(Permission::permission).collect(Collectors.toSet());
    }

    Collection<Permission> getAllOperationKeysExceptAdmin() {
        return OperationKey.BUILT_IN.stream().filter(operationKey -> !OperationKey.ADMINISTER.equals(operationKey)).map(operationKey -> Permission.permission((String)operationKey.getValue())).collect(Collectors.toSet());
    }

    private List<Permission> getAllContentPermissions() {
        return ImmutableList.of((Object)Permission.permission((String)"Edit"), (Object)Permission.permission((String)"Share"), (Object)Permission.permission((String)"View"));
    }

    @Override
    protected List<Permission> readPermission() {
        return ImmutableSet.builder().add((Object[])new Permission[]{Permission.permission((String)com.atlassian.confluence.security.Permission.VIEW.toString()), Permission.permission((String)"VIEWSPACE"), Permission.permission((String)OperationKey.READ.getValue()), Permission.permission((String)"View")}).build().asList();
    }
}

