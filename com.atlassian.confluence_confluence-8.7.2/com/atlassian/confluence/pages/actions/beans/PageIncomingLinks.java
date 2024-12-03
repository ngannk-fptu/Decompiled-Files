/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.pages.actions.beans;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;

public class PageIncomingLinks {
    private final LinkManager linkManager;
    private final PermissionManager permissionManager;

    public PageIncomingLinks(LinkManager linkManager, PermissionManager permissionManager) {
        this.linkManager = linkManager;
        this.permissionManager = permissionManager;
    }

    public List<OutgoingLink> getIncomingLinks(AbstractPage page, User user) {
        List<OutgoingLink> allLinks = this.linkManager.getIncomingLinksToContent(page);
        allLinks = this.permissionManager.getPermittedEntities(user, Permission.VIEW, allLinks);
        List<OutgoingLink> incomingLinks = this.getUniqueUndeletedIncomingLinks(page, allLinks);
        return incomingLinks;
    }

    private List<OutgoingLink> getUniqueUndeletedIncomingLinks(AbstractPage page, List<OutgoingLink> allLinks) {
        HashSet<OutgoingLink> incomingLinks = new HashSet<OutgoingLink>();
        for (OutgoingLink link : allLinks) {
            SpaceContentEntityObject spaceContentEntityObject;
            ContentEntityObject sourceContent = link.getSourceContent();
            if (!(sourceContent instanceof SpaceContentEntityObject) || (spaceContentEntityObject = (SpaceContentEntityObject)sourceContent).isDraft() || spaceContentEntityObject.isDeleted() || link.isFrom(page)) continue;
            incomingLinks.add(link);
        }
        return Lists.newArrayList(incomingLinks);
    }
}

