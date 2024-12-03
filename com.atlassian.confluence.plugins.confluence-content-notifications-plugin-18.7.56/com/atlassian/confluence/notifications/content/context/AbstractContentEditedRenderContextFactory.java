/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
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

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.ContentException;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.notifications.content.context.MediumSpecificRenderContextProvider;
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
import java.util.Map;

public abstract class AbstractContentEditedRenderContextFactory
extends RenderContextProviderTemplate<ContentEditedPayload>
implements MediumSpecificRenderContextProvider<ContentEditedPayload> {
    private final ContentService contentService;
    private final UserAccessor userAccessor;
    private final NotificationUserService notificationUserService;

    public AbstractContentEditedRenderContextFactory(ContentService contentService, UserAccessor userAccessor, NotificationUserService notificationUserService) {
        this.contentService = contentService;
        this.userAccessor = userAccessor;
        this.notificationUserService = notificationUserService;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<ContentEditedPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        if (roleRecipient.isEmpty() || ((Either)roleRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check.", (Object[])new Object[0]);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        Preconditions.checkArgument((AuthenticatedUserThreadLocal.get() != null && AuthenticatedUserThreadLocal.get().getKey().equals((Object)recipient.getUserKey()) ? 1 : 0) != 0, (String)"This factory requires the [%s] to be set to the recipient since the [%s] will perform a VIEW permission check in order to provide the content.", AuthenticatedUserThreadLocal.class, ContentService.class);
        NotificationContext notificationContext = new NotificationContext();
        ContentEditedPayload payload = (ContentEditedPayload)notification.getPayload();
        ContentId contentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        ConfluenceUser recipientUser = this.userAccessor.getExistingUserByKey(recipient.getUserKey());
        User originator = this.notificationUserService.findUserForKey((User)recipientUser, notification.getOriginator());
        Option maybeContent = this.contentService.find(this.getMediumSpecificExpansions()).withId(contentId).fetchOne();
        if (maybeContent.isEmpty()) {
            return MaybeNot.becauseOfException((Exception)new ContentException("Unable to find content with id [%s], this might be because it does not exist or recipient [%s] does not have VIEW permission.", contentId, recipient.getUserKey()));
        }
        Content content = (Content)maybeContent.get();
        long messageId = payload.getContentId();
        notificationContext.put("messageId", (Object)String.valueOf(messageId));
        notificationContext.put("modifier", (Object)originator);
        notificationContext.put("content", (Object)content);
        Maybe mediumContext = this.getMediumSpecificContext(notification, serverConfiguration, (Either)roleRecipient.get(), content);
        if (!mediumContext.isDefined()) {
            return MaybeNot.becauseOfNoResult((Maybe)mediumContext);
        }
        notificationContext.putAll((Map)mediumContext.get());
        Maybe<Notification.WatchType> watchType = WatchTypeUtil.computeWatchTypeFrom(recipient.getRole());
        if (watchType.isDefined()) {
            notificationContext.setWatchType((Notification.WatchType)watchType.get());
        }
        return Option.some((Object)notificationContext.getMap());
    }
}

