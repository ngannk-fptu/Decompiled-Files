/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.inlinecomments.helper;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class InlineCommentUserHelper {
    public static final String ANONYMOUS_KEY = "anonymous.name";
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18nBeanFactory;
    private final LocaleManager localeManager;

    public InlineCommentUserHelper(UserAccessor userAccessor, I18NBeanFactory beanFactory, LocaleManager localeManager) {
        this.userAccessor = userAccessor;
        this.i18nBeanFactory = beanFactory;
        this.localeManager = localeManager;
    }

    public String getFullNameForUserKey(String userKey) {
        ConfluenceUser user;
        if (StringUtils.isNotEmpty((CharSequence)userKey) && (user = this.userAccessor.getUserByKey(new UserKey(userKey))) != null) {
            return user.getFullName();
        }
        return this.i18nBeanFactory.getI18NBean(this.getUserLocale()).getText(ANONYMOUS_KEY);
    }

    private Locale getUserLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}

