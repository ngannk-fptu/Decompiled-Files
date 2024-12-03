/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.confluence.plugins.requestaccess.notifications.NotificationContextProviderHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

abstract class AbstractAccessNotificationContextProvider
extends RenderContextProviderTemplate<DefaultAccessNotificationPayload> {
    final NotificationContextProviderHelper helper;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    AbstractAccessNotificationContextProvider(@ComponentImport UserAccessor userAccessor, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.helper = new NotificationContextProviderHelper(contentEntityManager);
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
        this.localeManager = Objects.requireNonNull(localeManager);
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<DefaultAccessNotificationPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> recipientData) {
        Objects.requireNonNull(notification);
        Objects.requireNonNull(serverConfiguration);
        Objects.requireNonNull(recipientData);
        if (!this.helper.canFindRecipient(recipientData)) {
            return MaybeNot.becauseOf((String)"No recipient found", (Object[])new Object[0]);
        }
        DefaultAccessNotificationPayload payload = (DefaultAccessNotificationPayload)notification.getPayload();
        Content content = this.helper.getContent(payload);
        if (content == null) {
            return MaybeNot.becauseOf((String)"No content found", (Object[])new Object[0]);
        }
        NotificationContext context = new NotificationContext();
        ConfluenceUser actingUser = this.userAccessor.getUserByKey(payload.getSourceUserKey());
        ConfluenceUser receivingUser = this.userAccessor.getUserByKey(payload.getTargetUserKey());
        context.put("actingUser", (Object)actingUser);
        context.put("accessType", (Object)payload.getAccessType());
        context.put("content", (Object)content);
        context.put("contentTitle", (Object)(StringUtils.isEmpty((CharSequence)content.getTitle()) ? this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)receivingUser)).getText("confluence.notification.request.access.untitled." + content.getType()) : content.getTitle()));
        context.put("actionUrl", (Object)this.getRelativeActionUrl(payload, content, actingUser));
        return Option.option((Object)this.extendedContext(context, payload).getMap());
    }

    protected NotificationContext extendedContext(NotificationContext context, DefaultAccessNotificationPayload payload) {
        return context;
    }

    protected abstract String getRelativeActionUrl(DefaultAccessNotificationPayload var1, Content var2, ConfluenceUser var3);
}

