/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.plugins.rest.entities.UserEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.UserEntityBuilder;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;

public class UserEntityHelper {
    private UserEntityBuilder userEntityBuilder;

    public UserEntityHelper(UserAccessor userAccessor, SettingsManager settingsManager, WebResourceUrlProvider webResourceUrlProvider, I18NBeanFactory i18NBeanFactory) {
        this.userEntityBuilder = new UserEntityBuilder(userAccessor, settingsManager, webResourceUrlProvider, i18NBeanFactory);
    }

    public UserEntity buildEntityForUser(ConfluenceUser user) {
        return user != null ? this.userEntityBuilder.build((User)user) : this.userEntityBuilder.buildAnonymous();
    }
}

