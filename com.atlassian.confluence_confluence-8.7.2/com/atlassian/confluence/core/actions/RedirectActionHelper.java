/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class RedirectActionHelper {
    private WebInterfaceManager webInterfaceManager;

    public RedirectActionHelper(WebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }

    public String getRedirectUrlAndUpdateCookies(String cookieName, String webItemLocation, WebInterfaceContext context) throws Exception {
        List availableWebItems = this.webInterfaceManager.getDisplayableItems(webItemLocation, context.toMap());
        if (availableWebItems.isEmpty()) {
            ServletActionContext.getResponse().sendError(404);
            return null;
        }
        WebItemModuleDescriptor redirectTo = this.getItemToRedirectTo(availableWebItems, cookieName);
        GeneralUtil.setCookie(cookieName, redirectTo.getKey());
        return redirectTo.getLink().getRenderedUrl(context.toMap());
    }

    private WebItemModuleDescriptor getItemToRedirectTo(List availableWebItems, String cookieName) {
        String cookieValue = GeneralUtil.getCookieValue(cookieName);
        if (StringUtils.isNotEmpty((CharSequence)cookieValue)) {
            for (WebItemModuleDescriptor item : availableWebItems) {
                if (!item.getKey().equals(cookieValue)) continue;
                return item;
            }
        }
        return (WebItemModuleDescriptor)availableWebItems.get(0);
    }
}

