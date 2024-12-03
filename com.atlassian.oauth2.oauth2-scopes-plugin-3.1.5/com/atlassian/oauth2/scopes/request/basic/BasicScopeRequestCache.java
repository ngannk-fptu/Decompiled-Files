/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.InvalidScopeException
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request.basic;

import com.atlassian.oauth2.scopes.api.InvalidScopeException;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.request.DefaultScopesRequestCache;
import com.atlassian.oauth2.scopes.request.basic.BasicScope;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicScopeRequestCache
extends DefaultScopesRequestCache {
    private static final Logger log = LoggerFactory.getLogger(BasicScopeRequestCache.class);
    private final ScopeResolver scopeResolver;

    public BasicScopeRequestCache(ScopeResolver scopeResolver) {
        this.scopeResolver = scopeResolver;
    }

    @Override
    protected List<Permission> getPermissions(Scope scope) {
        if (scope == null) {
            return Collections.emptyList();
        }
        try {
            return this.scopeResolver.getScope(scope.getName()).getScopeAndInheritedScopes().stream().map(basicScope -> {
                switch ((BasicScope)((Object)basicScope)) {
                    case SYSTEM_ADMIN: {
                        return this.systemAdminPermission();
                    }
                    case ADMIN: {
                        return this.adminPermission();
                    }
                    case WRITE: {
                        return this.writePermission();
                    }
                    case READ: {
                        return this.readPermission();
                    }
                }
                return Collections.emptyList();
            }).flatMap(Collection::stream).distinct().collect(Collectors.toList());
        }
        catch (InvalidScopeException e) {
            log.info("Failed to determine scope in request", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    protected abstract List<Permission> systemAdminPermission();

    protected abstract List<Permission> adminPermission();

    protected abstract List<Permission> writePermission();

    protected abstract List<Permission> readPermission();
}

