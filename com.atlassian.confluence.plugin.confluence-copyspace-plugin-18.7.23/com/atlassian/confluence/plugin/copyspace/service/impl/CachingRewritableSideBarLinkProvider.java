/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.plugins.ia.SidebarLink
 *  com.atlassian.confluence.plugins.ia.SidebarLink$Type
 *  com.atlassian.confluence.plugins.ia.SidebarLinkManager
 *  com.atlassian.confluence.plugins.ia.SidebarLinks
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.plugin.copyspace.service.RewritableSideBarLinkProvider;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.SidebarLinks;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cachingRewritableSideBarLinkProvider")
public class CachingRewritableSideBarLinkProvider
implements RewritableSideBarLinkProvider {
    private static final Set<SidebarLink.Type> REWRITABLE_LINK_TYPES = ImmutableSet.of((Object)SidebarLink.Type.PINNED_PAGE, (Object)SidebarLink.Type.PINNED_BLOG_POST, (Object)SidebarLink.Type.PINNED_ATTACHMENT);
    private final SidebarLinkManager sidebarLinkManager;
    private final ContentService contentService;
    private final Cache<String, Map<Long, SidebarLink>> sidebarLinksCache;

    @Autowired
    public CachingRewritableSideBarLinkProvider(@ComponentImport SidebarLinkManager sidebarLinkManager, @ComponentImport CacheManager cacheManager, @ComponentImport(value="apiContentService") ContentService contentService) {
        this.sidebarLinkManager = sidebarLinkManager;
        this.contentService = contentService;
        this.sidebarLinksCache = cacheManager.getCache(CachingRewritableSideBarLinkProvider.class.getName() + ".sidebarLinksCache", this::loadLinksWithinSpace, new CacheSettingsBuilder().local().expireAfterAccess(1L, TimeUnit.MINUTES).build());
    }

    @Override
    public Collection<SidebarLink> fetchRewritableLinksWithinSpace(String spaceKey) {
        return ((Map)this.sidebarLinksCache.get((Object)spaceKey)).values();
    }

    @Override
    public Optional<SidebarLink> getSidebarLink(String spaceKey, Long contentId) {
        return Optional.ofNullable((SidebarLink)((Map)this.sidebarLinksCache.get((Object)spaceKey)).get(contentId));
    }

    @Nonnull
    private Map<Long, SidebarLink> loadLinksWithinSpace(String spaceKey) {
        SidebarLinks sidebarLinks = this.sidebarLinkManager.findBySpace(spaceKey);
        return sidebarLinks.getAllLinks().stream().filter(link -> REWRITABLE_LINK_TYPES.contains(link.getType())).filter(link -> this.contentBelongsToSpace(link.getDestPageId(), spaceKey)).collect(Collectors.toMap(SidebarLink::getDestPageId, Function.identity()));
    }

    private boolean contentBelongsToSpace(long contentId, String spaceKey) {
        Content content = (Content)this.contentService.find(new Expansion[]{new Expansion("space")}).withId(ContentId.of((long)contentId)).fetchOrNull();
        if (content != null) {
            return spaceKey.equals(content.getSpace().getKey());
        }
        return false;
    }
}

