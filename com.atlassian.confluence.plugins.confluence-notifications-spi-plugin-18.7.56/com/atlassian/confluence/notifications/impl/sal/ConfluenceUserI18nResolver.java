/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.notifications.spi.salext.AbstractUserI18nResolverImpl
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.impl.sal;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.notifications.spi.salext.AbstractUserI18nResolverImpl;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConfluenceUserI18nResolver
extends AbstractUserI18nResolverImpl {
    private final LocaleManager localeManager;
    private final UserAccessor userAccessor;

    public ConfluenceUserI18nResolver(@Qualifier(value="i18nResolver") I18nResolver i18nResolver, LocaleResolver localeResolver, LocaleManager localeManager, UserAccessor userAccessor) {
        super(i18nResolver, localeResolver);
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
    }

    protected Locale getLocaleForUser(UserKey userKey) {
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        return this.localeManager.getLocale((User)user);
    }
}

