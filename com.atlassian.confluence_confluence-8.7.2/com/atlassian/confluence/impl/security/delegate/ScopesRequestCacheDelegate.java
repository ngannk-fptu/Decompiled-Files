/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache$RequestCache
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.PostConstruct
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.confluence.impl.security.delegate;

import com.atlassian.confluence.api.impl.sal.ConfluenceThreadLocalContextManager;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.osgi.util.tracker.ServiceTracker;

public class ScopesRequestCacheDelegate {
    private final OsgiContainerManager osgiContainerManager;
    private final ConfluenceThreadLocalContextManager contextManager;
    private final ResettableLazyReference<ServiceTracker<ScopesRequestCache, ScopesRequestCache>> scopesRequestCacheServiceTracker;

    public ScopesRequestCacheDelegate(final OsgiContainerManager osgiContainerManager, ConfluenceThreadLocalContextManager contextManager) {
        this.osgiContainerManager = osgiContainerManager;
        this.contextManager = contextManager;
        this.scopesRequestCacheServiceTracker = new ResettableLazyReference<ServiceTracker<ScopesRequestCache, ScopesRequestCache>>(){

            protected ServiceTracker<ScopesRequestCache, ScopesRequestCache> create() {
                return osgiContainerManager.getServiceTracker(ScopesRequestCache.class.getName());
            }
        };
    }

    @PostConstruct
    public void injectIntoContextManager() {
        this.contextManager.setScopesRequestCacheDelegate(this);
    }

    public boolean hasPermission(com.atlassian.confluence.security.Permission permission, Object target) {
        return permission == null || this.hasPermission(permission.toString(), target);
    }

    public boolean hasPermission(String permission, Object target) {
        return permission == null || this.getScopeRequestCache().map(scopesRequestCache -> scopesRequestCache.hasPermission(Permission.permissionWithTarget((String)permission, (Object)target))).orElse(true) != false;
    }

    public Optional<String> getApplicationNameForRequest() {
        return this.getScopeRequestCache().flatMap(ScopesRequestCache::getApplicationNameForRequest);
    }

    public Optional<ScopesRequestCache.RequestCache> getRequestCache() {
        return this.getScopeRequestCache().map(ScopesRequestCache::getRequestCache);
    }

    public void setRequestCache(ScopesRequestCache.RequestCache requestCache) {
        this.getScopeRequestCache().ifPresent(scopesRequestCache -> scopesRequestCache.setRequestCache(requestCache));
    }

    public void clearRequestCache() {
        this.getScopeRequestCache().ifPresent(ScopesRequestCache::clearRequestCache);
    }

    private Optional<ScopesRequestCache> getScopeRequestCache() {
        if (this.osgiContainerManager.isRunning()) {
            return Optional.ofNullable((ScopesRequestCache)((ServiceTracker)this.scopesRequestCacheServiceTracker.get()).getService());
        }
        return Optional.empty();
    }
}

