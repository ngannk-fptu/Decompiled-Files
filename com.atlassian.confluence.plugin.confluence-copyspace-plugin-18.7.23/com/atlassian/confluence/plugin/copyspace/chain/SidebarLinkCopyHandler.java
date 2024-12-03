/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.SidebarLinkCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="sidebarLinkCopyHandler")
public class SidebarLinkCopyHandler
implements CopyHandler {
    private final SidebarLinkCopier sidebarLinkCopier;

    @Autowired
    public SidebarLinkCopyHandler(SidebarLinkCopier sidebarLinkCopier) {
        this.sidebarLinkCopier = sidebarLinkCopier;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        String originalSpaceKey = event.getOrigin().getSpaceKey();
        String targetSpaceKey = event.getDestination().getSpaceKey();
        this.sidebarLinkCopier.checkAndCopyRewritableSidebarLink(event.getOrigin().getId(), (ContentEntityObject)event.getDestination(), originalSpaceKey, targetSpaceKey);
        if (context.isCopyAttachments()) {
            this.sidebarLinkCopier.checkAndCopyRewritableAttachmentSidebarLink(event.getOrigin().getAttachments(), (ContentEntityObject)event.getDestination(), originalSpaceKey, targetSpaceKey);
        }
    }
}

