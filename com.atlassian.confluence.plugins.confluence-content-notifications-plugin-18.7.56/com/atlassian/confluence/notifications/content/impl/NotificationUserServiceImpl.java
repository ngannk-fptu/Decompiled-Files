/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.SystemUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.content.impl;

import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.SystemUser;
import com.atlassian.confluence.notifications.content.NotificationUserService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Maybe;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;

@Deprecated
public class NotificationUserServiceImpl
implements NotificationUserService {
    private static final String ANON_USER_KEY = "confluence.mail.templates.anonymous.name";
    private final UserAccessor userAccessor;
    private final I18NBeanFactory beanFactory;
    private final LocaleManager localeManager;

    public NotificationUserServiceImpl(UserAccessor userAccessor, I18NBeanFactory beanFactory, LocaleManager localeManager) {
        this.userAccessor = userAccessor;
        this.beanFactory = beanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public User findUserForKey(UserKey userKey) {
        return this.userAccessor.getUserByKey(userKey);
    }

    @Override
    public User findUserForPerson(User localeUser, Person person) {
        if (person instanceof KnownUser) {
            return this.userAccessor.getUserByName(((KnownUser)person).getUsername());
        }
        return this.getAnonymousUser(localeUser);
    }

    @Override
    public User findUserForKey(User localeUser, Maybe<UserKey> userKey) {
        if (userKey.isDefined()) {
            return this.findUserForKey((UserKey)userKey.get());
        }
        return this.getAnonymousUser(localeUser);
    }

    @Override
    public User findUserForName(User localeUser, Maybe<String> username) {
        if (username.isDefined()) {
            return this.userAccessor.getUserByName((String)username.get());
        }
        return this.getAnonymousUser(localeUser);
    }

    @Override
    public User getAnonymousUser(User user) {
        String anonName = this.beanFactory.getI18NBean(this.localeManager.getLocale(user)).getText(ANON_USER_KEY);
        return new SystemUser(anonName, "", "");
    }
}

