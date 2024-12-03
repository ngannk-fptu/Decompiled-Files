/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.ia.SidebarLink
 *  com.atlassian.confluence.plugins.ia.SidebarLink$Type
 *  com.atlassian.confluence.plugins.ia.SidebarLinkCategory
 *  com.atlassian.confluence.plugins.ia.SidebarLinkManager
 *  com.atlassian.confluence.plugins.ia.SidebarLinks
 *  com.atlassian.confluence.plugins.ia.service.SidebarService
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Sets
 *  net.java.ao.Entity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugin.copyspace.service.RewritableSideBarLinkProvider;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.SidebarLinks;
import com.atlassian.confluence.plugins.ia.service.SidebarService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.java.ao.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="sidebarLinkCopierImpl")
public class SidebarLinkCopierImpl
implements SidebarLinkCopier {
    private static final Logger log = LoggerFactory.getLogger(SidebarLinkCopierImpl.class);
    private static final List<String> SIDEBAR_OPTIONS = Arrays.asList("page-tree-state", "quick-links-state", "nav-type");
    private final SidebarLinkManager sidebarLinkManager;
    private final RewritableSideBarLinkProvider sideBarLinkProvider;
    private final SidebarService sidebarService;

    @Autowired
    public SidebarLinkCopierImpl(@ComponentImport SidebarLinkManager sidebarLinkManager, RewritableSideBarLinkProvider sideBarLinkProvider, @ComponentImport SidebarService sidebarService) {
        this.sidebarLinkManager = sidebarLinkManager;
        this.sideBarLinkProvider = sideBarLinkProvider;
        this.sidebarService = sidebarService;
    }

    @Override
    public void copyNonRewritableLinks(String originalSpaceKey, String targetSpaceKey) {
        SidebarLinks originalLinks = this.sidebarLinkManager.findBySpace(originalSpaceKey);
        Map allPinnedPagesMap = Sets.newHashSet((Iterable)originalLinks.getLinks(SidebarLinkCategory.QUICK)).stream().collect(Collectors.toMap(Entity::getID, Function.identity()));
        Collection<SidebarLink> rewritableLinks = this.sideBarLinkProvider.fetchRewritableLinksWithinSpace(originalSpaceKey);
        for (SidebarLink rewritableLink : rewritableLinks) {
            allPinnedPagesMap.remove(rewritableLink.getID());
        }
        allPinnedPagesMap.values().forEach(link -> this.createLink((SidebarLink)link, targetSpaceKey, link.getDestPageId()));
        this.copyMainLinks(originalLinks, targetSpaceKey);
        this.copySidebarOptions(originalSpaceKey, targetSpaceKey);
    }

    private void copySidebarOptions(String originalSpaceKey, String targetSpaceKey) {
        SIDEBAR_OPTIONS.forEach(option -> {
            String optionValue = this.sidebarService.getOption(originalSpaceKey, option);
            if (optionValue != null) {
                try {
                    this.sidebarService.setOption(targetSpaceKey, option, optionValue);
                }
                catch (NotPermittedException e) {
                    log.error("Not permitted to copy option {} for space {}", new Object[]{option, targetSpaceKey, e});
                }
            }
        });
    }

    @Override
    public void checkAndCopyRewritableSidebarLink(long originalContentId, ContentEntityObject targetContent, String originalSpaceKey, String targetSpaceKey) {
        this.sideBarLinkProvider.getSidebarLink(originalSpaceKey, originalContentId).ifPresent(link -> this.createLink((SidebarLink)link, targetSpaceKey, targetContent.getId()));
    }

    @Override
    public void checkAndCopyRewritableAttachmentSidebarLink(List<Attachment> originalAttachments, ContentEntityObject targetContent, String originalSpaceKey, String targetSpaceKey) {
        for (Attachment originalAttachment : originalAttachments) {
            Attachment targetAttachment = targetContent.getAttachmentNamed(originalAttachment.getFileName());
            this.checkAndCopyRewritableSidebarLink(originalAttachment.getId(), (ContentEntityObject)targetAttachment, originalSpaceKey, targetSpaceKey);
        }
    }

    private void createLink(SidebarLink link, String newSpaceKey, Long destContentId) {
        this.sidebarLinkManager.createLink(newSpaceKey, link.getCategory(), link.getType(), link.getWebItemKey(), link.getPosition(), link.getCustomTitle(), link.getHardcodedUrl(), link.getCustomIconClass(), destContentId.longValue());
    }

    private void copyMainLinks(SidebarLinks originalLinks, String targetSpaceKey) {
        Collection mainLinks = originalLinks.getLinks(SidebarLinkCategory.MAIN);
        mainLinks.forEach(mainLink -> {
            SidebarLink newLink = this.sidebarLinkManager.createLink(targetSpaceKey, SidebarLinkCategory.MAIN, SidebarLink.Type.WEB_ITEM, mainLink.getWebItemKey(), mainLink.getPosition(), null, null, null, -2L);
            if (mainLink.getHidden()) {
                this.sidebarLinkManager.hideLink(newLink);
            }
        });
    }
}

