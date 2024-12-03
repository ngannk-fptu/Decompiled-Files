/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.links.WebLink
 *  com.atlassian.confluence.core.Addressable
 *  com.atlassian.confluence.core.ContextPathHolder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinkDelegate;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.impl.DefaultSidebarLink;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import org.apache.commons.lang3.StringUtils;

public class DefaultSidebarLinkDelegate
implements SidebarLinkDelegate {
    private SidebarLinkManager sidebarLinkManager;
    private ContextPathHolder contextPathHolder;

    protected DefaultSidebarLinkDelegate(SidebarLinkManager sidebarLinkManager, ContextPathHolder contextPathHolder) {
        this.sidebarLinkManager = sidebarLinkManager;
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public SidebarLink createSidebarLink(String spaceKey, Long resourceId, SidebarLink.Type type, String customTitle, String url, String iconClass) {
        return this.sidebarLinkManager.createLink(spaceKey, SidebarLinkCategory.QUICK, type, null, 0, customTitle, url, iconClass, resourceId == null ? -2L : resourceId);
    }

    @Override
    public SidebarLinkBean getSidebarLinkBean(SidebarLink sidebarLink) {
        String title = sidebarLink.getCustomTitle();
        String url = sidebarLink.getHardcodedUrl();
        Object styleClass = sidebarLink.getType().getStyleClass();
        String customIconClass = sidebarLink.getCustomIconClass();
        title = title == null ? "" : title;
        Object object = styleClass = styleClass == null ? "" : styleClass;
        if (StringUtils.isNotBlank((CharSequence)customIconClass)) {
            styleClass = (String)styleClass + " " + customIconClass;
        }
        return new SidebarLinkBean(sidebarLink.getID(), sidebarLink.getWebItemKey(), title, WebLink.isValidURL((String)url) ? url : "#", sidebarLink.getPosition(), (String)styleClass, sidebarLink.getHidden(), false, null, null);
    }

    protected SidebarLink getCustomizedSidebarLink(SidebarLink sidebarLink, Addressable entity) {
        DefaultSidebarLink link = new DefaultSidebarLink(sidebarLink);
        if (entity != null) {
            String title = link.getCustomTitle();
            String url = link.getHardcodedUrl();
            String contextPath = this.contextPathHolder.getContextPath();
            if (StringUtils.isBlank((CharSequence)title)) {
                link.setCustomTitle(entity.getDisplayTitle());
            }
            if (StringUtils.isBlank((CharSequence)url)) {
                link.setHardcodedUrl(contextPath + entity.getUrlPath());
            }
        }
        return link;
    }
}

