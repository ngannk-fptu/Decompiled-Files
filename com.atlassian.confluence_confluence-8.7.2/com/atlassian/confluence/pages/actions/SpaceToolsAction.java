/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Iterator;
import java.util.List;
import org.apache.struts2.ServletActionContext;

public class SpaceToolsAction
extends AbstractSpaceAction {
    public static final String WEB_ITEM_LOCATION = "system.space.tools";
    private String redirectUrl;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, this.getSpace())) {
            return "permissions";
        }
        WebInterfaceContext context = this.getWebInterfaceContext();
        List sections = this.webInterfaceManager.getDisplayableSections(WEB_ITEM_LOCATION, context.toMap());
        for (WebSectionModuleDescriptor section : sections) {
            List items = this.webInterfaceManager.getDisplayableItems(section.getLocation() + "/" + section.getKey(), context.toMap());
            Iterator iterator = items.iterator();
            if (!iterator.hasNext()) continue;
            WebItemModuleDescriptor item = (WebItemModuleDescriptor)iterator.next();
            this.redirectUrl = item.getLink().getRenderedUrl(context.toMap());
            return "success";
        }
        ServletActionContext.getResponse().sendError(404);
        return null;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}

