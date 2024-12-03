/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.content.CommentPayload
 *  com.atlassian.confluence.notifications.content.CommonContentExpansions
 *  com.atlassian.confluence.notifications.content.ContentException
 *  com.atlassian.confluence.notifications.content.ContentRenderContextFactory
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.inlinecomments.notifications;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommentPayload;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentException;
import com.atlassian.confluence.notifications.content.ContentRenderContextFactory;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.user.User;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public abstract class AbstractInlineCommentRenderContextFactory
extends ContentRenderContextFactory<CommentPayload> {
    private final CachedContentFinder cachedContentFinder;
    private final NotificationUserService notificationUserService;
    private final ContentEntityManager contentEntityManager;
    protected final LocaleManager localeManager;

    public AbstractInlineCommentRenderContextFactory(CachedContentFinder cachedContentFinder, NotificationUserService notificationUserService, ContentEntityManager contentEntityManager, LocaleManager localeManager) {
        this.cachedContentFinder = cachedContentFinder;
        this.notificationUserService = notificationUserService;
        this.contentEntityManager = contentEntityManager;
        this.localeManager = localeManager;
    }

    protected Maybe<NotificationContext> createForRecipient(Notification<CommentPayload> notification, ServerConfiguration serverConfiguration, RoleRecipient recipient) {
        CommentPayload payload = (CommentPayload)notification.getPayload();
        User recipientUser = this.notificationUserService.findUserForKey(recipient.getUserKey());
        Locale locale = this.localeManager.getLocale(recipientUser);
        ContentId contentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        Option maybeContent = this.cachedContentFinder.getContent(notification.getId(), notification.getKey(), locale, contentId, new Expansion[]{CommonContentExpansions.SPACE, this.cachedContentFinder.exportBody(), CommonContentExpansions.CONTAINER});
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOfException((Exception)new ContentException("Unable to find content with id [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", new Object[]{contentId, recipient.getUserKey()}));
        }
        User actionUser = this.notificationUserService.findUserForKey(recipientUser, notification.getOriginator());
        Content commentContent = (Content)maybeContent.get();
        String commentContentBody = ((ContentBody)commentContent.getBody().get(this.cachedContentFinder.exportRepresentation())).getValue();
        Container container = commentContent.getContainer();
        ContentEntityObject entity = Objects.requireNonNull(this.contentEntityManager.getById(contentId.asLong()));
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.setActor(actionUser);
        notificationContext.setRecipient(recipientUser);
        notificationContext.put("content", (Object)commentContent);
        notificationContext.put("inlineContext", (Object)entity.getProperties().getStringProperty("inline-original-selection"));
        notificationContext.put("contentHtml", (Object)commentContentBody);
        notificationContext.put("page", (Object)container);
        notificationContext.put("messageId", (Object)commentContent.getId().serialise());
        Map<String, ?> enhancedContext = this.enhanceNotificationContext(actionUser, commentContentBody, container, notification, entity);
        if (enhancedContext != null && !enhancedContext.isEmpty()) {
            notificationContext.putAll(enhancedContext);
        }
        return Option.some((Object)notificationContext);
    }

    @Nullable
    protected abstract Map<String, ?> enhanceNotificationContext(User var1, String var2, Container var3, Notification<CommentPayload> var4, ContentEntityObject var5);
}

