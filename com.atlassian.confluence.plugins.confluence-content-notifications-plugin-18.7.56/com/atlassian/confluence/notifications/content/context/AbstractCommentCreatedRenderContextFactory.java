/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommentPayload;
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

public abstract class AbstractCommentCreatedRenderContextFactory
extends ContentRenderContextFactory<CommentPayload> {
    private final CachedContentFinder cachedContentFinder;
    private final LocaleManager localeManager;
    private NotificationUserService notificationUserService;

    public AbstractCommentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, NotificationUserService notificationUserService, LocaleManager localeManager) {
        this.cachedContentFinder = cachedContentFinder;
        this.notificationUserService = notificationUserService;
        this.localeManager = localeManager;
    }

    @Override
    protected Maybe<NotificationContext> createForRecipient(Notification<CommentPayload> notification, ServerConfiguration serverConfiguration, RoleRecipient recipient) {
        CommentPayload payload = (CommentPayload)notification.getPayload();
        NotificationContext notificationContext = new NotificationContext();
        User recipientUser = this.notificationUserService.findUserForKey(recipient.getUserKey());
        Locale locale = this.getLocale(recipientUser);
        ContentId contentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        Option maybeContent = this.cachedContentFinder.getContent(notification.getId(), notification.getKey(), locale, contentId, this.getMediumSpecificExpansions());
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOfException((Exception)new ContentException("Unable to find content with id [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", contentId, recipient.getUserKey()));
        }
        User originator = this.notificationUserService.findUserForKey(recipientUser, notification.getOriginator());
        Content commentContent = (Content)maybeContent.get();
        Container container = commentContent.getContainer();
        Space space = commentContent.getSpace();
        Maybe<Map<String, Object>> mediumSpecificContext = this.getMediumSpecificContext(notification, serverConfiguration, recipientUser, commentContent);
        if (!mediumSpecificContext.isDefined()) {
            return MaybeNot.becauseOfNoResult(mediumSpecificContext);
        }
        notificationContext.putAll((Map)mediumSpecificContext.get());
        long messageId = payload.getContainerId() > 0L ? payload.getContainerId() : payload.getContentId();
        notificationContext.put("modifier", (Object)originator);
        notificationContext.put("content", (Object)commentContent);
        notificationContext.put("space", (Object)space);
        notificationContext.put("page", (Object)container);
        notificationContext.put("messageId", (Object)String.valueOf(messageId));
        return Option.some((Object)notificationContext);
    }

    protected Locale getLocale(User recipientUser) {
        return this.localeManager.getLocale(recipientUser);
    }

    public abstract Expansion[] getMediumSpecificExpansions();

    public abstract Maybe<Map<String, Object>> getMediumSpecificContext(Notification<CommentPayload> var1, ServerConfiguration var2, User var3, Content var4);
}

