/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.ContentException;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractContentCreatedRenderContextFactory
extends RenderContextProviderTemplate<ContentIdPayload> {
    private final UserAccessor userAccessor;
    private final LocaleManager localeManager;
    private final NotificationUserService notificationUserService;
    private final CachedContentFinder cachedContentFinder;

    public AbstractContentCreatedRenderContextFactory(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, LocaleManager localeManager, NotificationUserService notificationUserService) {
        this.cachedContentFinder = cachedContentFinder;
        this.userAccessor = userAccessor;
        this.localeManager = localeManager;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<ContentIdPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        if (roleRecipient.isEmpty() || ((Either)roleRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check.", (Object[])new Object[0]);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        Preconditions.checkArgument((AuthenticatedUserThreadLocal.get() != null && AuthenticatedUserThreadLocal.get().getKey().equals((Object)recipient.getUserKey()) ? 1 : 0) != 0, (String)"This factory requires the [%s] to be set to the recipient [%s] since the [%s] will perform a VIEW permission check in order to provide the content.", (Object)AuthenticatedUserThreadLocal.class.getName(), (Object)recipient, (Object)ContentService.class.getName());
        ConfluenceUser recipientUser = this.userAccessor.getExistingUserByKey(recipient.getUserKey());
        User originator = this.notificationUserService.findUserForKey((User)recipientUser, notification.getOriginator());
        Locale locale = this.localeManager.getLocale((User)recipientUser);
        ContentId contentId = ContentId.of((ContentType)((ContentIdPayload)notification.getPayload()).getContentType(), (long)((ContentIdPayload)notification.getPayload()).getContentId());
        Option maybeContent = this.cachedContentFinder.getContent(notification.getId(), notification.getKey(), locale, contentId, this.getMediumSpecificExpansions());
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOfException((Exception)new ContentException(String.format("Unable to find content with id [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", contentId, recipient.getUserKey()), new Object[0]));
        }
        Content content = (Content)maybeContent.get();
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.put("modifier", (Object)originator);
        notificationContext.put("content", (Object)content);
        notificationContext.putAll(this.getMediumSpecificContext(content));
        Maybe<Notification.WatchType> watchType = WatchTypeUtil.computeWatchTypeFrom(recipient.getRole());
        if (watchType.isDefined()) {
            notificationContext.setWatchType((Notification.WatchType)watchType.get());
        }
        long messageId = contentId.asLong();
        notificationContext.put("messageId", (Object)String.valueOf(messageId));
        return Option.some((Object)notificationContext.getMap());
    }

    protected abstract Expansion[] getMediumSpecificExpansions();

    protected abstract Map<String, Object> getMediumSpecificContext(Content var1);
}

