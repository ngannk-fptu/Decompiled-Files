/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Closeable
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache$RequestCache
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.scopes.request;

import com.atlassian.oauth2.scopes.api.Closeable;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultScopesRequestCache
implements ScopesRequestCache {
    private static final Logger logger = LoggerFactory.getLogger(DefaultScopesRequestCache.class);
    private final ThreadLocal<ScopesRequestCache.RequestCache> requestCacheThreadLocal = new InheritableThreadLocal<ScopesRequestCache.RequestCache>(){

        @Override
        protected ScopesRequestCache.RequestCache initialValue() {
            return new ScopesRequestCache.RequestCache();
        }
    };

    public Closeable withScopes(Scope scope, Supplier<Optional<String>> applicationNameSupplier) {
        this.setRequestCache(new ScopesRequestCache.RequestCache(this.getPermissions(scope), applicationNameSupplier));
        return this::clearRequestCache;
    }

    protected abstract List<Permission> getPermissions(Scope var1);

    public boolean containsOnlyThisScope(Scope scope) {
        return !this.requestCacheThreadLocal.get().getPermissions().isEmpty() && Sets.difference((Set)this.requestCacheThreadLocal.get().getPermissions(), new HashSet<Permission>(this.getPermissions(scope))).isEmpty();
    }

    public boolean hasPermission(Permission permission) {
        return this.requestCacheThreadLocal.get().getPermissions().isEmpty() || this.requestCacheThreadLocal.get().getPermissions().contains(permission);
    }

    @Nonnull
    public Optional<String> getApplicationNameForRequest() {
        return (Optional)this.requestCacheThreadLocal.get().getApplicationNameSupplier().get();
    }

    @Nonnull
    public Set<Permission> getPermissionsForRequest() {
        return ImmutableSet.copyOf((Collection)this.requestCacheThreadLocal.get().getPermissions());
    }

    public ScopesRequestCache.RequestCache getRequestCache() {
        return this.requestCacheThreadLocal.get().copy();
    }

    public void setRequestCache(@Nullable ScopesRequestCache.RequestCache requestCache) {
        if (requestCache != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Storing permissions [{}] to scope request cache for application name [{}].", (Object)requestCache.getPermissions(), (Object)requestCache.getApplicationNameSupplier());
            }
            this.requestCacheThreadLocal.set(requestCache.copy());
        } else {
            logger.debug("Request Cache set to null value");
            this.clearRequestCache();
        }
    }

    public void clearRequestCache() {
        this.requestCacheThreadLocal.remove();
        logger.debug("Removing all permissions from scope request cache.");
    }
}

