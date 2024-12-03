/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationPayload
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.google.common.base.Preconditions;
import java.util.Map;

public abstract class ContentRenderContextFactory<PAYLOAD extends NotificationPayload>
extends RenderContextProviderTemplate<PAYLOAD> {
    protected final Maybe<Map<String, Object>> checkedCreate(Notification<PAYLOAD> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> maybeRecipient) {
        if (maybeRecipient.isEmpty() || ((Either)maybeRecipient.get()).isLeft()) {
            return MaybeNot.becauseOf((String)"This factory exposes content, thus recipient has to be provided in order to perform a VIEW permission check. Got %s", (Object[])new Object[]{maybeRecipient});
        }
        RoleRecipient recipient = (RoleRecipient)((Either)maybeRecipient.get()).right().get();
        Preconditions.checkArgument((AuthenticatedUserThreadLocal.get() != null && AuthenticatedUserThreadLocal.get().getKey().equals((Object)recipient.getUserKey()) ? 1 : 0) != 0, (String)"This factory requires the [%s] to be set to the recipient since the [%s] will perform a VIEW permission check in order to provide the content.", AuthenticatedUserThreadLocal.class, ContentService.class);
        Maybe<NotificationContext> maybeContext = this.createForRecipient(notification, serverConfiguration, recipient);
        if (maybeContext.isDefined()) {
            NotificationContext context = (NotificationContext)maybeContext.get();
            Maybe<Notification.WatchType> watchType = WatchTypeUtil.computeWatchTypeFrom(recipient.getRole());
            if (watchType.isDefined()) {
                context.setWatchType((Notification.WatchType)watchType.get());
            }
            return Option.some((Object)context.getMap());
        }
        return MaybeNot.becauseOfNoResult(maybeContext);
    }

    protected abstract Maybe<NotificationContext> createForRecipient(Notification<PAYLOAD> var1, ServerConfiguration var2, RoleRecipient var3);
}

