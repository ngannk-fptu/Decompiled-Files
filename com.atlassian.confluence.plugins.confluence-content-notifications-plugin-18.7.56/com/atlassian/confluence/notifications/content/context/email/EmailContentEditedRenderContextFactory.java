/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
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
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content.context.email;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.content.CommonContentExpansions;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.DiffContextProvider;
import com.atlassian.confluence.notifications.content.TransformerUtils;
import com.atlassian.confluence.notifications.content.context.AbstractContentEditedRenderContextFactory;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;

public class EmailContentEditedRenderContextFactory
extends AbstractContentEditedRenderContextFactory {
    private final DiffContextProvider diffContextProvider;

    public EmailContentEditedRenderContextFactory(ContentService contentService, UserAccessor userAccessor, NotificationUserService notificationUserService, DiffContextProvider diffContextProvider) {
        super(contentService, userAccessor, notificationUserService);
        this.diffContextProvider = diffContextProvider;
    }

    @Override
    public Expansion[] getMediumSpecificExpansions() {
        return new Expansion[]{CommonContentExpansions.CONTAINER, CommonContentExpansions.SPACE, CommonContentExpansions.VERSION};
    }

    @Override
    public Maybe<Map<String, Object>> getMediumSpecificContext(Notification<ContentEditedPayload> notification, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> roleRecipient, Content content) {
        ContentEditedPayload payload = (ContentEditedPayload)notification.getPayload();
        ContentId currentId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getContentId());
        ContentId originalId = ContentId.of((ContentType)payload.getContentType(), (long)payload.getOriginalId());
        Option recipient = roleRecipient.right().toOption().map(TransformerUtils.toUserKey());
        return Option.some(this.diffContextProvider.generateDiffContext(currentId, originalId, (Option<UserKey>)recipient));
    }
}

