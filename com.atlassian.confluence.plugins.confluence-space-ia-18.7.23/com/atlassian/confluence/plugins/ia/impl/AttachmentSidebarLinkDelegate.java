/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.impl.DefaultSidebarLinkDelegate;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;

class AttachmentSidebarLinkDelegate
extends DefaultSidebarLinkDelegate {
    private AttachmentManager attachmentManager;

    protected AttachmentSidebarLinkDelegate(SidebarLinkManager sidebarLinkManager, AttachmentManager attachmentManager, ContextPathHolder contextPathHolder) {
        super(sidebarLinkManager, contextPathHolder);
        this.attachmentManager = attachmentManager;
    }

    @Override
    public SidebarLink createSidebarLink(String spaceKey, Long attachmentId, SidebarLink.Type type, String customTitle, String url, String iconClass) {
        SidebarLink sidebarLink = null;
        Attachment attachment = this.attachmentManager.getAttachment(attachmentId.longValue());
        if (attachment != null) {
            sidebarLink = super.createSidebarLink(spaceKey, attachment.getId(), type, customTitle, null, iconClass);
        }
        return sidebarLink;
    }

    @Override
    public SidebarLinkBean getSidebarLinkBean(SidebarLink sidebarLink) {
        Attachment attachment = this.attachmentManager.getAttachment(sidebarLink.getDestPageId());
        return super.getSidebarLinkBean(this.getCustomizedSidebarLink(sidebarLink, (Addressable)attachment));
    }
}

