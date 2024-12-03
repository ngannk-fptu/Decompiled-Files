/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context.page.email;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.page.email.AbstractPageEventEmailRenderContextProvider;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

public class SharePageEventEmailRenderContextProvider
extends AbstractPageEventEmailRenderContextProvider {
    public SharePageEventEmailRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, ShareGroupEmailManager shareGroupEmailManager) {
        super(contentEntityManager, contentTypeResolver, userAccessor, i18NBeanFactory, localeManager, transactionTemplate, shareGroupEmailManager);
    }
}

