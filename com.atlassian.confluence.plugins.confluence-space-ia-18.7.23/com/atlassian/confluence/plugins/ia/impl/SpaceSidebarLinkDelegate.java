/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.impl.DefaultSidebarLinkDelegate;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;

class SpaceSidebarLinkDelegate
extends DefaultSidebarLinkDelegate {
    private SpaceManager spaceManager;

    protected SpaceSidebarLinkDelegate(SidebarLinkManager sidebarLinkManager, SpaceManager spaceManager, ContextPathHolder contextPathHolder) {
        super(sidebarLinkManager, contextPathHolder);
        this.spaceManager = spaceManager;
    }

    @Override
    public SidebarLink createSidebarLink(String spaceKey, Long spaceId, SidebarLink.Type type, String customTitle, String url, String iconClass) {
        SidebarLink sidebarLink = null;
        Space space = this.spaceManager.getSpace(spaceId.longValue());
        if (space != null) {
            sidebarLink = super.createSidebarLink(spaceKey, space.getId(), type, customTitle, null, iconClass);
        }
        return sidebarLink;
    }

    @Override
    public SidebarLinkBean getSidebarLinkBean(SidebarLink sidebarLink) {
        Space space = this.spaceManager.getSpace(sidebarLink.getDestPageId());
        return super.getSidebarLinkBean(this.getCustomizedSidebarLink(sidebarLink, (Addressable)space));
    }
}

