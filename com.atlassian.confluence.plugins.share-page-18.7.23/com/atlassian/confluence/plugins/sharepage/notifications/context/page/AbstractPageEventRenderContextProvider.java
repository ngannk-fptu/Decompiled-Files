/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$ContentBuilder
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context.page;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.AbstractContentEventRenderContextProvider;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractPageEventRenderContextProvider
extends AbstractContentEventRenderContextProvider {
    public AbstractPageEventRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, ShareGroupEmailManager shareGroupEmailManager) {
        super(contentEntityManager, contentTypeResolver, userAccessor, i18NBeanFactory, localeManager, transactionTemplate, shareGroupEmailManager);
    }

    @Override
    protected Content getContentForEntityId(Long entityId, Long contextualPageId) {
        return (Content)this.transactionTemplate.execute(() -> {
            ContentEntityObject contentToShare = this.contentEntityManager.getById(entityId.longValue());
            String contentType = this.contentTypeResolver.getContentType(contentToShare);
            Content.ContentBuilder contentBuilder = Content.builder().id(contentToShare.getContentId()).title(contentToShare.getDisplayTitle()).type(ContentType.valueOf((String)contentType)).status(contentToShare.getContentStatusObject()).addLink(LinkType.WEB_UI, contentToShare.getUrlPath());
            if (contentToShare instanceof AbstractPage) {
                contentBuilder.addLink(LinkType.EDIT_UI, ((AbstractPage)contentToShare).getEditUrlPath());
            }
            return contentBuilder.build();
        });
    }
}

