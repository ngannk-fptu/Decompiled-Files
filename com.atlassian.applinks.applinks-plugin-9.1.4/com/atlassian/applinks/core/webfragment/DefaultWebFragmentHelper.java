/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.plugin.web.model.WebPanel
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.core.webfragment;

import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.applinks.core.rest.model.WebItemEntity;
import com.atlassian.applinks.core.rest.model.WebItemEntityList;
import com.atlassian.applinks.core.rest.model.WebPanelEntity;
import com.atlassian.applinks.core.rest.model.WebPanelEntityList;
import com.atlassian.applinks.core.webfragment.WebFragmentContext;
import com.atlassian.applinks.core.webfragment.WebFragmentHelper;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.plugin.web.model.WebPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultWebFragmentHelper
implements WebFragmentHelper {
    private final WebInterfaceManager webInterfaceManager;

    @Autowired
    public DefaultWebFragmentHelper(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    @Override
    @Nonnull
    public WebItemEntityList getWebItemsForLocation(String location, WebFragmentContext context) {
        ArrayList<WebItemEntity> webItems = new ArrayList<WebItemEntity>();
        Map<String, Object> contextMap = context.getContextMap();
        HttpServletRequest request = CurrentContext.getHttpServletRequest();
        for (WebItemModuleDescriptor descriptor : this.webInterfaceManager.getDisplayableItems(location, contextMap)) {
            WebLink link = descriptor.getLink();
            WebIcon icon = descriptor.getIcon();
            WebItemEntity.Builder itemBuilder = new WebItemEntity.Builder();
            itemBuilder.id(link.getId());
            itemBuilder.url(link.getDisplayableUrl(request, new HashMap<String, Object>(contextMap)));
            if (link.hasAccessKey()) {
                itemBuilder.accessKey(link.getAccessKey(new HashMap<String, Object>(contextMap)));
            }
            if (icon != null) {
                itemBuilder.iconUrl(icon.getUrl().getDisplayableUrl(request, new HashMap<String, Object>(contextMap)));
                itemBuilder.iconHeight(icon.getHeight());
                itemBuilder.iconWidth(icon.getWidth());
            }
            if (descriptor.getWebLabel() != null) {
                itemBuilder.label(descriptor.getWebLabel().getDisplayableLabel(request, new HashMap<String, Object>(contextMap)));
            }
            if (descriptor.getTooltip() != null) {
                itemBuilder.tooltip(descriptor.getTooltip().getDisplayableLabel(request, new HashMap<String, Object>(contextMap)));
            }
            itemBuilder.styleClass(descriptor.getStyleClass());
            webItems.add(itemBuilder.build());
        }
        return new WebItemEntityList(webItems);
    }

    @Override
    public WebPanelEntityList getWebPanelsForLocation(String location, WebFragmentContext context) {
        ArrayList<WebPanelEntity> webPanels = new ArrayList<WebPanelEntity>();
        Map<String, Object> contextMap = context.getContextMap();
        for (WebPanel webPanel : this.webInterfaceManager.getDisplayableWebPanels(location, contextMap)) {
            webPanels.add(new WebPanelEntity(webPanel.getHtml(contextMap)));
        }
        return new WebPanelEntityList(webPanels);
    }
}

