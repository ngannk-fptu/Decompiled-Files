/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.ForgotPasswordPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ForgotPasswordNotificationRenderContextFactory
extends RenderContextProviderTemplate<ForgotPasswordPayload> {
    private final UserAccessor userAccessor;

    public ForgotPasswordNotificationRenderContextFactory(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<ForgotPasswordPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> addressData) {
        if (addressData.isEmpty()) {
            return MaybeNot.becauseOfNoResult(addressData);
        }
        if (((Either)addressData.get()).isRight()) {
            return MaybeNot.becauseOf((String)"forgot password notification must be sent to a direct medium, and not the user's preferences", (Object[])new Object[0]);
        }
        HashMap context = Maps.newHashMapWithExpectedSize((int)3);
        ForgotPasswordPayload payload = (ForgotPasswordPayload)notification.getPayload();
        Optional userKey = payload.getOriginatorUserKey();
        ConfluenceUser user = userKey.isPresent() ? this.userAccessor.getExistingUserByKey((UserKey)userKey.get()) : null;
        context.put("resetPasswordLink", payload.getResetPasswordLink());
        context.put("forgotPasswordLink", payload.getForgotPasswordLink());
        context.put("user", user);
        return Option.option((Object)context);
    }
}

