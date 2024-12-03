/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context.page.email;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.page.email.AbstractPageEventEmailRenderContextProvider;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class ShareDraftEventEmailRenderContextProvider
extends AbstractPageEventEmailRenderContextProvider {
    public ShareDraftEventEmailRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, ShareGroupEmailManager shareGroupEmailManager) {
        super(contentEntityManager, contentTypeResolver, userAccessor, i18NBeanFactory, localeManager, transactionTemplate, shareGroupEmailManager);
    }

    @Override
    protected Map<String, Object> buildSubjectContext(ConfluenceUser recipient, Content content, ConfluenceUser sender) {
        Locale recipientLocale = this.localeManager.getLocale((User)recipient);
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(recipientLocale);
        String title = content.getTitle();
        if (StringUtils.isEmpty((CharSequence)title)) {
            title = i18NBean.getText("share.invite.to.edit.mail.untitled." + content.getType());
        }
        String subject = i18NBean.getText("share.invite.to.edit.mail.subject.title", (Object[])new String[]{sender.getFullName(), title});
        return Collections.singletonMap("subject", subject);
    }
}

