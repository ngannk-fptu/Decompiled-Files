/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.context.AbstractContentEditedRenderContextFactory;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import java.util.Map;

public abstract class AbstractCommentEditedRenderContextFactory
extends AbstractContentEditedRenderContextFactory {
    public AbstractCommentEditedRenderContextFactory(ContentService contentService, UserAccessor userAccessor, NotificationUserService notificationUserService) {
        super(contentService, userAccessor, notificationUserService);
    }

    @Override
    protected final Maybe<Map<String, Object>> checkedCreate(Notification<ContentEditedPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        Maybe<Map<String, Object>> maybe = super.checkedCreate(notification, serverConfiguration, roleRecipient);
        if (maybe.isDefined()) {
            Map context = (Map)maybe.get();
            Content content = (Content)context.get("content");
            context.put("page", content.getContainer());
            return Option.option((Object)context);
        }
        return maybe;
    }
}

