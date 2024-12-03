/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@ExperimentalApi
public interface RenderContextProvider<PAYLOAD extends NotificationPayload>
extends Participant<PAYLOAD> {
    public static final String OVERRIDE_SYSTEM_FROM_FIELD = "OVERRIDE_SYSTEM_FROM_FIELD";

    @Deprecated
    public Maybe<Map<String, Object>> create(Notification<PAYLOAD> var1, ServerConfiguration var2, Maybe<Either<NotificationAddress, RoleRecipient>> var3);

    default public Optional<Map<String, Object>> optionalCreate(Notification<PAYLOAD> notification, ServerConfiguration serverConfiguration, Optional<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        return Optional.ofNullable((Map)this.create(notification, serverConfiguration, (Maybe<Either<NotificationAddress, RoleRecipient>>)Option.option((Object)roleRecipient.orElse(null))).getOrNull());
    }

    public Map<String, Object> createMessageMetadata(Notification<PAYLOAD> var1, ServerConfiguration var2, Maybe<Either<NotificationAddress, RoleRecipient>> var3);

    default public Map<String, Object> createMessageOriginator(Notification<PAYLOAD> notification) {
        return Collections.emptyMap();
    }
}

