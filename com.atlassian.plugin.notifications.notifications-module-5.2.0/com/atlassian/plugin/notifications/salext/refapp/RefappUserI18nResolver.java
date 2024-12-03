/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.salext.refapp;

import com.atlassian.plugin.notifications.spi.salext.AbstractUserI18nResolverImpl;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Qualifier;

public class RefappUserI18nResolver
extends AbstractUserI18nResolverImpl {
    public RefappUserI18nResolver(@Qualifier(value="i18nResolver") I18nResolver i18nResolver, LocaleResolver localeResolver) {
        super(i18nResolver, localeResolver);
    }

    @Override
    protected Locale getLocaleForUser(UserKey userKey) {
        return Locale.getDefault();
    }
}

