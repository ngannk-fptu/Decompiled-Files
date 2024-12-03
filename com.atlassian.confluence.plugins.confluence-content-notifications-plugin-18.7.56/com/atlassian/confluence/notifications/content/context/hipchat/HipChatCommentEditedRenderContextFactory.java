/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
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
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.notifications.content.context.hipchat;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.context.AbstractCommentEditedRenderContextFactory;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class HipChatCommentEditedRenderContextFactory
extends AbstractCommentEditedRenderContextFactory {
    public HipChatCommentEditedRenderContextFactory(ContentService contentService, UserAccessor userAccessor, NotificationUserService notificationUserService) {
        super(contentService, userAccessor, notificationUserService);
    }

    @Override
    public Expansion[] getMediumSpecificExpansions() {
        return new Expansion[]{CommonContentExpansions.CONTAINER, CommonContentExpansions.SPACE, CommonContentExpansions.VERSION};
    }

    @Override
    public Maybe<Map<String, Object>> getMediumSpecificContext(Notification<ContentEditedPayload> notification, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> roleRecipient, Content content) {
        return Option.some((Object)ImmutableMap.of());
    }
}

