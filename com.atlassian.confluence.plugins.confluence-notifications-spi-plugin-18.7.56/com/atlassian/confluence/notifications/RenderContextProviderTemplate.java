/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.ParticipantTemplate;
import com.atlassian.confluence.notifications.RenderContextProvider;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;

public abstract class RenderContextProviderTemplate<PAYLOAD extends NotificationPayload>
extends ParticipantTemplate<PAYLOAD>
implements RenderContextProvider<PAYLOAD> {
    public static final String CONTEXT_SOY_INJECTED_DATA = "soyInjectedData";

    @Override
    public Maybe<Map<String, Object>> create(Notification<PAYLOAD> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        this.performChecks(notification, serverConfiguration);
        return this.checkedCreate(notification, serverConfiguration, roleRecipient);
    }

    @Override
    public Map<String, Object> createMessageMetadata(Notification<PAYLOAD> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        return Collections.emptyMap();
    }

    private void performChecks(Notification<PAYLOAD> notification, ServerConfiguration serverConfiguration) {
        Preconditions.checkNotNull((Object)serverConfiguration, (Object)"Given serverConfiguration argument is null.");
        this.verifyPayloadMatches(notification);
    }

    protected abstract Maybe<Map<String, Object>> checkedCreate(Notification<PAYLOAD> var1, ServerConfiguration var2, Maybe<Either<NotificationAddress, RoleRecipient>> var3);
}

