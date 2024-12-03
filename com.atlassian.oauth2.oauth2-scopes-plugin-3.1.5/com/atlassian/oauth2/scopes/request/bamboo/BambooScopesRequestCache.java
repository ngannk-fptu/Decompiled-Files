/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.security.acegi.acls.BambooPermission
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request.bamboo;

import com.atlassian.bamboo.security.acegi.acls.BambooPermission;
import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.DefaultScopesRequestCache;
import com.atlassian.oauth2.scopes.request.bamboo.BambooScope;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BambooScopesRequestCache
extends DefaultScopesRequestCache {
    private static final Logger log = LoggerFactory.getLogger(BambooScopesRequestCache.class);
    private final ScopeResolver scopeResolver;

    public BambooScopesRequestCache(ScopeResolver scopeResolver) {
        this.scopeResolver = scopeResolver;
    }

    @Override
    protected List<Permission> getPermissions(Scope scope) {
        if (scope == null) {
            return Collections.emptyList();
        }
        try {
            return this.scopeResolver.getScope(scope.getName()).getScopeAndInheritedScopes().stream().map(bambooScope -> {
                switch ((BambooScope)((Object)bambooScope)) {
                    case READ: {
                        return this.readPermissions();
                    }
                    case TRIGGER: {
                        return this.triggerPermissions();
                    }
                    case USER: {
                        return this.allPermissions();
                    }
                }
                throw new IllegalArgumentException("Could not determine scope [" + bambooScope + "]");
            }).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        }
        catch (InvalidScopeException e) {
            log.info("Failed to determine scope in request", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private List<Permission> allPermissions() {
        return ImmutableList.builder().addAll((Iterable)BambooPermission.ALL_PERMISSIONS.stream().map(bambooPermission -> Permission.permission((String)String.valueOf(bambooPermission.getMask()))).collect(Collectors.toList())).build();
    }

    private List<Permission> triggerPermissions() {
        return ImmutableList.of((Object)Permission.permission((String)String.valueOf(BambooPermission.READ.getMask())), (Object)Permission.permission((String)String.valueOf(BambooPermission.BUILD.getMask())), (Object)Permission.permission((String)String.valueOf(BambooPermission.SOX_COMPLIANCE.getMask())));
    }

    private List<Permission> readPermissions() {
        return ImmutableList.of((Object)Permission.permission((String)String.valueOf(BambooPermission.READ.getMask())));
    }
}

