/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.SidebarLinks;
import com.atlassian.confluence.plugins.ia.impl.DefaultSidebarLink;
import java.util.Objects;
import java.util.stream.Collectors;

public class CachingSidebarLinkManager
implements SidebarLinkManager {
    private static final String CACHE_NAME = CachingSidebarLinkManager.class.getName() + ".SidebarLinkCache";
    private final SidebarLinkManager delegate;
    private final Cache<String, SidebarLinks> cache;

    public CachingSidebarLinkManager(SidebarLinkManager delegate, CacheFactory cacheFactory) {
        this(delegate, CachingSidebarLinkManager.createCache(cacheFactory));
    }

    CachingSidebarLinkManager(SidebarLinkManager delegate, Cache<String, SidebarLinks> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    private static Cache<String, SidebarLinks> createCache(CacheFactory cacheFactory) {
        Cache cache = cacheFactory.getCache(CACHE_NAME);
        cache.removeAll();
        return cache;
    }

    private void invalidateCache(SidebarLink link) {
        this.cache.remove((Object)Objects.requireNonNull(link).getSpaceKey());
    }

    private void invalidateCache() {
        this.cache.removeAll();
    }

    private static SidebarLinks transformAoProxiesToDefaultSidebarLinks(SidebarLinks proxiedLinks) {
        return new SidebarLinks(proxiedLinks.getAllLinks().stream().map(DefaultSidebarLink::new).collect(Collectors.toList()));
    }

    @Override
    public SidebarLink createLink(String spaceKey, SidebarLinkCategory category, SidebarLink.Type type, String webItemKey, int position, String customTitle, String hardcodedUrl, String customIconClass, long destResourceId) {
        SidebarLink newLink = this.delegate.createLink(spaceKey, category, type, webItemKey, position, customTitle, hardcodedUrl, customIconClass, destResourceId);
        this.invalidateCache(newLink);
        return newLink;
    }

    @Override
    public void moveLink(SidebarLink link, int from, int to) {
        this.delegate.moveLink(link, from, to);
        this.invalidateCache(link);
    }

    @Override
    public void deleteLink(SidebarLink link) {
        this.delegate.deleteLink(link);
        this.invalidateCache(link);
    }

    @Override
    public void deleteLinks(long resourceId, SidebarLink.Type type) {
        this.delegate.deleteLinks(resourceId, type);
        this.invalidateCache();
    }

    @Override
    public void deleteLinksForSpace(String spaceKey) {
        this.delegate.deleteLinksForSpace(spaceKey);
        this.invalidateCache();
    }

    @Override
    public void hideLink(SidebarLink link) {
        this.delegate.hideLink(link);
        this.invalidateCache(link);
    }

    @Override
    public void showLink(SidebarLink link) {
        this.delegate.showLink(link);
        this.invalidateCache(link);
    }

    @Override
    public SidebarLink findById(int id) {
        return this.delegate.findById(id);
    }

    @Override
    public SidebarLinks findBySpace(String spaceKey) {
        return (SidebarLinks)this.cache.get((Object)spaceKey, () -> CachingSidebarLinkManager.transformAoProxiesToDefaultSidebarLinks(this.delegate.findBySpace(spaceKey)));
    }
}

