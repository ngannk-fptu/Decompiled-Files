/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.permission.Permission
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request.bitbucket;

import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.DefaultScopesRequestCache;
import com.atlassian.oauth2.scopes.request.bitbucket.BitbucketScope;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitbucketScopesRequestCache
extends DefaultScopesRequestCache {
    private static final Logger log = LoggerFactory.getLogger(BitbucketScopesRequestCache.class);
    private final ScopeResolver scopeResolver;

    public BitbucketScopesRequestCache(ScopeResolver scopeResolver) {
        this.scopeResolver = scopeResolver;
    }

    @Override
    protected List<Permission> getPermissions(Scope scope) {
        if (scope == null) {
            return Collections.emptyList();
        }
        try {
            return this.scopeResolver.getScope(scope.getName()).getScopeAndInheritedScopes().stream().map(bitbucketScope -> {
                switch ((BitbucketScope)((Object)bitbucketScope)) {
                    case ACCOUNT_WRITE: {
                        return this.accountWritePermission();
                    }
                    case REPO_READ: {
                        return this.repoReadPermission();
                    }
                    case REPO_WRITE: {
                        return this.repoWritePermission();
                    }
                    case REPO_ADMIN: {
                        return this.repoAdminPermission();
                    }
                    case PROJECT_ADMIN: {
                        return this.projectAdminPermission();
                    }
                    case ADMIN_WRITE: {
                        return this.adminPermission();
                    }
                    case SYSTEM_ADMIN: {
                        return this.sysAdminPermission();
                    }
                }
                return this.defaultPermission();
            }).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        }
        catch (InvalidScopeException e) {
            log.info("Failed to determine scope in request", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private List<Permission> sysAdminPermission() {
        return this.getPermissionsForBitbucketPermission(this.adminPermission(), com.atlassian.bitbucket.permission.Permission.SYS_ADMIN);
    }

    private List<Permission> adminPermission() {
        return this.getPermissionsForBitbucketPermission(this.projectAdminPermission(), com.atlassian.bitbucket.permission.Permission.ADMIN);
    }

    private List<Permission> projectAdminPermission() {
        return this.getPermissionsForBitbucketPermission(this.repoAdminPermission(), com.atlassian.bitbucket.permission.Permission.PROJECT_ADMIN);
    }

    private List<Permission> repoAdminPermission() {
        return this.getPermissionsForBitbucketPermission(this.repoWritePermission(), com.atlassian.bitbucket.permission.Permission.REPO_ADMIN);
    }

    private List<Permission> repoWritePermission() {
        return this.getPermissionsForBitbucketPermission(this.repoReadPermission(), com.atlassian.bitbucket.permission.Permission.REPO_WRITE, com.atlassian.bitbucket.permission.Permission.PROJECT_WRITE);
    }

    private List<Permission> repoReadPermission() {
        return this.getPermissionsForBitbucketPermission(com.atlassian.bitbucket.permission.Permission.REPO_READ, com.atlassian.bitbucket.permission.Permission.PROJECT_READ);
    }

    private List<Permission> accountWritePermission() {
        return this.getPermissionsForBitbucketPermission(com.atlassian.bitbucket.permission.Permission.USER_ADMIN);
    }

    private List<Permission> defaultPermission() {
        return ImmutableList.of((Object)Permission.permission((String)com.atlassian.bitbucket.permission.Permission.LICENSED_USER.name()));
    }

    private List<Permission> getPermissionsForBitbucketPermission(List<Permission> inheritedPermissions, com.atlassian.bitbucket.permission.Permission ... bitbucketPermissions) {
        return ImmutableSet.builder().addAll(this.getPermissionsForBitbucketPermission(bitbucketPermissions)).addAll(inheritedPermissions).build().asList();
    }

    private List<Permission> getPermissionsForBitbucketPermission(com.atlassian.bitbucket.permission.Permission ... bitbucketPermissions) {
        return Stream.concat(Stream.of(bitbucketPermissions), Stream.of(com.atlassian.bitbucket.permission.Permission.LICENSED_USER)).flatMap(permission -> new ImmutableSet.Builder().addAll((Iterable)permission.getInheritedPermissions()).add(permission).build().stream()).distinct().map(permission -> Permission.permission((String)permission.name())).collect(Collectors.toList());
    }
}

