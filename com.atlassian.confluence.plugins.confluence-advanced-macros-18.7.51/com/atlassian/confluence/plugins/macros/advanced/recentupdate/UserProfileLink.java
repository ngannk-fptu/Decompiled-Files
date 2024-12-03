/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UserLink;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.spring.container.ContainerManager;

public class UserProfileLink
extends UserLink {
    final String staticResourceUrlPrefix;
    final ProfilePictureInfo profilePicture;

    public UserProfileLink(String username, I18NBean i18n) {
        super(username, i18n);
        this.profilePicture = this.userAccessor.getUserProfilePicture(this.user);
        this.staticResourceUrlPrefix = ((WebResourceUrlProvider)ContainerManager.getComponent((String)"webResourceUrlProvider")).getStaticResourcePrefix(UrlMode.AUTO);
    }

    @Override
    protected String getLinkBody() {
        if (this.user == null) {
            return String.format("<img class=\"userLogo logo anonymous\" src=\"%s/images/icons/profilepics/anonymous.png\" alt=\"\" title=\"%s\">", this.staticResourceUrlPrefix, this.i18n.getText("user.icon.anonymous.title"));
        }
        return String.format("<img class=\"userLogo logo\" src=\"%s\" alt=\"\" title=\"%s\">", this.getImageSrc(), HtmlUtil.htmlEncode((String)this.username));
    }

    @Override
    protected String getHref() {
        if (this.profilePicture.isDefault()) {
            if (this.user == AuthenticatedUserThreadLocal.get()) {
                return String.format("%s/users/editmyprofilepicture.action", RequestCacheThreadLocal.getContextPath());
            }
            return "";
        }
        return super.getHref();
    }

    private String getImageSrc() {
        if (this.profilePicture.isDefault()) {
            if (this.user == AuthenticatedUserThreadLocal.get()) {
                return String.format("%s/images/icons/profilepics/add_profile_pic.png", this.staticResourceUrlPrefix);
            }
            return this.staticResourceUrlPrefix + this.profilePicture.getDownloadPath();
        }
        return RequestCacheThreadLocal.getContextPath() + this.profilePicture.getDownloadPath();
    }
}

