/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 */
package com.atlassian.confluence.plugin.descriptor.mail;

import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.List;

public class DefaultNotificationRenderManager
implements NotificationRenderManager {
    private WebInterfaceManager webInterfaceManager;
    private DataSourceFactory dataSourceFactory;

    @Override
    public List<WebItemModuleDescriptor> getDisplayableItems(String section, NotificationContext context) {
        return this.webInterfaceManager.getDisplayableItems(section, context.getMap());
    }

    @Override
    public void attachActionIconImages(String section, NotificationContext context) {
        List<WebItemModuleDescriptor> webItems = this.getDisplayableItems(section, context);
        for (WebItemModuleDescriptor webItem : webItems) {
            context.addTemplateImage(this.dataSourceFactory.getServletContainerResource((String)webItem.getParams().get("icon"), webItem.getWebLabel().getKey()));
        }
    }

    public void setWebInterfaceManager(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }
}

