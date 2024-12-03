/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Either
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context.page.hipchat;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.page.AbstractPageEventRenderContextProvider;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Either;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class SharePageEventHipChatRenderContextProvider
extends AbstractPageEventRenderContextProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SharePageEventHipChatRenderContextProvider.class);

    public SharePageEventHipChatRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, ShareGroupEmailManager shareGroupEmailManager) {
        super(contentEntityManager, contentTypeResolver, userAccessor, i18NBeanFactory, localeManager, transactionTemplate, shareGroupEmailManager);
    }

    @Override
    protected Map<String, Object> buildContextForNotificationAddress(ShareContentPayload payload, Iterable<ConfluenceUser> users, Either<String, String> recipientData, Content content, ConfluenceUser sender) {
        LOG.warn("No RoleRecipient found in the recipient data.");
        return Collections.emptyMap();
    }
}

