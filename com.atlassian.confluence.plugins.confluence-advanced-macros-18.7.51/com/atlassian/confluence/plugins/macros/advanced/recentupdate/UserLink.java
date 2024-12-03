/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;

public class UserLink {
    final String username;
    final User user;
    final UserAccessor userAccessor;
    final I18NBean i18n;

    public UserLink(String username, I18NBean i18n) {
        this.username = username;
        this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        this.user = this.userAccessor.getUserByName(username);
        this.i18n = i18n;
    }

    public String toString() {
        return this.user != null ? String.format("<a class=\"%s\" data-username=\"%s\" href=\"%s\">%s</a>", this.getCssClass(), HtmlUtil.urlEncode((String)this.username), this.getHref(), this.getLinkBody()) : this.getLinkBody();
    }

    protected String getLinkBody() {
        if (this.user == null) {
            return this.i18n.getText("anonymous.name");
        }
        return HtmlUtil.htmlEncode((String)this.user.getFullName());
    }

    protected String getHref() {
        return RequestCacheThreadLocal.getContextPath() + UserProfileLink.getLinkPath((String)this.username);
    }

    private String getCssClass() {
        PermissionManager permissionManager = (PermissionManager)ContainerManager.getComponent((String)"permissionManager");
        if (permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)this.user)) {
            return "confluence-userlink url fn";
        }
        return "url fn";
    }
}

