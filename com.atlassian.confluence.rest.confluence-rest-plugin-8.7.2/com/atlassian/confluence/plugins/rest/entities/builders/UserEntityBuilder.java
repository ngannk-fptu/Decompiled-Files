/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.plugins.rest.entities.UserEntity;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.user.User;
import java.net.URI;

public class UserEntityBuilder {
    private final UserAccessor userAccessor;
    private final SettingsManager settingsManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final I18NBeanFactory i18NBeanFactory;

    public UserEntityBuilder(UserAccessor userAccessor, SettingsManager settingsManager, WebResourceUrlProvider webResourceUrlProvider, I18NBeanFactory i18NBeanFactory) {
        this.userAccessor = userAccessor;
        this.settingsManager = settingsManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public UserEntity build(User user) {
        UserEntity entity = new UserEntity();
        entity.setUsername(user.getName());
        entity.setFullName(user.getFullName());
        entity.setAvatarUrl(this.getAvatarUrl(user));
        entity.setDisplayableEmail(this.getDisplayableEmail(user));
        entity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("user/non-system").build(new Object[]{user.getName()})));
        return entity;
    }

    public UserEntity buildAnonymous() {
        UserEntity entity = new UserEntity();
        entity.setFullName(this.i18NBeanFactory.getI18NBean().getText("anonymous.name"));
        entity.setAvatarUrl(this.getAnonymousAvatarUrl());
        entity.setAnonymous(true);
        entity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("user/system/anonymous").build(new Object[0])));
        return entity;
    }

    private String getDisplayableEmail(User user) {
        String email = user.getEmail();
        String emailVisibility = this.settingsManager.getGlobalSettings().getEmailAddressVisibility();
        if (email == null || "email.address.private".equals(emailVisibility)) {
            return "";
        }
        if ("email.address.masked".equals(emailVisibility)) {
            return GeneralUtil.alwaysMaskEmail((String)email);
        }
        return email;
    }

    private String getAvatarUrl(User user) {
        ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture(user);
        if (userProfilePicture.isUploaded()) {
            return userProfilePicture.getUriReference();
        }
        return userProfilePicture.getUriReference();
    }

    private String getAnonymousAvatarUrl() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/anonymous.svg";
    }
}

