/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.follow.FollowManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractLikeEventRenderContextProvider
extends RenderContextProviderTemplate<LikePayload> {
    private final CachedContentFinder cachedContentFinder;
    private final UserAccessor userAccessor;
    private final FollowManager followManager;
    private final LocaleManager localeManager;

    public AbstractLikeEventRenderContextProvider(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, FollowManager followManager, LocaleManager localeManager) {
        this.cachedContentFinder = cachedContentFinder;
        this.userAccessor = userAccessor;
        this.followManager = followManager;
        this.localeManager = localeManager;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<LikePayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        Maybe<Map<String, Object>> maybeSubContext;
        if (roleRecipient.isEmpty() || ((Either)roleRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)("No RoleRecipient found in context provider [" + ((Object)((Object)this)).getClass().getSimpleName() + "]"), (Object[])new Object[0]);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        ConfluenceUser recipientUser = this.userAccessor.getUserByKey(recipient.getUserKey());
        NotificationContext context = new NotificationContext();
        LikePayload payload = (LikePayload)notification.getPayload();
        Locale locale = this.localeManager.getLocale((User)recipientUser);
        ContentId contentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        Option maybeContent = this.cachedContentFinder.getContent(notification.getId(), notification.getKey(), locale, contentId, this.getMediumSpecificExpansions(this.cachedContentFinder));
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOf((String)"No content found for notification", (Object[])new Object[0]);
        }
        Content content = (Content)maybeContent.get();
        UserKey userKey = new UserKey((String)payload.getOriginatingUserKey().get());
        ConfluenceUser liker = this.userAccessor.getUserByKey(userKey);
        boolean recipientIsAuthor = false;
        ConfluenceUser author = null;
        Person authorAsPerson = content.getHistory().getCreatedBy();
        if (authorAsPerson instanceof KnownUser) {
            Optional authorKey = authorAsPerson.optionalUserKey();
            Preconditions.checkArgument((boolean)authorKey.isPresent());
            author = this.userAccessor.getUserByKey((UserKey)authorKey.get());
            Objects.requireNonNull(author);
            recipientIsAuthor = author.getKey().equals((Object)recipient.getUserKey());
        }
        if ((maybeSubContext = this.getMediumSpecificContext(content, payload, recipientIsAuthor)) instanceof MaybeNot) {
            return maybeSubContext;
        }
        context.putAll((Map)maybeSubContext.get());
        context.put("content", (Object)content);
        context.put("modifier", (Object)liker);
        context.put("author", (Object)author);
        context.put("recipientIsAuthor", (Object)recipientIsAuthor);
        context.setManageNotificationOverridden(true);
        if (!recipientIsAuthor && this.followManager.isUserFollowing((User)recipientUser, (User)liker)) {
            context.setWatchType(Notification.WatchType.NETWORK);
        }
        return Option.some((Object)context.getMap());
    }

    protected abstract Expansion[] getMediumSpecificExpansions(CachedContentFinder var1);

    protected abstract Maybe<Map<String, Object>> getMediumSpecificContext(Content var1, LikePayload var2, boolean var3);
}

