/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContextPathHolder
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.impl.DefaultSidebarLinkDelegate;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;

class ContentEntityObjectSidebarLinkDelegate
extends DefaultSidebarLinkDelegate {
    private ContentEntityManager contentEntityManager;

    protected ContentEntityObjectSidebarLinkDelegate(SidebarLinkManager sidebarLinkManager, ContentEntityManager contentEntityManager, ContextPathHolder contextPathHolder) {
        super(sidebarLinkManager, contextPathHolder);
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public SidebarLink createSidebarLink(String spaceKey, Long resourceId, SidebarLink.Type type, String customTitle, String url, String iconClass) {
        SidebarLink sidebarLink = null;
        ContentEntityObject page = this.contentEntityManager.getById(resourceId.longValue());
        if (page != null) {
            sidebarLink = super.createSidebarLink(spaceKey, page.getId(), type, customTitle, null, iconClass);
        }
        return sidebarLink;
    }

    @Override
    public SidebarLinkBean getSidebarLinkBean(SidebarLink sidebarLink) {
        ContentEntityObject contentEntityObject = this.contentEntityManager.getById(sidebarLink.getDestPageId());
        return super.getSidebarLinkBean(this.getCustomizedSidebarLink(sidebarLink, (Addressable)contentEntityObject));
    }
}

